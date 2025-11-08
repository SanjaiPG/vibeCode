package com.runanywhere.startup_hackathon20.data

import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.runanywhere.startup_hackathon20.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class Repository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()


    // Liked destinations state
    private val _likedDestinations = MutableStateFlow<Set<String>>(emptySet())
    val likedDestinations: StateFlow<Set<String>> = _likedDestinations.asStateFlow()

    // Liked plans state
    private val _likedPlans = MutableStateFlow<Set<String>>(emptySet())
    val likedPlans: StateFlow<Set<String>> = _likedPlans.asStateFlow()

    // Plans version for triggering recomposition
    private val _plansVersion = MutableStateFlow(0)
    val plansVersion: StateFlow<Int> = _plansVersion.asStateFlow()

    // In-memory cache for plans
    private val plansCache = mutableListOf<Plan>()

    init {
        viewModelScope.launch {
            auth.currentUser?.let { firebaseUser ->

                // ✅ NEW: Auto-create profile if missing
                ensureUserProfileExists(firebaseUser)

                // ✅ Then load normally
                loadUserProfile(firebaseUser.uid)
                loadLikedDestinations(firebaseUser.uid)

                loadLikedPlans(firebaseUser.uid)
            }
        }
    }
    /**
     * Update user profile (called from ProfileScreen)
     */
    fun getPlanById(planId: String): Plan? {
        return plansCache.find { it.id == planId }
    }

    fun updateUser(user: User) {
        _currentUser.value = user

        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                saveUserProfile(
                    uid = firebaseUser.uid,
                    email = user.email,
                    displayName = user.name,
                    countryCode = user.countryCode,
                    phone = user.phone
                )

                // ✅ FIX: reload user after saving
                loadUserProfile(firebaseUser.uid)
            }
        }
    }


    private suspend fun ensureUserProfileExists(firebaseUser: FirebaseUser) {
        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()

        if (!userDoc.exists()) {
            val autoName = firebaseUser.displayName ?: ""
            val autoEmail = firebaseUser.email ?: ""
            val autoPhone = firebaseUser.phoneNumber ?: ""
            val autoCountry = "+91" // You can improve later


            saveUserProfile(
                uid = firebaseUser.uid,
                email = autoEmail,
                displayName = autoName,
                countryCode = autoCountry,
                phone = autoPhone
            )
        }
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Get current logged-in user
     */
    fun getCurrentUser(): User? = _currentUser.value

    /**
     * Save user profile data to Firebase after registration/login
     */
    suspend fun saveUserProfile(
        uid: String,
        email: String,
        displayName: String,
        countryCode: String,
        phone: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userRef = firestore.collection("users").document(uid)
            val userData = hashMapOf(
                "email" to email,
                "displayName" to displayName,
                "countryCode" to countryCode,
                "phone" to phone,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            // Use merge to update existing fields or create new document
            userRef.set(userData, com.google.firebase.firestore.SetOptions.merge()).await()

            // Update local cache
            _currentUser.value = User(
                username = email.substringBefore("@"),
                name = displayName,
                email = email,
                countryCode = countryCode,
                phone = phone
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Repository", "Error saving user profile", e)
            Result.failure(e)
        }
    }

    /**
     * Load user profile from Firebase
     */
    suspend fun loadUserProfile(uid: String): User? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(uid).get().await()

            if (doc.exists()) {
                val email = doc.getString("email") ?: ""
                val displayName = doc.getString("displayName") ?: ""
                val countryCode = doc.getString("countryCode") ?: "+91"
                val phone = doc.getString("phone") ?: ""

                val user = User(
                    username = email.substringBefore("@"),
                    name = displayName,
                    email = email,
                    countryCode = countryCode,
                    phone = phone
                )

                // Update local cache
                _currentUser.value = user
                user
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error loading user profile", e)
            null
        }
    }


    /**
     * Update user password
     */
    fun updatePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        // In a real app, this would verify with Firebase Auth
        // For now, return true for demo purposes
        return true
    }

    /**
     * Clear user data on logout
     */
    fun clearUserData() {
        _currentUser.value = null
        _likedDestinations.value = emptySet()
        _likedPlans.value = emptySet()
        plansCache.clear()
        _plansVersion.value = 0
    }

    // ==================== DESTINATIONS ====================

    /**
     * Get all popular destinations (mock data)
     */
    fun getPopularDestinations(): List<Destination> {
        return listOf(
            Destination(
                id = "paris",
                name = "Paris",
                country = "France",
                lat = 48.8566,
                lng = 2.3522,
                imageUrl = "",
                currencyCode = "EUR",
                rating = 4.8,
                reviewCount = 15234,
                description = "The City of Light beckons with its iconic Eiffel Tower, world-class museums, and romantic atmosphere. Stroll along the Seine, explore charming neighborhoods, and indulge in exquisite French cuisine.",
                hotels = listOf(
                    Hotel(
                        "Hotel Eiffel Trocadéro",
                        4.7,
                        "€200/night",
                        listOf("WiFi", "Breakfast", "City View"),
                        "🏨"
                    ),
                    Hotel(
                        "Paris Marriott Opera",
                        4.9,
                        "€350/night",
                        listOf("WiFi", "Spa", "Restaurant", "Fitness Center"),
                        "🏨"
                    ),
                    Hotel(
                        "Hotel des Grands Boulevards",
                        4.5,
                        "€180/night",
                        listOf("WiFi", "Bar", "Restaurant"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Le Jules Verne", 4.8, "French Fine Dining", "€€€€", "🍽️"),
                    Restaurant("L'Atelier Saint-Germain", 4.6, "Modern French", "€€€", "🍽️"),
                    Restaurant("Bistrot Paul Bert", 4.4, "Traditional Bistro", "€€", "🍽️")
                ),
                topReviews = listOf(
                    Review("Emma", 5, "Absolutely magical! The city exceeded all expectations.", "2024-01-15", "👩"),
                    Review("James", 5, "A dream destination. Will definitely return!", "2024-01-10", "👨")
                )
            ),
            Destination(
                id = "tokyo",
                name = "Tokyo",
                country = "Japan",
                lat = 35.6762,
                lng = 139.6503,
                imageUrl = "",
                currencyCode = "JPY",
                rating = 4.9,
                reviewCount = 18567,
                description = "Experience the perfect blend of ancient traditions and cutting-edge technology. From serene temples to neon-lit streets, Tokyo offers an unforgettable journey through Japanese culture.",
                hotels = listOf(
                    Hotel(
                        "The Peninsula Tokyo",
                        4.8,
                        "¥45000/night",
                        listOf("WiFi", "Spa", "Imperial Palace View", "Fine Dining"),
                        "🏨"
                    ),
                    Hotel(
                        "Shibuya Excel Hotel",
                        4.7,
                        "¥25000/night",
                        listOf("WiFi", "City View", "Restaurant", "24h Front Desk"),
                        "🏨"
                    ),
                    Hotel(
                        "Asakusa View Hotel",
                        4.6,
                        "¥18000/night",
                        listOf("WiFi", "Traditional Design", "Temple Views"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Sukiyabashi Jiro", 5.0, "Sushi Omakase", "¥¥¥¥", "🍣"),
                    Restaurant("Ichiran Ramen Shibuya", 4.6, "Tonkotsu Ramen", "¥¥", "🍜"),
                    Restaurant("Nabezo Shibuya", 4.3, "All-You-Can-Eat Shabu-shabu", "¥¥¥", "🥩")
                ),
                topReviews = listOf(
                    Review("Yuki", 5, "The food, culture, and hospitality are unmatched!", "2024-01-20", "👩"),
                    Review("Michael", 5, "Tokyo is a city like no other. Highly recommend!", "2024-01-18", "👨")
                )
            ),
            Destination(
                id = "bali",
                name = "Bali",
                country = "Indonesia",
                lat = -8.3405,
                lng = 115.0920,
                imageUrl = "",
                currencyCode = "IDR",
                rating = 4.7,
                reviewCount = 12890,
                description = "A tropical paradise with stunning beaches, lush rice terraces, and spiritual temples. Perfect for relaxation, adventure, and cultural immersion in the Island of the Gods.",
                hotels = listOf(
                    Hotel(
                        "COMO Uma Ubud",
                        4.8,
                        "Rp2500000/night",
                        listOf("Infinity Pool", "Spa", "Yoga Studio", "Tropical Garden"),
                        "🏨"
                    ),
                    Hotel(
                        "W Bali Seminyak",
                        4.6,
                        "Rp3200000/night",
                        listOf("Beach Access", "Pool Party", "Spa", "Fine Dining"),
                        "🏨"
                    ),
                    Hotel(
                        "The Kayon Jungle Resort",
                        4.9,
                        "Rp1800000/night",
                        listOf("Jungle View", "Private Pool", "Spa", "Organic Restaurant"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Locavore", 4.9, "Contemporary Indonesian", "Rp Rp Rp Rp", "🍽️"),
                    Restaurant("Mama San", 4.5, "Pan-Asian Fusion", "Rp Rp Rp", "🍽️"),
                    Restaurant("Warung Biah Biah", 4.6, "Traditional Balinese", "Rp Rp", "🍽️")
                ),
                topReviews = listOf(
                    Review("Sarah", 5, "Paradise on earth! The beaches are breathtaking.", "2024-01-22", "👩"),
                    Review("David", 4, "Great for relaxation and adventure activities.", "2024-01-19", "👨")
                )
            ),
            Destination(
                id = "newyork",
                name = "New York",
                country = "United States",
                lat = 40.7128,
                lng = -74.0060,
                imageUrl = "",
                currencyCode = "USD",
                rating = 4.6,
                reviewCount = 23456,
                description = "The city that never sleeps offers Broadway shows, world-class museums, iconic landmarks, and endless dining options. Experience the energy of Manhattan and explore diverse neighborhoods.",
                hotels = listOf(
                    Hotel(
                        "The Plaza",
                        4.8,
                        "$450/night",
                        listOf("Luxury", "Central Park View", "Spa", "Fine Dining"),
                        "🏨"
                    ),
                    Hotel(
                        "Pod Hotels Times Square",
                        4.3,
                        "$200/night",
                        listOf("Modern Design", "Rooftop Bar", "WiFi"),
                        "🏨"
                    ),
                    Hotel(
                        "1 Hotels Brooklyn Bridge",
                        4.7,
                        "$320/night",
                        listOf("Eco-Friendly", "Waterfront View", "Organic Restaurant"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Le Bernardin", 4.9, "French Seafood", "$$$$", "🐟"),
                    Restaurant("Joe's Pizza", 4.4, "New York Pizza", "$", "🍕"),
                    Restaurant("Peter Luger Steak House", 4.6, "American Steakhouse", "$$$", "🥩")
                ),
                topReviews = listOf(
                    Review(
                        "Alex",
                        5,
                        "Incredible energy and so much to see and do!",
                        "2024-01-25",
                        "👨"
                    ),
                    Review(
                        "Maria",
                        4,
                        "Amazing city but can be overwhelming. Plan ahead!",
                        "2024-01-23",
                        "👩"
                    )
                )
            ),
            Destination(
                id = "london",
                name = "London",
                country = "United Kingdom",
                lat = 51.5074,
                lng = -0.1278,
                imageUrl = "",
                currencyCode = "GBP",
                rating = 4.5,
                reviewCount = 19876,
                description = "A perfect blend of historic charm and modern innovation. Explore royal palaces, world-class museums, charming pubs, and beautiful parks in this quintessentially British capital.",
                hotels = listOf(
                    Hotel(
                        "The Savoy",
                        4.9,
                        "£400/night",
                        listOf("Historic Luxury", "Thames View", "Afternoon Tea", "Spa"),
                        "🏨"
                    ),
                    Hotel(
                        "Premier Inn London City",
                        4.2,
                        "£80/night",
                        listOf("Central Location", "WiFi", "Family Rooms"),
                        "🏨"
                    ),
                    Hotel(
                        "The Shard Hotel",
                        4.7,
                        "£250/night",
                        listOf("Skyline Views", "Modern Design", "Fine Dining"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Dishoom", 4.7, "Indian Bombay Café", "££", "🍛"),
                    Restaurant("Sketch", 4.5, "Contemporary European", "££££", "🍽️"),
                    Restaurant("Borough Market", 4.6, "Food Market", "£", "🥪")
                ),
                topReviews = listOf(
                    Review(
                        "Oliver",
                        5,
                        "Rich history and fantastic museums. Love the pub culture!",
                        "2024-01-28",
                        "👨"
                    ),
                    Review(
                        "Sophie",
                        4,
                        "Great city but weather can be unpredictable. Bring an umbrella!",
                        "2024-01-26",
                        "👩"
                    )
                )
            ),
            Destination(
                id = "dubai",
                name = "Dubai",
                country = "United Arab Emirates",
                lat = 25.2048,
                lng = 55.2708,
                imageUrl = "",
                currencyCode = "AED",
                rating = 4.7,
                reviewCount = 16543,
                description = "A futuristic oasis in the desert featuring towering skyscrapers, luxury shopping, pristine beaches, and world-class dining. Experience the perfect blend of tradition and innovation.",
                hotels = listOf(
                    Hotel(
                        "Burj Al Arab",
                        5.0,
                        "AED 4500/night",
                        listOf(
                            "7-Star Luxury",
                            "Private Beach",
                            "Butler Service",
                            "Helicopter Transfer"
                        ),
                        "🏨"
                    ),
                    Hotel(
                        "Atlantis The Palm",
                        4.8,
                        "AED 1200/night",
                        listOf("Waterpark", "Aquarium", "Beach Access", "Multiple Restaurants"),
                        "🏨"
                    ),
                    Hotel(
                        "Jumeirah Beach Hotel",
                        4.6,
                        "AED 800/night",
                        listOf("Beach Front", "Water Sports", "Spa", "Kids Club"),
                        "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant("Nobu Dubai", 4.8, "Japanese Fine Dining", "AED AED AED AED", "🍣"),
                    Restaurant("Al Hadheerah", 4.5, "Traditional Emirati", "AED AED AED", "🐪"),
                    Restaurant("La Petite Maison", 4.7, "French Mediterranean", "AED AED AED", "🍽️")
                ),
                topReviews = listOf(
                    Review(
                        "Ahmed",
                        5,
                        "Incredible architecture and luxury shopping. Desert safari was amazing!",
                        "2024-01-30",
                        "👨"
                    ),
                    Review(
                        "Lisa",
                        4,
                        "Modern marvel but can be expensive. Book activities in advance.",
                        "2024-01-29",
                        "👩"
                    )
                )
            )
        )
    }

    /**
     * Get destination by ID
     */
    fun getDestinationById(id: String): Destination? {
        return getPopularDestinations().find { it.id == id }
    }

    // ==================== LIKED DESTINATIONS ====================

    /**
     * Load liked destinations from Firebase
     */
    private suspend fun loadLikedDestinations(uid: String) = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("likedDestinations")
                .get()
                .await()

            val likedIds = snapshot.documents.map { it.id }.toSet()
            _likedDestinations.value = likedIds
        } catch (e: Exception) {
            Log.e("Repository", "Error loading liked destinations", e)
        }
    }

    private suspend fun loadLikedPlans(uid: String) = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("likedPlans")
                .get()
                .await()

            val likedIds = snapshot.documents.map { it.id }.toSet()
            _likedPlans.value = likedIds
        } catch (e: Exception) {
            Log.e("Repository", "Error loading liked plans", e)
        }
    }

    /**
     * Like a destination
     */
    fun likeDestination(destinationId: String) {
        _likedDestinations.value = _likedDestinations.value + destinationId

        // Save to Firebase
        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("likedDestinations")
                        .document(destinationId)
                        .set(hashMapOf("likedAt" to com.google.firebase.Timestamp.now()))
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error liking destination", e)
                }
            }
        }
    }

    /**
     * Unlike a destination
     */
    fun unlikeDestination(destinationId: String) {
        _likedDestinations.value = _likedDestinations.value - destinationId

        // Remove from Firebase
        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("likedDestinations")
                        .document(destinationId)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error unliking destination", e)
                }
            }
        }
    }

    // ==================== PLANS ====================

    /**
     * Get all plans
     */
    fun getAllPlans(): List<Plan> = plansCache.toList()

    /**
     * Add a new plan
     */
    fun addPlan(plan: Plan) {
        plansCache.add(plan)
        _plansVersion.value++

        // Save to Firebase
        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("plans")
                        .document(plan.id)
                        .set(plan)
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error saving plan", e)
                }
            }
        }
    }

    fun likePlan(planId: String) {
        _likedPlans.value = _likedPlans.value + planId
        _plansVersion.value++ // trigger recomposition

        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("likedPlans")
                        .document(planId)
                        .set(hashMapOf("likedAt" to com.google.firebase.Timestamp.now()))
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error liking plan", e)
                }
            }
        }
    }

    fun unlikePlan(planId: String) {
        _likedPlans.value = _likedPlans.value - planId
        _plansVersion.value++

        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("likedPlans")
                        .document(planId)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error unliking plan", e)
                }
            }
        }
    }

    /**
     * Delete a plan
     */
    fun deletePlan(planId: String) {
        plansCache.removeAll { it.id == planId }
        _likedPlans.value = _likedPlans.value - planId
        _plansVersion.value++

        // Delete from Firebase
        auth.currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("plans")
                        .document(planId)
                        .delete()
                        .await()
                } catch (e: Exception) {
                    Log.e("Repository", "Error deleting plan", e)
                }
            }
        }
    }
}