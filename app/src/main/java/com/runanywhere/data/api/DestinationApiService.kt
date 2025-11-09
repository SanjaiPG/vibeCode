package com.runanywhere.data.api

import com.runanywhere.data.model.*
import com.runanywhere.startup_hackathon20.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.google.gson.Gson
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere

/**
 * API Service for fetching destination-related data from external APIs
 * Uses real API calls with configured API keys
 */
object DestinationApiService {
    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    /**
     * Search destinations online using Places API
     * Replace with actual Google Places API call
     */
    suspend fun searchDestinationsOnline(query: String): List<Destination> = withContext(Dispatchers.IO) {
        delay(500) // Simulate network delay
        
        // Mock results - replace with actual Places API call
        if (query.isEmpty()) return@withContext emptyList()
        
        val mockResults = listOf(
            Destination(
                id = "tokyo_${query.hashCode()}",
                name = "Tokyo, Japan",
                country = "Japan",
                lat = 35.6762,
                lng = 139.6503,
                imageUrl = "",
                currencyCode = "JPY",
                rating = 4.8,
                reviewCount = 12345
            ),
            Destination(
                id = "newyork_${query.hashCode()}",
                name = "New York, USA",
                country = "United States",
                lat = 40.7128,
                lng = -74.0060,
                imageUrl = "",
                currencyCode = "USD",
                rating = 4.7,
                reviewCount = 23456
            )
        )
        
        // Filter by query
        mockResults.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.country.contains(query, ignoreCase = true) 
        }
    }
    
    /**
     * Get destination image URL from Unsplash API
     */
    suspend fun getDestinationImageUrl(destinationName: String): String = withContext(Dispatchers.IO) {
        try {
            // Use destination-specific keywords for better image results
            val searchQuery = when (destinationName.lowercase()) {
                "paris" -> "paris+eiffel+tower+france"
                "tokyo" -> "tokyo+japan+skyline+city"
                "bali" -> "bali+indonesia+temple+beach"
                "new york" -> "new+york+city+manhattan+skyline"
                "london" -> "london+big+ben+england"
                "rome" -> "rome+colosseum+italy"
                "dubai" -> "dubai+burj+khalifa+uae"
                "singapore" -> "singapore+marina+bay+skyline"
                "bangkok" -> "bangkok+temple+thailand"
                "mumbai" -> "mumbai+gateway+india"
                "delhi" -> "new+delhi+india+gate"
                "sydney" -> "sydney+opera+house+australia"
                "los angeles" -> "los+angeles+hollywood+california"
                "barcelona" -> "barcelona+sagrada+familia+spain"
                "amsterdam" -> "amsterdam+canals+netherlands"
                else -> destinationName.replace(" ", "+") + "+travel+destination"
            }

            val url =
                "https://api.unsplash.com/search/photos?query=$searchQuery&client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}&per_page=1&orientation=landscape"
            val response: UnsplashResponse = httpClient.get(url).body()
            response.results.firstOrNull()?.urls?.regular ?: getFallbackImage(destinationName)
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackImage(destinationName)
        }
    }

    private fun getFallbackImage(destinationName: String): String {
        // Return destination-specific fallback images
        return when (destinationName.lowercase()) {
            "paris" -> "https://images.unsplash.com/photo-1502602898536-47ad22581b52?w=800&h=600&fit=crop"
            "tokyo" -> "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800&h=600&fit=crop"
            "bali" -> "https://images.unsplash.com/photo-1537953773345-d172ccf13cf1?w=800&h=600&fit=crop"
            "new york" -> "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=800&h=600&fit=crop"
            "london" -> "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?w=800&h=600&fit=crop"
            "rome" -> "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=800&h=600&fit=crop"
            "dubai" -> "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=800&h=600&fit=crop"
            "singapore" -> "https://images.unsplash.com/photo-1525625293386-3f8f99389edd?w=800&h=600&fit=crop"
            "bangkok" -> "https://images.unsplash.com/photo-1508009603885-50cf7c579365?w=800&h=600&fit=crop"
            "mumbai" -> "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=800&h=600&fit=crop"
            "delhi" -> "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=800&h=600&fit=crop"
            "sydney" -> "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop"
            "los angeles" -> "https://images.unsplash.com/photo-1441742917377-57f78ee0e582?w=800&h=600&fit=crop"
            "barcelona" -> "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=800&h=600&fit=crop"
            "amsterdam" -> "https://images.unsplash.com/photo-1534351590666-13e3e96b5017?w=800&h=600&fit=crop"
            else -> "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop"
        }
    }
    
    /**
     * Generate AI description for destination using on-device AI or fallback
     */
    suspend fun generateAIDescription(destination: String, country: String): String = withContext(Dispatchers.IO) {
        // Note: OpenAI integration removed - using on-device RunAnywhere AI instead
        // For destination descriptions, we use fallback data
        getFallbackDescription(destination, country)
    }

    private fun getFallbackDescription(destination: String, country: String): String {
        return """
        $destination, $country is a captivating destination that offers an incredible blend of culture, history, and natural beauty. 
        From stunning landscapes to vibrant local traditions, this destination provides unforgettable experiences for every traveler. 
        The best time to visit is during the spring and autumn months when the weather is pleasant and the crowds are manageable. 
        Whether you're seeking adventure, relaxation, or cultural immersion, $destination promises to exceed your expectations.
        """.trimIndent()
    }
    
    /**
     * Fetch reviews from external APIs
     * Note: Google Places API doesn't provide direct review access without place details
     * This uses mock reviews with AI-powered sentiment analysis
     */
    suspend fun fetchReviewsFromAPI(destinationName: String, country: String): List<ReviewWithSentiment> = withContext(Dispatchers.IO) {
        delay(600)
        
        // Mock reviews - in production, fetch from Google Places API or TripAdvisor
        val mockReviews = listOf(
            ReviewWithSentiment(
                userName = "Alex Thompson",
                rating = 5,
                comment = "Absolutely amazing! The culture and people were incredible. Highly recommend visiting during spring season.",
                date = "2024-01-15",
                userEmoji = "👨",
                sentiment = "positive"
            ),
            ReviewWithSentiment(
                userName = "Maria Garcia",
                rating = 4,
                comment = "Great destination with lots to see and do. Some areas were crowded but overall a wonderful experience.",
                date = "2024-01-10",
                userEmoji = "👩",
                sentiment = "positive"
            ),
            ReviewWithSentiment(
                userName = "John Smith",
                rating = 3,
                comment = "It was okay. The weather wasn't great during my visit, but the food was excellent.",
                date = "2024-01-05",
                userEmoji = "👤",
                sentiment = "neutral"
            )
        )
        
        // Analyze sentiment for each review using AI
        mockReviews.map { review ->
            val analyzedSentiment = analyzeSentiment(review.comment)
            review.copy(sentiment = analyzedSentiment)
        }
    }
    
    /**
     * Fetch trending hashtags and social data
     * Replace with actual Social Media API call
     */
    suspend fun fetchTrendingHashtags(destinationName: String): SocialData = withContext(Dispatchers.IO) {
        delay(400)
        
        // Mock social data - replace with actual API call
        SocialData(
            hashtags = listOf(
                destinationName.lowercase().replace(" ", ""),
                "${destinationName.lowercase()}travel",
                "${destinationName.lowercase()}photography",
                "travel${destinationName.lowercase()}",
                "visit${destinationName.lowercase()}"
            ),
            postCount = (5000..50000).random()
        )
    }
    
    /**
     * Calculate budget breakdown for destination
     * This could use a database or ML model
     */
    suspend fun calculateBudgetForDestination(destinationId: String, currencyCode: String): BudgetBreakdown = withContext(Dispatchers.IO) {
        delay(300)
        
        // Mock budget calculation - replace with actual calculation logic
        val baseMultiplier = when (currencyCode) {
            "USD" -> 1.0
            "EUR" -> 0.85
            "JPY" -> 150.0
            "IDR" -> 15000.0
            else -> 1.0
        }
        
        val accommodation = 150.0 * baseMultiplier
        val food = 80.0 * baseMultiplier
        val activities = 100.0 * baseMultiplier
        val transport = 50.0 * baseMultiplier
        val total = accommodation + food + activities + transport
        
        BudgetBreakdown(
            accommodation = accommodation,
            food = food,
            activities = activities,
            transport = transport,
            total = total
        )
    }
    
    /**
     * Fetch visa requirements
     * Replace with actual Visa API call (iVisaGuide, Nomad Visa API, etc.)
     */
    suspend fun fetchVisaRequirements(country: String): VisaRequirements = withContext(Dispatchers.IO) {
        delay(400)
        
        // Mock visa data - replace with actual API call
        val visaRequired = when (country.lowercase()) {
            "france", "spain", "italy" -> false // EU countries
            "japan", "thailand", "indonesia" -> true
            else -> true
        }
        
        VisaRequirements(
            visaRequired = visaRequired,
            passportValidity = 6,
            processingDays = if (visaRequired) 14 else 0,
            visaType = if (visaRequired) "Tourist Visa" else null,
            applicationUrl = if (visaRequired) "https://example.com/visa-application" else null
        )
    }
    
    /**
     * Fetch attractions/things to do
     * Replace with actual Google Places or Foursquare API call
     */
    suspend fun fetchAttractions(destinationName: String, lat: Double, lng: Double): List<Attraction> = withContext(Dispatchers.IO) {
        delay(500)

        // Destination-specific attractions instead of generic ones
        val attractions = when (destinationName.lowercase()) {
            "paris" -> listOf(
                Attraction(
                    id = "eiffel_tower",
                    title = "Eiffel Tower",
                    description = "Iconic iron tower and symbol of Paris offering breathtaking city views.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1502602898536-47ad22581b52?w=400",
                    name = "Eiffel Tower",
                    rating = 4.6,
                    duration = "2-3 hours",
                    estimatedCost = "€26",
                    imageUrl = "https://images.unsplash.com/photo-1502602898536-47ad22581b52?w=400"
                ),
                Attraction(
                    id = "louvre_museum",
                    title = "Louvre Museum",
                    description = "World's largest art museum housing the Mona Lisa and thousands of masterpieces.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1566139937007-825ec268ba16?w=400",
                    name = "Louvre Museum",
                    rating = 4.8,
                    duration = "4-6 hours",
                    estimatedCost = "€17",
                    imageUrl = "https://images.unsplash.com/photo-1566139937007-825ec268ba16?w=400"
                ),
                Attraction(
                    id = "notre_dame",
                    title = "Notre-Dame Cathedral",
                    description = "Gothic cathedral with stunning architecture and rich history.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1605198188297-40de6b88a6c9?w=400",
                    name = "Notre-Dame Cathedral",
                    rating = 4.7,
                    duration = "1-2 hours",
                    estimatedCost = "Free",
                    imageUrl = "https://images.unsplash.com/photo-1605198188297-40de6b88a6c9?w=400"
                )
            )

            "tokyo" -> listOf(
                Attraction(
                    id = "sensoji_temple",
                    title = "Sensoji Temple",
                    description = "Tokyo's oldest Buddhist temple with traditional markets and cultural experiences.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=400",
                    name = "Sensoji Temple",
                    rating = 4.7,
                    duration = "2-3 hours",
                    estimatedCost = "Free",
                    imageUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=400"
                ),
                Attraction(
                    id = "tokyo_skytree",
                    title = "Tokyo Skytree",
                    description = "World's tallest tower offering spectacular panoramic views of Tokyo.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1513407030348-c983a97b98d8?w=400",
                    name = "Tokyo Skytree",
                    rating = 4.5,
                    duration = "2-3 hours",
                    estimatedCost = "¥2060",
                    imageUrl = "https://images.unsplash.com/photo-1513407030348-c983a97b98d8?w=400"
                ),
                Attraction(
                    id = "shibuya_crossing",
                    title = "Shibuya Crossing",
                    description = "World's busiest pedestrian crossing in the heart of Tokyo's youth culture.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=400",
                    name = "Shibuya Crossing",
                    rating = 4.4,
                    duration = "1-2 hours",
                    estimatedCost = "Free",
                    imageUrl = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=400"
                )
            )

            "bali" -> listOf(
                Attraction(
                    id = "tanah_lot",
                    title = "Tanah Lot Temple",
                    description = "Stunning Hindu temple perched on a rock formation by the sea.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1555400282-47b9a7d8b3d8?w=400",
                    name = "Tanah Lot Temple",
                    rating = 4.6,
                    duration = "2-3 hours",
                    estimatedCost = "Rp 60,000",
                    imageUrl = "https://images.unsplash.com/photo-1555400282-47b9a7d8b3d8?w=400"
                ),
                Attraction(
                    id = "ubud_rice_terraces",
                    title = "Tegallalang Rice Terraces",
                    description = "Beautiful terraced rice fields offering scenic walks and photo opportunities.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1528181304800-259b08848526?w=400",
                    name = "Tegallalang Rice Terraces",
                    rating = 4.5,
                    duration = "2-4 hours",
                    estimatedCost = "Rp 15,000",
                    imageUrl = "https://images.unsplash.com/photo-1528181304800-259b08848526?w=400"
                ),
                Attraction(
                    id = "uluwatu_temple",
                    title = "Uluwatu Temple",
                    description = "Clifftop temple with spectacular sunset views and traditional Kecak dance.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1544966503-7cc5ac882d5e?w=400",
                    name = "Uluwatu Temple",
                    rating = 4.8,
                    duration = "3-4 hours",
                    estimatedCost = "Rp 30,000",
                    imageUrl = "https://images.unsplash.com/photo-1544966503-7cc5ac882d5e?w=400"
                )
            )

            else -> listOf(
                Attraction(
                    id = "historic_center_${destinationName.hashCode()}",
                    title = "Historic City Center",
                    description = "Explore the historic heart of $destinationName with stunning architecture and rich culture.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=400",
                    name = "Historic City Center",
                    rating = 4.8,
                    duration = "3-4 hours",
                    estimatedCost = "$$",
                    imageUrl = "https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?w=400"
                ),
                Attraction(
                    id = "local_market_${destinationName.hashCode()}",
                    title = "Local Market",
                    description = "Experience authentic local culture at bustling markets in $destinationName.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                    name = "Local Market",
                    rating = 4.6,
                    duration = "2-3 hours",
                    estimatedCost = "$",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400"
                ),
                Attraction(
                    id = "cultural_site_${destinationName.hashCode()}",
                    title = "Cultural Heritage Site",
                    description = "Discover the cultural heritage and traditions of $destinationName.",
                    thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=400",
                    name = "Cultural Heritage Site",
                    rating = 4.7,
                    duration = "2-4 hours",
                    estimatedCost = "$$",
                    imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=400"
                )
            )
        }

        attractions
    }
    
    /**
     * Analyze sentiment of review text using simple keyword analysis
     */
    suspend fun analyzeSentiment(text: String): String = withContext(Dispatchers.IO) {
        // Note: OpenAI integration removed - using keyword-based sentiment analysis
        val positiveWords = listOf(
            "amazing",
            "wonderful",
            "excellent",
            "great",
            "love",
            "perfect",
            "beautiful",
            "incredible",
            "fantastic",
            "awesome"
        )
        val negativeWords = listOf(
            "terrible",
            "awful",
            "bad",
            "disappointing",
            "horrible",
            "worst",
            "hate",
            "poor",
            "unpleasant"
        )

        val lowerText = text.lowercase()
        val positiveCount = positiveWords.count { lowerText.contains(it) }
        val negativeCount = negativeWords.count { lowerText.contains(it) }

        when {
            positiveCount > negativeCount -> "positive"
            negativeCount > positiveCount -> "negative"
            else -> "neutral"
        }
    }

    /**
     * Generate destination details using RunAnywhere SDK (on-device AI)
     * This is FASTER and works OFFLINE!
     */
    suspend fun generateDestinationWithRunAnywhereAI(destinationQuery: String): DestinationSearchResult =
        withContext(Dispatchers.IO) {
            try {
                Log.d(
                    "DestinationApiService",
                    "Generating destination with RunAnywhere AI: $destinationQuery"
                )

                val prompt = """
                Generate travel information for: $destinationQuery
                
                Provide details in this format:
                
                DESTINATION: [City Name]
                COUNTRY: [Country Name]
                DESCRIPTION: [2-3 engaging sentences about the destination]
                BEST TIME: [Best months/season to visit]
                CURRENCY: [Currency code like USD, EUR, JPY]
                
                HOTELS:
                1. [Hotel Name] - Rating: [X.X] - Price: [$ or $$ format] - Amenities: [list]
                2. [Hotel Name] - Rating: [X.X] - Price: [$ or $ format] - Amenities: [list]
                3. [Hotel Name] - Rating: [X.X] - Price: [$ format] - Amenities: [list]
                
                RESTAURANTS:
                1. [Name] - Cuisine: [Type] - Rating: [X.X] - Price: [$ symbols]
                2. [Name] - Cuisine: [Type] - Rating: [X.X] - Price: [$ symbols]
                3. [Name] - Cuisine: [Type] - Rating: [X.X] - Price: [$ symbols]
                
                ATTRACTIONS:
                1. [Name] - [Brief description] - Duration: [X hours] - Cost: [$ symbols]
                2. [Name] - [Brief description] - Duration: [X hours] - Cost: [$ symbols]
                3. [Name] - [Brief description] - Duration: [X hours] - Cost: [$ symbols]
                
                TIPS: [2-3 essential travel tips]
                
                Keep it concise and informative.
                """.trimIndent()

                Log.d("DestinationApiService", "Generating with AI...")

                // Generate using RunAnywhere SDK
                var aiResponse = StringBuilder()
                RunAnywhere.generateStream(prompt).collect { token ->
                    aiResponse.append(token)
                }

                val fullResponse = aiResponse.toString()
                Log.d("DestinationApiService", "AI Response length: ${fullResponse.length}")
                Log.d("DestinationApiService", "AI Response preview: ${fullResponse.take(200)}")

                // Parse the AI response
                val result = parseAIResponse(fullResponse, destinationQuery)

                Log.d(
                    "DestinationApiService",
                    "Parsed destination: ${result.name}, ${result.country}"
                )

                // Fetch real image from Unsplash
                val imageUrl = try {
                    getDestinationImageUrl(result.name)
                } catch (e: Exception) {
                    Log.e("DestinationApiService", "Image fetch failed: ${e.message}")
                    ""
                }

                return@withContext result.copy(imageUrl = imageUrl)

            } catch (e: Exception) {
                Log.e(
                    "DestinationApiService",
                    "Error in generateDestinationWithRunAnywhereAI: ${e.message}",
                    e
                )
                // Return fallback data
                return@withContext createFallbackDestination(destinationQuery)
            }
        }

    /**
     * Parse AI response text into structured data
     */
    private fun parseAIResponse(
        response: String,
        destinationQuery: String
    ): DestinationSearchResult {
        try {
            val lines = response.lines()

            // Extract fields using regex and string matching
            var destination = destinationQuery.capitalize()
            var country = "Various"
            var description = "Discover the beauty and culture of $destinationQuery."
            var bestTime = "Spring & Autumn"
            var currency = "Local Currency"
            val hotels = mutableListOf<HotelSearchResult>()
            val restaurants = mutableListOf<RestaurantSearchResult>()
            val attractions = mutableListOf<AttractionSearchResult>()
            var tips = "Plan ahead and book accommodations early."

            var currentSection = ""

            for (line in lines) {
                val trimmedLine = line.trim()

                when {
                    trimmedLine.startsWith("DESTINATION:", ignoreCase = true) -> {
                        destination =
                            trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                    }
                    trimmedLine.startsWith("COUNTRY:", ignoreCase = true) -> {
                        country = trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                    }
                    trimmedLine.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                        description =
                            trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                    }
                    trimmedLine.startsWith("BEST TIME:", ignoreCase = true) -> {
                        bestTime =
                            trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                    }
                    trimmedLine.startsWith("CURRENCY:", ignoreCase = true) -> {
                        currency =
                            trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                    }

                    trimmedLine.startsWith("HOTELS:", ignoreCase = true) -> {
                        currentSection = "hotels"
                    }

                    trimmedLine.startsWith("RESTAURANTS:", ignoreCase = true) -> {
                        currentSection = "restaurants"
                    }

                    trimmedLine.startsWith("ATTRACTIONS:", ignoreCase = true) -> {
                        currentSection = "attractions"
                    }

                    trimmedLine.startsWith("TIPS:", ignoreCase = true) -> {
                        tips = trimmedLine.substringAfter(":").trim().removeSurrounding("[", "]")
                        currentSection = "tips"
                    }

                    trimmedLine.matches(Regex("^\\d+\\..*")) -> {
                        // Parse numbered items
                        when (currentSection) {
                            "hotels" -> {
                                val hotelText = trimmedLine.substringAfter(". ")
                                val parts = hotelText.split(" - ")
                                if (parts.size >= 3) {
                                    val name = parts[0].trim().removeSurrounding("[", "]")
                                    val rating =
                                        parts.getOrNull(1)?.substringAfter("Rating:")?.trim()
                                            ?.removeSurrounding("[", "]")?.toDoubleOrNull() ?: 4.5
                                    val price = parts.getOrNull(2)?.substringAfter("Price:")?.trim()
                                        ?.removeSurrounding("[", "]") ?: "$$"
                                    val amenities =
                                        parts.getOrNull(3)?.substringAfter("Amenities:")?.trim()
                                            ?.removeSurrounding("[", "]") ?: "WiFi, Restaurant"

                                    hotels.add(HotelSearchResult(name, rating, price, amenities))
                                }
                            }

                            "restaurants" -> {
                                val restaurantText = trimmedLine.substringAfter(". ")
                                val parts = restaurantText.split(" - ")
                                if (parts.size >= 3) {
                                    val name = parts[0].trim().removeSurrounding("[", "]")
                                    val cuisine =
                                        parts.getOrNull(1)?.substringAfter("Cuisine:")?.trim()
                                            ?.removeSurrounding("[", "]") ?: "Local"
                                    val rating =
                                        parts.getOrNull(2)?.substringAfter("Rating:")?.trim()
                                            ?.removeSurrounding("[", "]")?.toDoubleOrNull() ?: 4.5
                                    val price = parts.getOrNull(3)?.substringAfter("Price:")?.trim()
                                        ?.removeSurrounding("[", "]") ?: "$$"

                                    restaurants.add(
                                        RestaurantSearchResult(
                                            name,
                                            cuisine,
                                            rating,
                                            price
                                        )
                                    )
                                }
                            }

                            "attractions" -> {
                                val attractionText = trimmedLine.substringAfter(". ")
                                val parts = attractionText.split(" - ")
                                if (parts.size >= 3) {
                                    val name = parts[0].trim().removeSurrounding("[", "]")
                                    val desc =
                                        parts.getOrNull(1)?.trim()?.removeSurrounding("[", "]")
                                            ?: "Explore this attraction"
                                    val duration =
                                        parts.getOrNull(2)?.substringAfter("Duration:")?.trim()
                                            ?.removeSurrounding("[", "]") ?: "2-3 hours"
                                    val cost = parts.getOrNull(3)?.substringAfter("Cost:")?.trim()
                                        ?.removeSurrounding("[", "]") ?: "$$"

                                    attractions.add(
                                        AttractionSearchResult(
                                            name,
                                            desc,
                                            duration,
                                            cost
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Ensure we have minimum data
            if (hotels.isEmpty()) {
                hotels.addAll(
                    listOf(
                        HotelSearchResult(
                            "Grand Hotel $destination",
                            4.5,
                            "$$$",
                            "WiFi, Pool, Spa"
                        ),
                        HotelSearchResult("Comfort Inn $destination", 4.2, "$$", "WiFi, Breakfast"),
                        HotelSearchResult(
                            "Budget Stay $destination",
                            3.8,
                            "$",
                            "WiFi, Basic Amenities"
                        )
                    )
                )
            }

            if (restaurants.isEmpty()) {
                restaurants.addAll(
                    listOf(
                        RestaurantSearchResult("Local Cuisine House", "Traditional", 4.6, "$$$"),
                        RestaurantSearchResult("International Bistro", "International", 4.4, "$$"),
                        RestaurantSearchResult("Street Food Market", "Street Food", 4.5, "$")
                    )
                )
            }

            if (attractions.isEmpty()) {
                attractions.addAll(
                    listOf(
                        AttractionSearchResult(
                            "Historic City Center",
                            "Explore the heart of the city",
                            "3-4 hours",
                            "$$"
                        ),
                        AttractionSearchResult(
                            "Cultural Museum",
                            "Learn about local history",
                            "2-3 hours",
                            "$$"
                        ),
                        AttractionSearchResult(
                            "Scenic Viewpoint",
                            "Breathtaking views",
                            "1-2 hours",
                            "$"
                        )
                    )
                )
            }

            return DestinationSearchResult(
                name = destination,
                country = country,
                description = description,
                bestTimeToVisit = bestTime,
                currency = currency,
                hotels = hotels,
                restaurants = restaurants,
                attractions = attractions,
                tips = tips,
                imageUrl = ""
            )

        } catch (e: Exception) {
            Log.e("DestinationApiService", "Error parsing AI response: ${e.message}", e)
            return createBasicFallback(destinationQuery)
        }
    }

    /**
     * Create basic fallback destination data (non-suspend version)
     */
    private fun createBasicFallback(destinationQuery: String): DestinationSearchResult {
        return DestinationSearchResult(
            name = destinationQuery.capitalize(),
            country = "Various",
            description = "Discover the beauty and culture of $destinationQuery. This destination offers incredible experiences, from historic landmarks to modern attractions, with something for every traveler.",
            bestTimeToVisit = "Spring & Autumn (March-May, September-November)",
            currency = "Local Currency",
            hotels = listOf(
                HotelSearchResult(
                    name = "Grand Hotel ${destinationQuery.take(10)}",
                    rating = 4.5,
                    priceRange = "${'$'}${'$'}${'$'}",
                    amenities = "WiFi, Pool, Spa, Restaurant"
                ),
                HotelSearchResult(
                    name = "Comfort Inn ${destinationQuery.take(10)}",
                    rating = 4.2,
                    priceRange = "${'$'}${'$'}",
                    amenities = "WiFi, Breakfast, Parking"
                ),
                HotelSearchResult(
                    name = "Budget Stay ${destinationQuery.take(10)}",
                    rating = 3.8,
                    priceRange = "${'$'}",
                    amenities = "WiFi, Basic Amenities"
                )
            ),
            restaurants = listOf(
                RestaurantSearchResult(
                    name = "Local Cuisine House",
                    cuisine = "Traditional Local",
                    rating = 4.6,
                    priceRange = "${'$'}${'$'}${'$'}"
                ),
                RestaurantSearchResult(
                    name = "International Bistro",
                    cuisine = "International",
                    rating = 4.4,
                    priceRange = "${'$'}${'$'}"
                ),
                RestaurantSearchResult(
                    name = "Street Food Market",
                    cuisine = "Local Street Food",
                    rating = 4.5,
                    priceRange = "${'$'}"
                )
            ),
            attractions = listOf(
                AttractionSearchResult(
                    name = "Historic City Center",
                    description = "Explore the heart of the city with stunning architecture",
                    duration = "3-4 hours",
                    cost = "${'$'}${'$'}"
                ),
                AttractionSearchResult(
                    name = "Cultural Museum",
                    description = "Learn about local history and traditions",
                    duration = "2-3 hours",
                    cost = "${'$'}${'$'}"
                ),
                AttractionSearchResult(
                    name = "Scenic Viewpoint",
                    description = "Breathtaking views of the surrounding area",
                    duration = "1-2 hours",
                    cost = "${'$'}"
                )
            ),
            tips = "Best to visit during off-peak seasons. Learn basic local phrases. Try local cuisine. Book accommodations in advance.",
            imageUrl = ""
        )
    }

    /**
     * Search for destination details using ChatGPT API
     * Returns complete destination information including hotels, restaurants, and attractions
     * ALWAYS returns a result, never null - uses fallback if API fails
     */
    suspend fun searchDestinationWithAI(destinationQuery: String): DestinationSearchResult =
        withContext(Dispatchers.IO) {
            // Use RunAnywhere SDK instead of ChatGPT API
            return@withContext generateDestinationWithRunAnywhereAI(destinationQuery)
        }

    /**
     * Create fallback destination data when API fails
     */
    private suspend fun createFallbackDestination(destinationQuery: String): DestinationSearchResult {
        Log.i("DestinationApiService", "Creating fallback data for: $destinationQuery")

        return DestinationSearchResult(
            name = destinationQuery.capitalize(),
            country = "Various",
            description = "Discover the beauty and culture of $destinationQuery. This destination offers incredible experiences, from historic landmarks to modern attractions, with something for every traveler.",
            bestTimeToVisit = "Spring & Autumn (March-May, September-November)",
            currency = "Local Currency",
            hotels = listOf(
                HotelSearchResult(
                    name = "Grand Hotel ${destinationQuery.take(10)}",
                    rating = 4.5,
                    priceRange = "${'$'}${'$'}${'$'}",
                    amenities = "WiFi, Pool, Spa, Restaurant"
                ),
                HotelSearchResult(
                    name = "Comfort Inn ${destinationQuery.take(10)}",
                    rating = 4.2,
                    priceRange = "${'$'}${'$'}",
                    amenities = "WiFi, Breakfast, Parking"
                ),
                HotelSearchResult(
                    name = "Budget Stay ${destinationQuery.take(10)}",
                    rating = 3.8,
                    priceRange = "${'$'}",
                    amenities = "WiFi, Basic Amenities"
                )
            ),
            restaurants = listOf(
                RestaurantSearchResult(
                    name = "Local Cuisine House",
                    cuisine = "Traditional Local",
                    rating = 4.6,
                    priceRange = "${'$'}${'$'}${'$'}"
                ),
                RestaurantSearchResult(
                    name = "International Bistro",
                    cuisine = "International",
                    rating = 4.4,
                    priceRange = "${'$'}${'$'}"
                ),
                RestaurantSearchResult(
                    name = "Street Food Market",
                    cuisine = "Local Street Food",
                    rating = 4.5,
                    priceRange = "${'$'}"
                )
            ),
            attractions = listOf(
                AttractionSearchResult(
                    name = "Historic City Center",
                    description = "Explore the heart of the city with stunning architecture",
                    duration = "3-4 hours",
                    cost = "${'$'}${'$'}"
                ),
                AttractionSearchResult(
                    name = "Cultural Museum",
                    description = "Learn about local history and traditions",
                    duration = "2-3 hours",
                    cost = "${'$'}${'$'}"
                ),
                AttractionSearchResult(
                    name = "Scenic Viewpoint",
                    description = "Breathtaking views of the surrounding area",
                    duration = "1-2 hours",
                    cost = "${'$'}"
                )
            ),
            tips = "Best to visit during off-peak seasons. Learn basic local phrases. Try local cuisine. Book accommodations in advance.",
            imageUrl = getDestinationImageUrl(destinationQuery)
        )
    }

    // API Response Models
    @Serializable
    data class UnsplashResponse(
        val results: List<UnsplashPhoto>
    )

    @Serializable
    data class UnsplashPhoto(
        val urls: UnsplashUrls
    )

    @Serializable
    data class UnsplashUrls(
        val regular: String
    )

    @Serializable
    data class OpenAIRequest(
        val model: String,
        val messages: List<OpenAIMessage>,
        val maxTokens: Int,
        val temperature: Double
    )

    @Serializable
    data class OpenAIMessage(
        val role: String,
        val content: String
    )

    @Serializable
    data class OpenAIResponse(
        val choices: List<OpenAIChoice>
    )

    @Serializable
    data class OpenAIChoice(
        val message: OpenAIMessage
    )

    // New data models for AI search results
    data class DestinationSearchResult(
        val name: String,
        val country: String,
        val description: String,
        val bestTimeToVisit: String,
        val currency: String,
        val hotels: List<HotelSearchResult>,
        val restaurants: List<RestaurantSearchResult>,
        val attractions: List<AttractionSearchResult>,
        val tips: String,
        val imageUrl: String = ""
    )

    data class HotelSearchResult(
        val name: String,
        val rating: Double,
        val priceRange: String,
        val amenities: String
    )

    data class RestaurantSearchResult(
        val name: String,
        val cuisine: String,
        val rating: Double,
        val priceRange: String
    )

    data class AttractionSearchResult(
        val name: String,
        val description: String,
        val duration: String,
        val cost: String
    )
}
