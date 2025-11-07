package com.runanywhere.startup_hackathon20.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Firebase User Model
 * Stored in Firestore: users/{userId}
 */
data class FirebaseUser(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val countryCode: String = "",
    val phone: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

/**
 * Firebase Destination Model
 * Stored in Firestore: destinations/{destinationId}
 */
data class FirebaseDestination(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val imageUrl: String = "",
    val currencyCode: String = "",
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val description: String = "",
    val hotels: List<FirebaseHotel> = emptyList(),
    val restaurants: List<FirebaseRestaurant> = emptyList(),
    val topReviews: List<FirebaseReview> = emptyList()
)

data class FirebaseHotel(
    val name: String = "",
    val rating: Double = 0.0,
    val pricePerNight: String = "",
    val amenities: List<String> = emptyList(),
    val imageEmoji: String = "🏨"
)

data class FirebaseRestaurant(
    val name: String = "",
    val rating: Double = 0.0,
    val cuisine: String = "",
    val priceRange: String = "",
    val imageEmoji: String = "🍽️"
)

data class FirebaseReview(
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val date: String = "",
    val userEmoji: String = "👤"
)

/**
 * Firebase Plan Model
 * Stored in Firestore: users/{userId}/plans/{planId}
 */
data class FirebasePlan(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val from: String = "",
    val to: String = "",
    val destinationId: String = "",
    val startDate: String = "",
    val nights: Int = 0,
    val budget: Int = 0,
    val people: Int = 1,
    val markdownItinerary: String = "",
    val isLiked: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

/**
 * User Liked Destinations
 * Stored in Firestore: users/{userId}/likedDestinations/{destinationId}
 */
data class LikedDestination(
    @DocumentId
    val destinationId: String = "",
    @ServerTimestamp
    val likedAt: Timestamp? = null
)