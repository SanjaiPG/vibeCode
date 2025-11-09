package com.runanywhere.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.runanywhere.data.model.Destination
import com.runanywhere.data.model.Hotel
import com.runanywhere.data.model.Plan
import com.runanywhere.data.model.Restaurant
import com.runanywhere.data.model.Review
import com.runanywhere.data.model.User
import com.runanywhere.startup_hackathon20.data.firebase.FirestoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository for providing destination data and managing user preferences
 */
object Repository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firestoreManager = FirestoreManager()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    // SharedPreferences for local storage
    private var prefs: SharedPreferences? = null
    private const val PREFS_NAME = "travel_app_prefs"
    private const val KEY_LIKED_DESTINATIONS = "liked_destinations"
    private const val KEY_LIKED_PLANS = "liked_plans"
    private const val KEY_PLANS = "plans"
    private const val KEY_CURRENT_USER = "current_user"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    // Helper to get user-specific key
    private fun getUserKey(baseKey: String): String {
        val userId =
            auth.currentUser?.uid ?: prefs?.getString(KEY_CURRENT_USER_ID, null) ?: "default"
        return "${baseKey}_${userId}"
    }

    // State flows for reactive data
    private val _likedDestinations = MutableStateFlow<Set<String>>(emptySet())
    val likedDestinations: StateFlow<Set<String>> = _likedDestinations

    private val _likedPlans = MutableStateFlow<Set<String>>(emptySet())
    val likedPlans: StateFlow<Set<String>> = _likedPlans

    private val _plansVersion = MutableStateFlow(0)
    val plansVersion: StateFlow<Int> = _plansVersion

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // In-memory cache for plans
    private val plansCache = mutableListOf<Plan>()

    // In-memory storage for user credentials (for demo purposes)
    private val userCredentials = mutableMapOf<String, String>() // username -> password
    private val users = mutableMapOf<String, User>() // username -> User

    /**
     * Initialize the repository with application context
     * Call this from Application.onCreate()
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Also try to load from Firebase if user is authenticated
        auth.currentUser?.let { user ->
            // Save current user ID to preferences
            prefs?.edit()?.apply {
                putString(KEY_CURRENT_USER_ID, user.uid)
                apply()
            }

            // Load data from local storage for this user
            loadFromLocalStorage()

            repositoryScope.launch {
                loadLikedDestinations(user.uid)
                loadLikedPlans(user.uid)
                loadPlans(user.uid)
                loadCurrentUser(user.uid)
            }
        } ?: run {
            // No user logged in, load generic data
            loadFromLocalStorage()
        }
    }

    /**
     * Load all data from local SharedPreferences storage
     */
    private fun loadFromLocalStorage() {
        try {
            prefs?.let { sp ->
                // Load liked destinations
                val likedDestSet =
                    sp.getStringSet(getUserKey(KEY_LIKED_DESTINATIONS), emptySet()) ?: emptySet()
                _likedDestinations.value = likedDestSet
                Log.d(
                    "Repository",
                    "Loaded ${likedDestSet.size} liked destinations from local storage"
                )

                // Load liked plans
                val likedPlanSet =
                    sp.getStringSet(getUserKey(KEY_LIKED_PLANS), emptySet()) ?: emptySet()
                _likedPlans.value = likedPlanSet
                Log.d("Repository", "Loaded ${likedPlanSet.size} liked plans from local storage")

                // Load plans
                val plansJson = sp.getString(getUserKey(KEY_PLANS), null)
                if (plansJson != null) {
                    val type = object : TypeToken<List<Plan>>() {}.type
                    val loadedPlans: List<Plan> = gson.fromJson(plansJson, type)
                    plansCache.clear()
                    plansCache.addAll(loadedPlans)
                    _plansVersion.value++
                    Log.d("Repository", "Loaded ${loadedPlans.size} plans from local storage")
                }

                // Load current user
                val userJson = sp.getString(getUserKey(KEY_CURRENT_USER), null)
                if (userJson != null) {
                    val user: User = gson.fromJson(userJson, User::class.java)
                    _currentUser.value = user
                    users[user.username] = user
                    Log.d("Repository", "Loaded user ${user.username} from local storage")
                }
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error loading from local storage", e)
        }
    }

    /**
     * Save liked destinations to local storage
     */
    private fun saveLikedDestinationsToLocal() {
        try {
            prefs?.edit()?.apply {
                putStringSet(getUserKey(KEY_LIKED_DESTINATIONS), _likedDestinations.value)
                apply()
            }
            Log.d(
                "Repository",
                "Saved ${_likedDestinations.value.size} liked destinations to local storage"
            )
        } catch (e: Exception) {
            Log.e("Repository", "Error saving liked destinations to local storage", e)
        }
    }

    /**
     * Save liked plans to local storage
     */
    private fun saveLikedPlansToLocal() {
        try {
            prefs?.edit()?.apply {
                putStringSet(getUserKey(KEY_LIKED_PLANS), _likedPlans.value)
                apply()
            }
            Log.d("Repository", "Saved ${_likedPlans.value.size} liked plans to local storage")
        } catch (e: Exception) {
            Log.e("Repository", "Error saving liked plans to local storage", e)
        }
    }

    /**
     * Save plans to local storage
     */
    private fun savePlansToLocal() {
        try {
            val plansJson = gson.toJson(plansCache)
            prefs?.edit()?.apply {
                putString(getUserKey(KEY_PLANS), plansJson)
                apply()
            }
            Log.d("Repository", "Saved ${plansCache.size} plans to local storage")
        } catch (e: Exception) {
            Log.e("Repository", "Error saving plans to local storage", e)
        }
    }

    /**
     * Save current user to local storage
     */
    private fun saveCurrentUserToLocal() {
        try {
            val userJson = gson.toJson(_currentUser.value)
            prefs?.edit()?.apply {
                putString(getUserKey(KEY_CURRENT_USER), userJson)
                apply()
            }
            Log.d("Repository", "Saved current user to local storage")
        } catch (e: Exception) {
            Log.e("Repository", "Error saving current user to local storage", e)
        }
    }

    /**
     * Call this when user logs in to load their data
     */
    fun onUserLogin(uid: String) {
        // Save current user ID to preferences
        prefs?.edit()?.apply {
            putString(KEY_CURRENT_USER_ID, uid)
            apply()
        }

        repositoryScope.launch {
            loadLikedDestinations(uid)
            loadLikedPlans(uid)
            loadPlans(uid)
            loadCurrentUser(uid)
        }
    }

    /**
     * Call this when user logs out to clear data
     */
    fun onUserLogout() {
        // Clear current user ID from preferences
        prefs?.edit()?.apply {
            remove(KEY_CURRENT_USER_ID)
            apply()
        }

        _likedDestinations.value = emptySet()
        _likedPlans.value = emptySet()
        _currentUser.value = null
        plansCache.clear()
        _plansVersion.value++
        saveLikedDestinationsToLocal()
        saveLikedPlansToLocal()
        savePlansToLocal()
        saveCurrentUserToLocal()
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Load current user from Firebase or in-memory storage
     */
    private suspend fun loadCurrentUser(uid: String) = withContext(Dispatchers.IO) {
        try {
            val user = firestoreManager.getCurrentUser(uid)
            _currentUser.value = user
            users[user.username] = user
            saveCurrentUserToLocal()
            Log.d("Repository", "Loaded user from Firestore: ${user.name} (${user.email})")
        } catch (e: Exception) {
            Log.e("Repository", "Error loading current user", e)
            // If Firebase fails, try to create from Firebase Auth
            auth.currentUser?.let { firebaseUser ->
                val user = User(
                    username = firebaseUser.email?.substringBefore("@") ?: firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    countryCode = "+91",
                    phone = ""
                )
                _currentUser.value = user
                users[user.username] = user
                saveCurrentUserToLocal()
            }
        }
    }

    /**
     * Get current user (synchronous)
     */
    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    /**
     * Update user information
     */
    fun updateUser(user: User) {
        _currentUser.value = user
        users[user.username] = user
        saveCurrentUserToLocal()

        // Save to Firebase
        auth.currentUser?.let { firebaseUser ->
            repositoryScope.launch {
                try {
                    firestoreManager.updateUser(firebaseUser.uid, user)
                    Log.d("Repository", "User updated successfully in Firestore: ${user.name}")
                } catch (e: Exception) {
                    Log.e("Repository", "Error updating user in Firestore", e)
                }
            }
        }
        saveCurrentUserToLocal()
    }

    /**
     * Register a new user (for demo purposes)
     */
    fun registerUser(username: String, password: String, user: User): Boolean {
        return if (!userCredentials.containsKey(username)) {
            userCredentials[username] = password
            users[username] = user
            _currentUser.value = user
            saveCurrentUserToLocal()
            true
        } else {
            false
        }
    }

    /**
     * Login user (for demo purposes)
     */
    fun loginUser(username: String, password: String): Boolean {
        return if (userCredentials[username] == password) {
            _currentUser.value = users[username]
            saveCurrentUserToLocal()
            true
        } else {
            false
        }
    }

    /**
     * Update user password with Firebase Auth
     */
    suspend fun updatePassword(oldPassword: String, newPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                if (currentUser == null || currentUser.email == null) {
                    return@withContext Result.failure(Exception("No user logged in"))
                }

                // Re-authenticate user with old password
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
                    currentUser.email!!,
                    oldPassword
                )

                // Re-authenticate before changing password
                currentUser.reauthenticate(credential).await()

                // Update password
                currentUser.updatePassword(newPassword).await()

                Log.d("Repository", "Password updated successfully in Firebase")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("Repository", "Error updating password", e)
                Result.failure(e)
            }
        }

    // ==================== DESTINATIONS ====================

    /**
     * Get all popular destinations (mock data)
     */
    fun getPopularDestinations(): List<Destination> {
        return listOf(
            Destination(
                id = "tokyo",
                name = "Tokyo",
                country = "Japan",
                lat = 35.6762,
                lng = 139.6503,
                description = "A vibrant metropolis blending tradition and modernity",
                imageUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf",
                currencyCode = "JPY",
                rating = 4.9,
                reviewCount = 15678,
                hotels = listOf(
                    Hotel(
                        name = "Park Hyatt Tokyo",
                        rating = 4.9,
                        pricePerNight = "¥60,000",
                        amenities = listOf("City Views", "Pool", "Spa", "Fine Dining"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Shinjuku Prince Hotel",
                        rating = 4.5,
                        pricePerNight = "¥15,000",
                        amenities = listOf("WiFi", "Restaurant", "Metro Access", "Business Center"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Asakusa View Hotel",
                        rating = 4.3,
                        pricePerNight = "¥12,000",
                        amenities = listOf(
                            "Skytree View",
                            "Onsen",
                            "Traditional Area",
                            "Breakfast"
                        ),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Sukiyabashi Jiro",
                        rating = 4.9,
                        cuisine = "Sushi",
                        priceRange = "¥¥¥¥",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Ichiran Ramen",
                        rating = 4.6,
                        cuisine = "Ramen",
                        priceRange = "¥",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Tsukiji Sushiko",
                        rating = 4.7,
                        cuisine = "Seafood",
                        priceRange = "¥¥",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "bali",
                name = "Bali",
                country = "Indonesia",
                lat = -8.3405,
                lng = 115.0920,
                description = "Tropical paradise with stunning beaches and temples",
                imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4",
                currencyCode = "IDR",
                rating = 4.7,
                reviewCount = 9876,
                hotels = listOf(
                    Hotel(
                        name = "COMO Uma Ubud",
                        rating = 4.8,
                        pricePerNight = "Rp 4,500,000",
                        amenities = listOf("Infinity Pool", "Spa", "Yoga", "Rice Field Views"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Potato Head Beach Club",
                        rating = 4.6,
                        pricePerNight = "Rp 3,200,000",
                        amenities = listOf("Beachfront", "Pool", "Restaurants", "Nightlife"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Seminyak Beach Resort",
                        rating = 4.4,
                        pricePerNight = "Rp 1,800,000",
                        amenities = listOf("Beach Access", "Pool", "Spa", "WiFi"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Locavore",
                        rating = 4.9,
                        cuisine = "Modern Indonesian",
                        priceRange = "Rp Rp Rp Rp",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Warung Biah Biah",
                        rating = 4.5,
                        cuisine = "Balinese",
                        priceRange = "Rp",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Mozaic Restaurant",
                        rating = 4.8,
                        cuisine = "French-Indonesian Fusion",
                        priceRange = "Rp Rp Rp",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "newyork",
                name = "New York",
                country = "USA",
                lat = 40.7128,
                lng = -74.0060,
                description = "The city that never sleeps, full of energy and diversity",
                imageUrl = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9",
                currencyCode = "USD",
                rating = 4.6,
                reviewCount = 18765,
                hotels = listOf(
                    Hotel(
                        name = "The Plaza Hotel",
                        rating = 4.8,
                        pricePerNight = "$850",
                        amenities = listOf(
                            "Central Park View",
                            "Spa",
                            "Michelin Dining",
                            "Concierge"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Pod Times Square",
                        rating = 4.3,
                        pricePerNight = "$180",
                        amenities = listOf("Rooftop Bar", "WiFi", "Modern Rooms", "Prime Location"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Hotel Beacon",
                        rating = 4.4,
                        pricePerNight = "$250",
                        amenities = listOf("Kitchen", "Upper West Side", "WiFi", "Breakfast"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Eleven Madison Park",
                        rating = 4.9,
                        cuisine = "Contemporary American",
                        priceRange = "$$$$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Joe's Pizza",
                        rating = 4.5,
                        cuisine = "Pizza",
                        priceRange = "$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Katz's Delicatessen",
                        rating = 4.6,
                        cuisine = "Deli",
                        priceRange = "$$",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "rome",
                name = "Rome",
                country = "Italy",
                lat = 41.9028,
                lng = 12.4964,
                description = "Ancient city with incredible history and cuisine",
                imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5",
                currencyCode = "EUR",
                rating = 4.8,
                reviewCount = 11234,
                hotels = listOf(
                    Hotel(
                        name = "Hotel Hassler Roma",
                        rating = 4.9,
                        pricePerNight = "€650",
                        amenities = listOf(
                            "Spanish Steps View",
                            "Michelin Restaurant",
                            "Spa",
                            "Rooftop"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Hotel Campo de' Fiori",
                        rating = 4.5,
                        pricePerNight = "€200",
                        amenities = listOf("Terrace", "Historic Center", "WiFi", "Breakfast"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Trastevere Boutique",
                        rating = 4.4,
                        pricePerNight = "€130",
                        amenities = listOf(
                            "WiFi",
                            "Charming District",
                            "Local Restaurants",
                            "Breakfast"
                        ),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "La Pergola",
                        rating = 4.9,
                        cuisine = "Italian Fine Dining",
                        priceRange = "€€€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Trattoria Da Enzo",
                        rating = 4.7,
                        cuisine = "Traditional Roman",
                        priceRange = "€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Pizzarium",
                        rating = 4.6,
                        cuisine = "Pizza al Taglio",
                        priceRange = "€",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "maldives",
                name = "Maldives",
                country = "Maldives",
                lat = 3.2028,
                lng = 73.2207,
                description = "Crystal clear waters and luxury resorts",
                imageUrl = "https://images.unsplash.com/photo-1514282401047-d79a71a590e8",
                currencyCode = "MVR",
                rating = 4.9,
                reviewCount = 8765,
                hotels = listOf(
                    Hotel(
                        name = "Soneva Fushi",
                        rating = 5.0,
                        pricePerNight = "$1,200",
                        amenities = listOf("Private Villa", "Overwater", "Butler Service", "Spa"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Conrad Maldives",
                        rating = 4.8,
                        pricePerNight = "$800",
                        amenities = listOf(
                            "Underwater Restaurant",
                            "Snorkeling",
                            "Spa",
                            "All-Inclusive"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Paradise Island Resort",
                        rating = 4.5,
                        pricePerNight = "$350",
                        amenities = listOf("Beach Access", "Diving Center", "Pool", "WiFi"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Ithaa Undersea",
                        rating = 4.9,
                        cuisine = "Contemporary",
                        priceRange = "$$$$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Sea.Fire.Salt",
                        rating = 4.7,
                        cuisine = "Seafood Grill",
                        priceRange = "$$$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Sunset Grill",
                        rating = 4.6,
                        cuisine = "Maldivian",
                        priceRange = "$$",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "london",
                name = "London",
                country = "UK",
                lat = 51.5074,
                lng = -0.1278,
                description = "Historic capital with royal heritage and modern culture",
                imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad",
                currencyCode = "GBP",
                rating = 4.7,
                reviewCount = 14567,
                hotels = listOf(
                    Hotel(
                        name = "The Savoy",
                        rating = 4.9,
                        pricePerNight = "£500",
                        amenities = listOf("Thames View", "Art Deco", "Afternoon Tea", "Spa"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "CitizenM Tower of London",
                        rating = 4.5,
                        pricePerNight = "£150",
                        amenities = listOf("Modern Design", "Rooftop Bar", "WiFi", "24/7 Food"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Premier Inn London",
                        rating = 4.2,
                        pricePerNight = "£90",
                        amenities = listOf("Breakfast", "WiFi", "Central Location", "Comfy Beds"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Sketch",
                        rating = 4.7,
                        cuisine = "Contemporary European",
                        priceRange = "££££",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Dishoom",
                        rating = 4.6,
                        cuisine = "Indian",
                        priceRange = "££",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Borough Market Food Stalls",
                        rating = 4.5,
                        cuisine = "International Street Food",
                        priceRange = "£",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "dubai",
                name = "Dubai",
                country = "UAE",
                lat = 25.2048,
                lng = 55.2708,
                description = "Futuristic city with luxury shopping and architecture",
                imageUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c",
                currencyCode = "AED",
                rating = 4.6,
                reviewCount = 13456,
                hotels = listOf(
                    Hotel(
                        name = "Burj Al Arab",
                        rating = 5.0,
                        pricePerNight = "AED 8,000",
                        amenities = listOf(
                            "Iconic Sail Shape",
                            "Butler Service",
                            "Private Beach",
                            "Gold Decor"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Atlantis The Palm",
                        rating = 4.7,
                        pricePerNight = "AED 2,500",
                        amenities = listOf(
                            "Water Park",
                            "Aquarium",
                            "Beach",
                            "Multiple Restaurants"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Rove Downtown",
                        rating = 4.3,
                        pricePerNight = "AED 450",
                        amenities = listOf("Modern", "Burj Khalifa View", "Pool", "WiFi"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Nobu Dubai",
                        rating = 4.8,
                        cuisine = "Japanese-Peruvian",
                        priceRange = "AED AED AED AED",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Al Nafoorah",
                        rating = 4.6,
                        cuisine = "Lebanese",
                        priceRange = "AED AED AED",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Arabian Tea House",
                        rating = 4.4,
                        cuisine = "Emirati",
                        priceRange = "AED AED",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "santorini",
                name = "Santorini",
                country = "Greece",
                lat = 36.3932,
                lng = 25.4615,
                description = "Iconic white buildings and stunning sunsets",
                imageUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff",
                currencyCode = "EUR",
                rating = 4.8,
                reviewCount = 10987,
                hotels = listOf(
                    Hotel(
                        name = "Canaves Oia Suites",
                        rating = 4.9,
                        pricePerNight = "€600",
                        amenities = listOf(
                            "Caldera View",
                            "Private Pool",
                            "Cave Suite",
                            "Sunset Views"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Astra Suites",
                        rating = 4.7,
                        pricePerNight = "€350",
                        amenities = listOf("Infinity Pool", "Caldera View", "Breakfast", "WiFi"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Karma Boutique",
                        rating = 4.4,
                        pricePerNight = "€180",
                        amenities = listOf("Pool", "Sunset View", "Traditional Style", "Breakfast"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Selene",
                        rating = 4.8,
                        cuisine = "Modern Greek",
                        priceRange = "€€€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Metaxi Mas",
                        rating = 4.7,
                        cuisine = "Traditional Greek",
                        priceRange = "€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Lucky's Souvlakis",
                        rating = 4.5,
                        cuisine = "Greek Street Food",
                        priceRange = "€",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "bangkok",
                name = "Bangkok",
                country = "Thailand",
                lat = 13.7563,
                lng = 100.5018,
                description = "Vibrant city with temples, street food, and markets",
                imageUrl = "https://images.unsplash.com/photo-1508009603885-50cf7c579365",
                currencyCode = "THB",
                rating = 4.7,
                reviewCount = 12345,
                hotels = listOf(
                    Hotel(
                        name = "Mandarin Oriental Bangkok",
                        rating = 4.9,
                        pricePerNight = "฿12,000",
                        amenities = listOf("River View", "Spa", "Fine Dining", "Historic"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Lub d Bangkok Silom",
                        rating = 4.4,
                        pricePerNight = "฿800",
                        amenities = listOf("Social Hostel", "Rooftop", "WiFi", "Trendy"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Siam@Siam Design Hotel",
                        rating = 4.5,
                        pricePerNight = "฿2,500",
                        amenities = listOf("Rooftop Pool", "Modern", "BTS Access", "Restaurant"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Gaggan Anand",
                        rating = 4.9,
                        cuisine = "Progressive Indian",
                        priceRange = "฿฿฿฿",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Jay Fai",
                        rating = 4.7,
                        cuisine = "Thai Street Food",
                        priceRange = "฿฿",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Thip Samai Pad Thai",
                        rating = 4.6,
                        cuisine = "Pad Thai Specialist",
                        priceRange = "฿",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "sydney",
                name = "Sydney",
                country = "Australia",
                lat = -33.8688,
                lng = 151.2093,
                description = "Beautiful harbor city with Opera House and beaches",
                imageUrl = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9",
                currencyCode = "AUD",
                rating = 4.8,
                reviewCount = 11234,
                hotels = listOf(
                    Hotel(
                        name = "Park Hyatt Sydney",
                        rating = 4.9,
                        pricePerNight = "A$800",
                        amenities = listOf(
                            "Opera House View",
                            "Harbor",
                            "Rooftop Pool",
                            "Fine Dining"
                        ),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "The Russell Hotel",
                        rating = 4.5,
                        pricePerNight = "A$200",
                        amenities = listOf("The Rocks", "Historic", "Rooftop", "WiFi"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Wake Up! Sydney",
                        rating = 4.2,
                        pricePerNight = "A$80",
                        amenities = listOf("Central", "Social", "Cafe", "Tours"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Quay",
                        rating = 4.9,
                        cuisine = "Contemporary Australian",
                        priceRange = "A$ A$ A$ A$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Icebergs Dining Room",
                        rating = 4.7,
                        cuisine = "Italian-Australian",
                        priceRange = "A$ A$ A$",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Harry's Cafe de Wheels",
                        rating = 4.5,
                        cuisine = "Meat Pies",
                        priceRange = "A$",
                        imageEmoji = "🍽️"
                    )
                )
            ),
            Destination(
                id = "barcelona",
                name = "Barcelona",
                country = "Spain",
                lat = 41.3851,
                lng = 2.1734,
                description = "Gaudí's masterpiece city with beaches and tapas",
                imageUrl = "https://images.unsplash.com/photo-1583422409516-2895a77efded",
                currencyCode = "EUR",
                rating = 4.9,
                reviewCount = 13456,
                hotels = listOf(
                    Hotel(
                        name = "Hotel Arts Barcelona",
                        rating = 4.9,
                        pricePerNight = "€450",
                        amenities = listOf("Beachfront", "Spa", "Michelin Dining", "Marina Views"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Hotel DO Plaça Reial",
                        rating = 4.6,
                        pricePerNight = "€180",
                        amenities = listOf("Gothic Quarter", "Rooftop Pool", "Tapas Bar", "WiFi"),
                        imageEmoji = "🏨"
                    ),
                    Hotel(
                        name = "Casa Gracia Hostel",
                        rating = 4.4,
                        pricePerNight = "€35",
                        amenities = listOf("Gaudí Area", "Rooftop", "Social", "Breakfast"),
                        imageEmoji = "🏨"
                    )
                ),
                restaurants = listOf(
                    Restaurant(
                        name = "Disfrutar",
                        rating = 4.9,
                        cuisine = "Avant-garde Spanish",
                        priceRange = "€€€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "Cervecería Catalana",
                        rating = 4.7,
                        cuisine = "Tapas",
                        priceRange = "€€",
                        imageEmoji = "🍽️"
                    ),
                    Restaurant(
                        name = "La Boqueria Market",
                        rating = 4.6,
                        cuisine = "Market Food",
                        priceRange = "€",
                        imageEmoji = "🍽️"
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
    
    /**
     * Search destinations by query
     */
    fun searchDestinations(query: String): List<Destination> {
        if (query.isBlank()) return getPopularDestinations()

        return getPopularDestinations().filter {
            it.name.contains(query, ignoreCase = true) ||
            it.country.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }
    
    /**
     * Get destinations by country
     */
    fun getDestinationsByCountry(country: String): List<Destination> {
        return getPopularDestinations().filter {
            it.country.equals(country, ignoreCase = true)
        }
    }

    // ==================== LIKED DESTINATIONS ====================

    /**
     * Load liked destinations from Firebase
     */
    private suspend fun loadLikedDestinations(uid: String) = withContext(Dispatchers.IO) {
        try {
            val likedIds = firestoreManager.getLikedDestinations(uid)
            _likedDestinations.value = likedIds
            saveLikedDestinationsToLocal()
            Log.d("Repository", "Loaded ${likedIds.size} liked destinations from Firestore")
        } catch (e: Exception) {
            Log.e("Repository", "Error loading liked destinations", e)
        }
    }

    /**
     * Check if a destination is liked
     */
    fun isDestinationLiked(destinationId: String): Boolean {
        return _likedDestinations.value.contains(destinationId)
    }

    /**
     * Like a destination
     */
    fun likeDestination(destinationId: String) {
        _likedDestinations.value = _likedDestinations.value + destinationId
        saveLikedDestinationsToLocal()

        // Save to Firebase
        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.likeDestination(user.uid, destinationId)
                    Log.d("Repository", "Destination liked in Firestore: $destinationId")
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
        saveLikedDestinationsToLocal()

        // Remove from Firebase
        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.unlikeDestination(user.uid, destinationId)
                    Log.d("Repository", "Destination unliked in Firestore: $destinationId")
                } catch (e: Exception) {
                    Log.e("Repository", "Error unliking destination", e)
                }
            }
        }
    }

    /**
     * Get all liked destinations
     */
    fun getLikedDestinations(): List<Destination> {
        val likedIds = _likedDestinations.value
        return getPopularDestinations().filter { it.id in likedIds }
    }

    // ==================== PLANS ====================

    /**
     * Load plans from Firebase
     */
    private suspend fun loadPlans(uid: String) = withContext(Dispatchers.IO) {
        try {
            val plans = firestoreManager.getPlans(uid)
            plansCache.clear()
            plansCache.addAll(plans)
            _plansVersion.value++
            savePlansToLocal()
            Log.d("Repository", "Loaded ${plansCache.size} plans from Firestore")
        } catch (e: Exception) {
            Log.e("Repository", "Error loading plans from Firestore", e)
        }
    }

    /**
     * Load liked plans from Firebase
     */
    private suspend fun loadLikedPlans(uid: String) = withContext(Dispatchers.IO) {
        try {
            val likedIds = firestoreManager.getLikedPlans(uid)
            _likedPlans.value = likedIds
            saveLikedPlansToLocal()
            Log.d("Repository", "Loaded ${likedIds.size} liked plans from Firestore")
        } catch (e: Exception) {
            Log.e("Repository", "Error loading liked plans", e)
        }
    }

    /**
     * Get all plans
     */
    fun getAllPlans(): List<Plan> = plansCache.toList()

    /**
     * Get plan by ID
     */
    fun getPlanById(planId: String): Plan? {
        return plansCache.find { it.id == planId }
    }

    /**
     * Add a new plan
     */
    fun addPlan(plan: Plan) {
        plansCache.add(plan)
        _plansVersion.value++
        savePlansToLocal()

        // Save to Firebase
        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.addPlan(user.uid, plan)
                    Log.d("Repository", "Plan saved successfully to Firestore: ${plan.title}")
                } catch (e: Exception) {
                    Log.e("Repository", "Error saving plan to Firestore", e)
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
        savePlansToLocal()
        saveLikedPlansToLocal()

        // Delete from Firebase
        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.deletePlan(user.uid, planId)
                    Log.d("Repository", "Plan deleted from Firestore: $planId")
                } catch (e: Exception) {
                    Log.e("Repository", "Error deleting plan", e)
                }
            }
        }
    }

    /**
     * Check if a plan is liked
     */
    fun isPlanLiked(planId: String): Boolean {
        return _likedPlans.value.contains(planId)
    }

    /**
     * Like a plan
     */
    fun likePlan(planId: String) {
        _likedPlans.value = _likedPlans.value + planId
        _plansVersion.value++
        saveLikedPlansToLocal()

        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.likePlan(user.uid, planId)
                    Log.d("Repository", "Plan liked in Firestore: $planId")
                } catch (e: Exception) {
                    Log.e("Repository", "Error liking plan", e)
                }
            }
        }
    }

    /**
     * Unlike a plan
     */
    fun unlikePlan(planId: String) {
        _likedPlans.value = _likedPlans.value - planId
        _plansVersion.value++
        saveLikedPlansToLocal()

        auth.currentUser?.let { user ->
            repositoryScope.launch {
                try {
                    firestoreManager.unlikePlan(user.uid, planId)
                    Log.d("Repository", "Plan unliked in Firestore: $planId")
                } catch (e: Exception) {
                    Log.e("Repository", "Error unliking plan", e)
                }
            }
        }
    }

    /**
     * Get all liked plans
     */
    fun getLikedPlans(): List<Plan> {
        val likedIds = _likedPlans.value
        return plansCache.filter { it.id in likedIds }
    }
}