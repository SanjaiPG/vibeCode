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
    val thumbnailUrl: String
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
