package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.data.api.DestinationApiService
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String,
    onBack: () -> Unit,
    onMakePlan: (String) -> Unit // destinationName parameter for MakePlan
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var destinationResult by remember {
        mutableStateOf<DestinationApiService.DestinationSearchResult?>(
            null
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch destination details from ChatGPT
    LaunchedEffect(searchQuery) {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null
                android.util.Log.d("SearchResultsScreen", "Starting search for: $searchQuery")

                // searchDestinationWithAI now ALWAYS returns a result (uses fallback if API fails)
                val result = DestinationApiService.searchDestinationWithAI(searchQuery)
                destinationResult = result
                android.util.Log.d(
                    "SearchResultsScreen",
                    "Successfully loaded destination: ${result.name}"
                )

            } catch (e: Exception) {
                android.util.Log.e("SearchResultsScreen", "Error: ${e.message}", e)
                errorMessage = "Error loading destination: ${e.message}"
            } finally {
                isLoading = false
                android.util.Log.d("SearchResultsScreen", "Loading complete")
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Header with Back Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2563EB)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Search Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        "for \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Content
        when {
            isLoading -> {
                // Loading State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(64.dp),
                            color = Color(0xFF2563EB),
                            strokeWidth = 4.dp
                        )
                        Text(
                            "🌍 Searching destination details...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "Fetching hotels, restaurants & attractions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            errorMessage != null -> {
                // Error State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFFEF4444)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Oops! Something went wrong",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }

            destinationResult != null -> {
                // Success State - Show destination details
                DestinationDetailsContent(
                    destination = destinationResult!!,
                    onMakePlan = { onMakePlan(destinationResult!!.name) }
                )
            }
        }
    }
}

@Composable
private fun DestinationDetailsContent(
    destination: DestinationApiService.DestinationSearchResult,
    onMakePlan: () -> Unit
) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Destination Image Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(destination.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1506905925346-21bda4d32df4" })
                            .crossfade(true)
                            .build(),
                        contentDescription = destination.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Title
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            destination.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            fontSize = 32.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                destination.country,
                                style = MaterialTheme.typography.titleMedium,
                                color = White,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // Info Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Best Time",
                        value = destination.bestTimeToVisit,
                        icon = "🌤️",
                        backgroundColor = Color(0xFFDBEAFE)
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Currency",
                        value = destination.currency,
                        icon = "💰",
                        backgroundColor = Color(0xFFDCFCE7)
                    )
                }
            }

            // Description
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "✨ About",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            destination.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF475569),
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Hotels Section
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "🏨 Hotels",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(12.dp))
            }

            items(destination.hotels) { hotel ->
                HotelCard(hotel = hotel)
            }

            // Restaurants Section
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "🍽️ Restaurants",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(12.dp))
            }

            items(destination.restaurants) { restaurant ->
                RestaurantCard(restaurant = restaurant)
            }

            // Attractions Section
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "📍 Top Attractions",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(12.dp))
            }

            items(destination.attractions) { attraction ->
                AttractionCard(attraction = attraction)
            }

            // Travel Tips
            item {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "💡 Travel Tips",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            destination.tips,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF92400E),
                            lineHeight = 22.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // Fixed "Make a Plan" Button at Bottom
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onMakePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text(
                    "✨ Make a Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: String,
    backgroundColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF1E293B).copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
private fun HotelCard(hotel: DestinationApiService.HotelSearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2563EB).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🏨", fontSize = 28.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            hotel.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        "•",
                        color = Color(0xFF64748B)
                    )
                    Text(
                        hotel.priceRange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    hotel.amenities,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RestaurantCard(restaurant: DestinationApiService.RestaurantSearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF59E0B).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍽️", fontSize = 28.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        restaurant.cuisine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        "•",
                        color = Color(0xFF64748B)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            restaurant.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        "•",
                        color = Color(0xFF64748B)
                    )
                    Text(
                        restaurant.priceRange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttractionCard(attraction: DestinationApiService.AttractionSearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    attraction.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    attraction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "⏱️ ${attraction.duration}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3730A3)
                        )
                    }
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "💰 ${attraction.cost}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        }
    }
}
