package com.runanywhere.startup_hackathon20.data

import com.runanywhere.startup_hackathon20.data.model.*

class TravelRepository {
    private val destinations = listOf(
        // France
        Destination("paris","Paris","France",48.8566,2.3522,"https://picsum.photos/600/400?1","EUR"),
        Destination(
            "nice",
            "Nice",
            "France",
            43.7102,
            7.2620,
            "https://picsum.photos/600/400?2",
            "EUR"
        ),
        Destination(
            "lyon",
            "Lyon",
            "France",
            45.7640,
            4.8357,
            "https://picsum.photos/600/400?3",
            "EUR"
        ),

        // Japan
        Destination(
            "tokyo",
            "Tokyo",
            "Japan",
            35.6762,
            139.6503,
            "https://picsum.photos/600/400?4",
            "JPY"
        ),
        Destination(
            "kyoto",
            "Kyoto",
            "Japan",
            35.0116,
            135.7681,
            "https://picsum.photos/600/400?5",
            "JPY"
        ),
        Destination(
            "osaka",
            "Osaka",
            "Japan",
            34.6937,
            135.5023,
            "https://picsum.photos/600/400?6",
            "JPY"
        ),

        // Indonesia
        Destination(
            "bali",
            "Bali",
            "Indonesia",
            -8.3405,
            115.0920,
            "https://picsum.photos/600/400?7",
            "IDR"
        ),
        Destination(
            "jakarta",
            "Jakarta",
            "Indonesia",
            -6.2088,
            106.8456,
            "https://picsum.photos/600/400?8",
            "IDR"
        ),
        Destination(
            "lombok",
            "Lombok",
            "Indonesia",
            -8.6500,
            116.3242,
            "https://picsum.photos/600/400?9",
            "IDR"
        ),

        // Italy
        Destination(
            "rome",
            "Rome",
            "Italy",
            41.9028,
            12.4964,
            "https://picsum.photos/600/400?10",
            "EUR"
        ),
        Destination(
            "venice",
            "Venice",
            "Italy",
            45.4408,
            12.3155,
            "https://picsum.photos/600/400?11",
            "EUR"
        ),
        Destination(
            "milan",
            "Milan",
            "Italy",
            45.4642,
            9.1900,
            "https://picsum.photos/600/400?12",
            "EUR"
        ),

        // United States
        Destination(
            "newyork",
            "New York",
            "USA",
            40.7128,
            -74.0060,
            "https://picsum.photos/600/400?13",
            "USD"
        ),
        Destination(
            "losangeles",
            "Los Angeles",
            "USA",
            34.0522,
            -118.2437,
            "https://picsum.photos/600/400?14",
            "USD"
        ),
        Destination(
            "miami",
            "Miami",
            "USA",
            25.7617,
            -80.1918,
            "https://picsum.photos/600/400?15",
            "USD"
        ),

        // Thailand
        Destination(
            "bangkok",
            "Bangkok",
            "Thailand",
            13.7563,
            100.5018,
            "https://picsum.photos/600/400?16",
            "THB"
        ),
        Destination(
            "phuket",
            "Phuket",
            "Thailand",
            7.8804,
            98.3923,
            "https://picsum.photos/600/400?17",
            "THB"
        ),
        Destination(
            "chiangmai",
            "Chiang Mai",
            "Thailand",
            18.7883,
            98.9853,
            "https://picsum.photos/600/400?18",
            "THB"
        ),

        // Spain
        Destination(
            "barcelona",
            "Barcelona",
            "Spain",
            41.3851,
            2.1734,
            "https://picsum.photos/600/400?19",
            "EUR"
        ),
        Destination(
            "madrid",
            "Madrid",
            "Spain",
            40.4168,
            -3.7038,
            "https://picsum.photos/600/400?20",
            "EUR"
        ),
        Destination(
            "seville",
            "Seville",
            "Spain",
            37.3891,
            -5.9845,
            "https://picsum.photos/600/400?21",
            "EUR"
        ),

        // United Kingdom
        Destination(
            "london",
            "London",
            "UK",
            51.5074,
            -0.1278,
            "https://picsum.photos/600/400?22",
            "GBP"
        ),
        Destination(
            "edinburgh",
            "Edinburgh",
            "UK",
            55.9533,
            -3.1883,
            "https://picsum.photos/600/400?23",
            "GBP"
        ),
        Destination(
            "manchester",
            "Manchester",
            "UK",
            53.4808,
            -2.2426,
            "https://picsum.photos/600/400?24",
            "GBP"
        ),

        // United Arab Emirates
        Destination(
            "dubai",
            "Dubai",
            "UAE",
            25.2048,
            55.2708,
            "https://picsum.photos/600/400?25",
            "AED"
        ),
        Destination(
            "abudhabi",
            "Abu Dhabi",
            "UAE",
            24.4539,
            54.3773,
            "https://picsum.photos/600/400?26",
            "AED"
        ),
        Destination(
            "sharjah",
            "Sharjah",
            "UAE",
            25.3463,
            55.4209,
            "https://picsum.photos/600/400?27",
            "AED"
        ),

        // India
        Destination(
            "delhi",
            "Delhi",
            "India",
            28.7041,
            77.1025,
            "https://picsum.photos/600/400?28",
            "INR"
        ),
        Destination(
            "mumbai",
            "Mumbai",
            "India",
            19.0760,
            72.8777,
            "https://picsum.photos/600/400?29",
            "INR"
        ),
        Destination(
            "goa",
            "Goa",
            "India",
            15.2993,
            74.1240,
            "https://picsum.photos/600/400?30",
            "INR"
        ),
    )

    private val likedDestinations = mutableSetOf<String>()
    private val plans = mutableMapOf<String, Plan>()
    private val likedPlans = mutableSetOf<String>()

    // User management
    private var currentUser: User? = null
    private val registeredUsers = mutableMapOf<String, User>()  // email -> User

    fun getPopularDestinations(): List<Destination> = destinations
    fun getDestination(id: String): Destination? = destinations.find { it.id == id }

    fun likeDestination(id: String) { likedDestinations += id }
    fun getLikedDestinations(): List<Destination> = likedDestinations.mapNotNull { getDestination(it) }

    fun savePlan(plan: Plan) { plans[plan.id] = plan }
    fun getPlan(planId: String): Plan? = plans[planId]
    fun likePlan(planId: String) { likedPlans += planId }
    fun getLikedPlans(): List<Plan> = likedPlans.mapNotNull { plans[it] }

    // User functions
    fun registerUser(email: String, name: String, phone: String, location: String): Boolean {
        if (registeredUsers.containsKey(email)) {
            return false // User already exists
        }
        val user = User(email, name, phone, location)
        registeredUsers[email] = user
        currentUser = user
        return true
    }

    fun loginUser(email: String, password: String): Boolean {
        // Simple login - in real app, verify password
        val user = registeredUsers[email]
        if (user != null) {
            currentUser = user
            return true
        }
        // For demo, allow any login and create user
        val newUser = User(email, "User", "", "")
        registeredUsers[email] = newUser
        currentUser = newUser
        return true
    }

    fun getCurrentUser(): User? = currentUser

    fun updateUser(user: User) {
        currentUser = user
        registeredUsers[user.email] = user
    }

    fun isLoggedIn(): Boolean = currentUser != null
}
