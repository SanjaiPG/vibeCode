package com.runanywhere.startup_hackathon20.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import com.runanywhere.startup_hackathon20.ChatViewModel
import com.runanywhere.startup_hackathon20.ui.navigation.*
import com.runanywhere.startup_hackathon20.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

@Suppress("unused")
@Composable
fun AppRoot() {
    // Check if user is already authenticated with Firebase
    val auth = remember { FirebaseAuth.getInstance() }
    val isUserAuthenticated = auth.currentUser != null

    // Track login state - check Firebase auth state on start
    var isLoggedIn by remember { mutableStateOf(isUserAuthenticated) }

    // Load user data if already authenticated
    LaunchedEffect(isUserAuthenticated) {
        if (isUserAuthenticated) {
            auth.currentUser?.let { firebaseUser ->
                com.runanywhere.data.Repository.onUserLogin(firebaseUser.uid)
            }
        }
    }

    // Simple state-based navigator
    var currentRoute by remember { mutableStateOf(AppRoute.Home.route) }

    // Track previous route for back navigation
    var previousRoute by remember { mutableStateOf<String?>(null) }

    // Track selected destination for plan creation
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    // Track plan ID for editing
    var planIdToEdit by remember { mutableStateOf<String?>(null) }

    // If not logged in, show login screen
    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = {
            isLoggedIn = true
            // Load user data from Firebase after successful login
            auth.currentUser?.let { firebaseUser ->
                com.runanywhere.data.Repository.onUserLogin(firebaseUser.uid)
            }
        })
        return
    }

    // Main app with bottom navigation (shown only after login)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val current = currentRoute
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = { currentRoute = item.route },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Apply Scaffold padding so the content isn't obscured by bars
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            when {
                currentRoute == AppRoute.Home.route -> {
                    HomeScreen(
                        onOpenDestination = { id ->
                            previousRoute = currentRoute
                            currentRoute = "${AppRoute.Destination.base}/$id"
                        },
                        onOpenMap = {
                            previousRoute = currentRoute
                            currentRoute = AppRoute.Map.route
                        },
                        onOpenProfile = {
                            previousRoute = currentRoute
                            currentRoute = AppRoute.Profile.route
                        }
                    )
                }

                currentRoute == AppRoute.Map.route -> {
                    MapScreen(
                        onBack = {
                            currentRoute = AppRoute.Home.route
                        },
                        onMakePlan = { destinationId ->
                            selectedDestinationId = destinationId
                            previousRoute = currentRoute  // Save map route
                            currentRoute = AppRoute.MakePlan.route
                        }
                    )
                }

                currentRoute == AppRoute.Profile.route -> {
                    ProfileScreen(
                        onBack = {
                            currentRoute = AppRoute.Home.route
                        },
                        onLogout = {
                            // Sign out from Firebase and clear user session
                            // IMPORTANT: Clear repository data BEFORE signing out
                            com.runanywhere.data.Repository.onUserLogout()
                            auth.signOut()
                            // Return to login screen
                            isLoggedIn = false
                            currentRoute = AppRoute.Home.route
                        }
                    )
                }

                currentRoute == AppRoute.LikedPlans.route -> {
                    LikedPlansScreen(onOpenPlan = { planId ->
                        previousRoute = currentRoute
                        currentRoute = "${AppRoute.PlanResult.base}/$planId"
                    })
                }

                currentRoute == AppRoute.AllPlans.route -> {
                    AllPlansScreen(onOpenPlan = { planId ->
                        previousRoute = currentRoute
                        currentRoute = "${AppRoute.PlanResult.base}/$planId"
                    })
                }

                currentRoute == AppRoute.LikedDestinations.route -> {
                    LikedDestinationsScreen(onOpenDestination = { id ->
                        previousRoute = currentRoute
                        currentRoute = "${AppRoute.Destination.base}/$id"
                    })
                }

                currentRoute == AppRoute.Chat.route -> {
                    ChatTab(
                        onNavigateToMakePlan = {
                            previousRoute = currentRoute
                            currentRoute = AppRoute.MakePlan.route
                        },
                        onNavigateToHome = {
                            currentRoute = AppRoute.Home.route
                        }
                    )
                }

                currentRoute.startsWith("${AppRoute.Destination.base}/") -> {
                    val id = currentRoute.substringAfter("${AppRoute.Destination.base}/")
                    DestinationScreen(destinationId = id, onMakePlan = {
                        selectedDestinationId = id
                        previousRoute = currentRoute
                        currentRoute = AppRoute.MakePlan.route
                    })
                }

                currentRoute == AppRoute.MakePlan.route -> {
                    MakePlanScreen(
                        destinationId = selectedDestinationId,
                        planIdToEdit = planIdToEdit,
                        onPlanCreated = { planId ->
                            planIdToEdit = null // Clear edit mode
                            currentRoute = "${AppRoute.PlanResult.base}/$planId"
                        },
                        onNavigateToChat = {
                            currentRoute = AppRoute.Chat.route
                        },
                        onBack = if (previousRoute != null) {
                            {
                                planIdToEdit = null // Clear edit mode on back
                                currentRoute = previousRoute!!
                                previousRoute = null
                            }
                        } else null
                    )
                }

                currentRoute.startsWith("${AppRoute.PlanResult.base}/") -> {
                    val planId = currentRoute.substringAfter("${AppRoute.PlanResult.base}/")
                    PlanResultScreen(
                        planId = planId,
                        onNavigateToEditPlan = { editPlanId ->
                            planIdToEdit = editPlanId
                            previousRoute = currentRoute
                            currentRoute = AppRoute.MakePlan.route
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatTab(
    viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToMakePlan: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    com.runanywhere.startup_hackathon20.ChatScreen(
        viewModel = viewModel,
        onNavigateToMakePlan = onNavigateToMakePlan,
        onNavigateToHome = onNavigateToHome
    )
}
