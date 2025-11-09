package com.runanywhere.startup_hackathon20.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
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

    // Track destination name from search (when not from repository)
    var selectedDestinationName by remember { mutableStateOf<String?>(null) }

    // Track plan ID for editing
    var planIdToEdit by remember { mutableStateOf<String?>(null) }

    // Track search query for AI-powered search
    var searchQuery by remember { mutableStateOf<String?>(null) }

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

    // Main app with floating bottom navigation
    Box(modifier = Modifier.fillMaxSize()) {
        // Content area with bottom padding for floating bar
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 88.dp)
        ) {
            when {
                // Search Results Screen (AI-powered search)
                searchQuery != null -> {
                    SearchResultsScreen(
                        searchQuery = searchQuery!!,
                        onBack = {
                            searchQuery = null
                        },
                        onMakePlan = { destinationName ->
                            // Pass destination name from search results
                            selectedDestinationId = null
                            selectedDestinationName = destinationName
                            searchQuery = null
                            previousRoute = AppRoute.Home.route
                            currentRoute = AppRoute.MakePlan.route
                        }
                    )
                }

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
                        },
                        onSearch = { query ->
                            // Trigger AI-powered search
                            if (query.isNotBlank()) {
                                searchQuery = query
                            }
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
                            selectedDestinationName = null
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
                        selectedDestinationName = null
                        previousRoute = currentRoute
                        currentRoute = AppRoute.MakePlan.route
                    })
                }

                currentRoute == AppRoute.MakePlan.route -> {
                    MakePlanScreen(
                        destinationId = selectedDestinationId,
                        destinationName = selectedDestinationName,
                        planIdToEdit = planIdToEdit,
                        onPlanCreated = { planId ->
                            planIdToEdit = null // Clear edit mode
                            selectedDestinationId = null // Clear destination
                            selectedDestinationName = null // Clear destination name
                            currentRoute = AppRoute.AllPlans.route // Navigate to My Travel Plans
                        },
                        onNavigateToChat = {
                            currentRoute = AppRoute.Chat.route
                        },
                        onBack = if (previousRoute != null) {
                            {
                                planIdToEdit = null // Clear edit mode on back
                                selectedDestinationId = null // Clear destination
                                selectedDestinationName = null // Clear destination name
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
                        },
                        onBack = {
                            // Navigate to All Plans screen after deletion
                            currentRoute = AppRoute.AllPlans.route
                        }
                    )
                }
            }
        }

        // Floating Bottom Navigation Bar
        FloatingBottomBar(
            currentRoute = currentRoute,
            onNavigate = { route -> currentRoute = route },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        )
    }
}

@Composable
fun ChatTab(
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

@Composable
fun FloatingBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false
            )
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE0F4FF), // Very light blue
                        Color(0xFFF5FAFF), // Almost white with hint of blue
                        Color(0xFFE0F4FF)  // Very light blue
                    )
                )
            ),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomItems.forEach { item ->
                val isSelected = currentRoute == item.route
                FloatingBottomBarItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FloatingBottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .size(52.dp),
        color = if (isSelected) {
            Color(0xFF3B82F6).copy(alpha = 0.2f)
        } else {
            Color.Transparent
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Icon only
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF3B82F6) else Color(0xFF64748B),
                modifier = Modifier.size(if (isSelected) 28.dp else 26.dp)
            )
        }
    }
}
