package com.runanywhere.startup_hackathon20.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(val route: String) {
    data object Home: AppRoute("home")
    data object Map : AppRoute("map")
    data object Profile : AppRoute("profile")
    data object AllPlans : AppRoute("all_plans")
    data object LikedPlans: AppRoute("liked_plans")
    data object LikedDestinations: AppRoute("liked_destinations")
    data object Chat: AppRoute("chat")

    data object Destination: AppRoute("destination/{id}") { const val base = "destination" }
    data object MakePlan: AppRoute("make_plan")
    data object PlanResult: AppRoute("plan_result/{planId}") { const val base = "plan_result" }
}

@Immutable
data class BottomItem(val route: String, val label: String, val icon: ImageVector)

val bottomItems = listOf(
    BottomItem(AppRoute.Home.route, "Home", Icons.Filled.Home),
    BottomItem(AppRoute.LikedDestinations.route, "Wishlist", Icons.Filled.Favorite),
    BottomItem(AppRoute.AllPlans.route, "My Plans", Icons.Filled.List),
    BottomItem(AppRoute.Chat.route, "Chatbot", Icons.Filled.Info),
)
