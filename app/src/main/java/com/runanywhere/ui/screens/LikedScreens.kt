package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.runanywhere.data.DI
import com.runanywhere.data.api.DestinationApiService
import kotlinx.coroutines.launch

@Composable
fun TravelPlanCardWithImage(
    plan: com.runanywhere.data.model.Plan,
    onOpenPlan: (String) -> Unit
) {
    val repo = remember { DI.repo }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Extract destination name from plan title and get its image
    val destinationName = plan.title.substringBefore("(").substringAfter("→").trim()

    LaunchedEffect(plan.id) {
        scope.launch {
            // Try to get destination from repository first
            val destination = repo.getPopularDestinations().find {
                it.name.contains(destinationName, ignoreCase = true) ||
                        destinationName.contains(it.name, ignoreCase = true)
            }

            if (destination != null) {
                imageUrl = DestinationApiService.getDestinationImageUrl(destination.name)
            } else {
                // Fallback to using the extracted destination name
                imageUrl = DestinationApiService.getDestinationImageUrl(destinationName)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .clickable { onOpenPlan(plan.id) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image or gradient
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = plan.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback blue gradient similar to destination cards
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF87CEEB),
                                    Color(0xFF0EA5E9),
                                    Color(0xFF3B82F6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        destinationName.take(2).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Gradient overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 80f
                        )
                    )
            )

            // Plan information overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Plan title
                Text(
                    plan.title.substringBefore("(").trim(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    maxLines = 2
                )

                // Duration and badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Duration badge if available
                    val durationMatch = Regex("\\((\\d+)\\s*nights?\\)").find(plan.title)
                    if (durationMatch != null) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    durationMatch.groupValues[1] + " nights",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Travel plan badge
                    Surface(
                        color = Color(0xFF0EA5E9).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.Flight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Plan",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllPlansScreen(onOpenPlan: (String) -> Unit) {
    val repo = remember { DI.repo }
    val likedPlanIds = repo.likedPlans.collectAsState().value
    val plansVersion by repo.plansVersion.collectAsState()

    val allPlans = remember(plansVersion) {
        repo.getAllPlans()
    }
    val wishlistPlans = allPlans.filter { likedPlanIds.contains(it.id) }

    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue - same as homepage
                        Color(0xFFB0E0E6), // Powder blue - same as homepage
                        Color(0xFFE0F4FF), // Very light blue - same as homepage
                        Color(0xFFF5FAFF), // Almost white with hint of blue - same as homepage
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header with homepage blue theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Modern icon container with blue gradient
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9),
                                        Color(0xFF3B82F6)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Flight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column {
                        Text(
                            "My Travel Plans",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                        Text(
                            "${allPlans.size} ${if (allPlans.size == 1) "adventure" else "adventures"} awaiting",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (allPlans.isEmpty()) {
            // Enhanced empty state with blue theme
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(12.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9).copy(alpha = 0.2f),
                                        Color(0xFF3B82F6).copy(alpha = 0.1f)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Flight,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFF0EA5E9).copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        "No adventures yet",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Start planning your dream getaway!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(allPlans) { plan ->

                    // Travel Plan Card with destination image
                    TravelPlanCardWithImage(
                        plan = plan,
                        onOpenPlan = onOpenPlan
                    )
                }
            }
        }
    }
}

@Composable
fun LikedPlansScreen(onOpenPlan: (String) -> Unit) {
    val repo = remember { DI.repo }
    val likedPlans by repo.likedPlans.collectAsState()
    val plans = remember(likedPlans) {
        repo.getAllPlans().filter { it.id in likedPlans }
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue - same as homepage
                        Color(0xFFB0E0E6), // Powder blue - same as homepage
                        Color(0xFFE0F4FF), // Very light blue - same as homepage
                        Color(0xFFF5FAFF), // Almost white with hint of blue - same as homepage
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header with blue theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Modern icon container with blue gradient
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9),
                                        Color(0xFF3B82F6)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column {
                        Text(
                            "My Favorites",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                        Text(
                            "${plans.size} ${if (plans.size == 1) "saved plan" else "saved plans"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (plans.isEmpty()) {
            // Enhanced empty state with blue theme
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(12.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9).copy(alpha = 0.2f),
                                        Color(0xFF3B82F6).copy(alpha = 0.1f)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFF0EA5E9).copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        "No favorites yet",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Save your favorite travel plans here!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(plans) { plan ->

                    // Use the same card with images for favorite plans
                    TravelPlanCardWithImage(
                        plan = plan,
                        onOpenPlan = onOpenPlan
                    )
                }
            }
        }
    }
}

@Composable
fun LikedDestinationsScreen(onOpenDestination: (String) -> Unit) {
    val repo = remember { DI.repo }
    val likedDestinations by repo.likedDestinations.collectAsState()
    val places = remember(likedDestinations) {
        repo.getPopularDestinations().filter { it.id in likedDestinations }
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue - same as homepage
                        Color(0xFFB0E0E6), // Powder blue - same as homepage
                        Color(0xFFE0F4FF), // Very light blue - same as homepage
                        Color(0xFFF5FAFF), // Almost white with hint of blue - same as homepage
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header with blue theme - same as homepage
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Modern icon container with blue gradient
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9),
                                        Color(0xFF3B82F6)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Column {
                        Text(
                            "My Wishlist",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 28.sp
                        )
                        Text(
                            "${places.size} ${if (places.size == 1) "destination" else "destinations"} saved",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (places.isEmpty()) {
            // Enhanced empty state with blue theme
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(12.dp, CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9).copy(alpha = 0.2f),
                                        Color(0xFF3B82F6).copy(alpha = 0.1f)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFF0EA5E9).copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        "No saved places yet",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Discover amazing destinations!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Single column grid with same cards as homepage
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(places) { d ->
                    CleanWishlistDestinationCard(
                        destination = d,
                        onOpenDestination = { onOpenDestination(d.id) }
                    )
                }
            }
        }
    }
}

// Clean Wishlist Destination Card without like button - same style as travel plans
@Composable
fun CleanWishlistDestinationCard(
    destination: com.runanywhere.data.model.Destination,
    onOpenDestination: (String) -> Unit
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Load image from Unsplash
    LaunchedEffect(destination.id) {
        scope.launch {
            imageUrl = DestinationApiService.getDestinationImageUrl(destination.name)
        }
    }

    Card(
        onClick = { onOpenDestination(destination.id) },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image or gradient placeholder
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0EA5E9),
                                    Color(0xFF3B82F6),
                                    Color(0xFF6366F1)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        destination.name.take(2).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Card details over image - cleaner layout
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    destination.name,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp,
                    maxLines = 1
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        destination.country,
                        color = Color.White.copy(alpha = 0.95f),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating badge
                    Surface(
                        color = Color(0xFFFBBF24).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "${destination.rating}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Currency badge
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            destination.currencyCode,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Wishlist indicator - small badge
                    Surface(
                        color = Color(0xFFFF6B9D).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "Saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
