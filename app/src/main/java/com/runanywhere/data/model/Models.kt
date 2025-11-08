package com.runanywhere.startup_hackathon20.data.model

data class Destination(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String,
    val currencyCode: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 2453,
    val description: String = "Explore the beauty and culture of this amazing destination. Experience unforgettable adventures, stunning landscapes, and rich history that will make your journey truly memorable.",
    val hotels: List<Hotel> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val topReviews: List<Review> = emptyList()
)

data class Hotel(
    val name: String,
    val rating: Double,
    val pricePerNight: String,
    val amenities: List<String>,
    val imageEmoji: String = "🏨"
)

data class Restaurant(
    val name: String,
    val rating: Double,
    val cuisine: String,
    val priceRange: String,
    val imageEmoji: String = "🍽️"
)

data class Review(
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val userEmoji: String = "👤"
)

data class Attraction(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val name: String = title,
    val rating: Double = 4.5,
    val duration: String = "2-3 hours",
    val estimatedCost: String = "$$",
    val imageUrl: String = thumbnailUrl
)

data class PlanForm(
    val from: String,
    val to: String,
    val startDate: String,
    val nights: Int,
    val budget: Int,
    val people: Int
)

data class Plan(
    val id: String,
    val title: String,
    val markdownItinerary: String,
    val destinationId: String
)

data class User(
    val username: String,
    val name: String,
    val email: String,
    val countryCode: String,
    val phone: String
)

// New data models for enhanced features
data class WeatherInfo(
    val bestSeason: String,
    val avgTemp: String,
    val rainfallLevel: String,
    val currentTemp: Double? = null,
    val condition: String? = null
)

data class BudgetBreakdown(
    val accommodation: Double,
    val food: Double,
    val activities: Double,
    val transport: Double,
    val total: Double
)

data class VisaRequirements(
    val visaRequired: Boolean,
    val passportValidity: Int, // months
    val processingDays: Int,
    val visaType: String? = null,
    val applicationUrl: String? = null
)

data class ReviewWithSentiment(
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val userEmoji: String = "👤",
    val sentiment: String = "neutral" // positive, neutral, negative
)

data class SocialData(
    val hashtags: List<String>,
    val postCount: Int
)
