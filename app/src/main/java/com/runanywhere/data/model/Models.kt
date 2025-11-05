package com.runanywhere.startup_hackathon20.data.model

data class Destination(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lng: Double,
    val imageUrl: String,
    val currencyCode: String
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
    val email: String,
    val name: String,
    val phone: String,
    val location: String
)
