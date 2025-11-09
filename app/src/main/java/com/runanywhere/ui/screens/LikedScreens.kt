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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun AllPlansScreen(onOpenPlan: (String) -> Unit) {
    val repo = remember { DI.repo }
    val likedPlanIds = repo.likedPlans.collectAsState().value
    val plansVersion by repo.plansVersion.collectAsState()

    val allPlans = remember(plansVersion) {
        repo.getAllPlans()
    }
    val wishlistPlans = allPlans.filter { likedPlanIds.contains(it.id) }
    // Derive all plans reactively - recompose when plansVersion changes (new plan created)


    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFFB0E0E6), // Powder blue
                        Color(0xFFE0F4FF), // Very light blue
                        Color(0xFFF5FAFF), // Almost white with hint of blue
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header integrated directly on sky gradient - no blue box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.List,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column {
                Text(
                    "My Travel Plans",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "${allPlans.size} ${if (allPlans.size == 1) "plan" else "plans"} generated",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }

        if (allPlans.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Color(0xFF3B82F6).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF3B82F6).copy(alpha = 0.5f)
                        )
                    }
                    Text(
                        "No plans yet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Create your first travel plan!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allPlans) { p ->
                    val isLiked = likedPlanIds.contains(p.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlan(p.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon with gradient background
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF0EA5E9),
                                                    Color(0xFF3B82F6)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Flight,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        p.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Tap to view full itinerary",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF6B7280)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFDBEAFE),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            "Travel Plan",
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF1E40AF),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Like Button in Top Right Corner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isLiked) {
                                            repo.unlikePlan(p.id)
                                        } else {
                                            repo.likePlan(p.id)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = if (isLiked) "Remove from favorites" else "Add to favorites",
                                        tint = if (isLiked) Color(0xFF3B82F6) else Color(0xFF9CA3AF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
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
        repo.getAllPlans()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFFB0E0E6), // Powder blue
                        Color(0xFFE0F4FF), // Very light blue
                        Color(0xFFF5FAFF), // Almost white with hint of blue
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header integrated directly on sky gradient - no blue box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column {
                Text(
                    "My Cart ",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "${plans.size} itineraries saved",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }

        if (plans.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Color(0xFF3B82F6).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF3B82F6).copy(alpha = 0.5f)
                        )
                    }
                    Text(
                        "No saved plans yet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Start planning your adventures!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(plans) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlan(p.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon with gradient background
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF0EA5E9),
                                                    Color(0xFF3B82F6)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }

                                Spacer(Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        p.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Tap to view full itinerary",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF6B7280)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFDBEAFE),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            "Saved Plan",
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF1E40AF),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Wishlist Button in Top Right Corner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                IconButton(
                                    onClick = {
                                        repo.unlikePlan(p.id)
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = "Remove from Cart",
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
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
                        Color(0xFF87CEEB), // Sky blue
                        Color(0xFFB0E0E6), // Powder blue
                        Color(0xFFE0F4FF), // Very light blue
                        Color(0xFFF5FAFF), // Almost white with hint of blue
                        Color.White,        // Pure white
                        Color.White         // Pure white continues
                    ),
                    startY = 0f,
                    endY = 3000f
                )
            )
    ) {
        // Header integrated directly on sky gradient - no blue box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF0EA5E9),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column {
                Text(
                    "Wishlist ",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "${places.size} destinations saved",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }

        if (places.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Color(0xFF0EA5E9).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF0EA5E9).copy(alpha = 0.5f)
                        )
                    }
                    Text(
                        "No saved places yet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "Discover amazing destinations!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B7280)
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
                    WishlistDestinationCard(
                        destination = d,
                        isLiked = true,
                        onToggleLike = {
                            repo.unlikeDestination(d.id)
                        },
                        onOpenDestination = { onOpenDestination(d.id) }
                    )
                }
            }
        }
    }
}

// Wishlist Destination Card - same style as homepage but optimized for single column
@Composable
fun WishlistDestinationCard(
    destination: com.runanywhere.data.model.Destination,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
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
            .height(280.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
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
                                    Color(0xFF2563EB)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        destination.name.take(2).uppercase(),
                        style = MaterialTheme.typography.displayLarge,
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
                            startY = 150f
                        )
                    )
            )

            // Card details over image
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    destination.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "${destination.rating}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Currency badge
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            destination.currencyCode,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Like button in top right
            IconButton(
                onClick = {
                    onToggleLike()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(48.dp)
            ) {
                Icon(
                    if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Unlike",
                    tint = if (isLiked) Color(0xFFEF4444) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
