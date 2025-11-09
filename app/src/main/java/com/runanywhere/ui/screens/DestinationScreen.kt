package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
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
import com.runanywhere.data.model.*
import kotlinx.coroutines.launch

@Composable
fun DestinationScreen(destinationId: String, onMakePlan: () -> Unit) {
    val repo = remember { DI.repo }
    val dest = remember { repo.getDestinationById(destinationId) } ?: return
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // AI-generated description
    var aiGeneratedDescription by remember { mutableStateOf("") }
    var isLoadingDescription by remember { mutableStateOf(true) }

    // Reviews with sentiment
    var reviews by remember { mutableStateOf<List<ReviewWithSentiment>>(emptyList()) }
    var filterRating by remember { mutableStateOf(0) } // 0 = all, 1-5 = specific rating

    // Social data
    var socialData by remember { mutableStateOf<SocialData?>(null) }

    // Budget breakdown
    var estimatedCost by remember { mutableStateOf<BudgetBreakdown?>(null) }

    // Visa requirements
    var visaInfo by remember { mutableStateOf<VisaRequirements?>(null) }

    // Attractions
    var attractions by remember { mutableStateOf<List<Attraction>>(emptyList()) }

    // Selected tab
    var selectedTab by remember { mutableStateOf(0) }

    // Load AI description
    LaunchedEffect(destinationId) {
        scope.launch {
            val description = DestinationApiService.generateAIDescription(dest.name, dest.country)
            aiGeneratedDescription = description
            isLoadingDescription = false
        }
    }

    // Load reviews
    LaunchedEffect(destinationId) {
        scope.launch {
            reviews = DestinationApiService.fetchReviewsFromAPI(dest.name, dest.country)
        }
    }

    // Load social data
    LaunchedEffect(destinationId) {
        scope.launch {
            socialData = DestinationApiService.fetchTrendingHashtags(dest.name)
        }
    }

    // Load budget
    LaunchedEffect(destinationId) {
        scope.launch {
            estimatedCost = DestinationApiService.calculateBudgetForDestination(dest.id, dest.currencyCode)
        }
    }

    // Load visa info
    LaunchedEffect(destinationId) {
        scope.launch {
            visaInfo = DestinationApiService.fetchVisaRequirements(dest.country)
        }
    }

    // Load attractions
    LaunchedEffect(destinationId) {
        scope.launch {
            attractions = DestinationApiService.fetchAttractions(dest.name, dest.lat, dest.lng)
        }
    }

    // Get image URL
    var imageUrl by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(destinationId) {
        scope.launch {
            imageUrl = DestinationApiService.getDestinationImageUrl(dest.name)
        }
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
            .verticalScroll(scrollState)
    ) {
        // Hero Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = dest.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                )
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
                            startY = 200f
                        )
                    )
            )

            // Content overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    dest.name,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        dest.country,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Content Section with Tabs
        Column(Modifier.padding(20.dp)) {
            // Tab Row in Card style
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF3B82F6),
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFF3B82F6),
                                height = 3.dp
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        selectedContentColor = Color(0xFF3B82F6),
                        unselectedContentColor = Color(0xFF6B7280)
                    ) {
                        Text(
                            "Overview",
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        selectedContentColor = Color(0xFF3B82F6),
                        unselectedContentColor = Color(0xFF6B7280)
                    ) {
                        Text(
                            "Hotels",
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        selectedContentColor = Color(0xFF3B82F6),
                        unselectedContentColor = Color(0xFF6B7280)
                    ) {
                        Text(
                            "Food",
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        selectedContentColor = Color(0xFF3B82F6),
                        unselectedContentColor = Color(0xFF6B7280)
                    ) {
                        Text(
                            "Things To Do",
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tab Content
            when (selectedTab) {
                0 -> OverviewTab(
                    dest = dest,
                    aiDescription = aiGeneratedDescription,
                    isLoadingDescription = isLoadingDescription,
                    socialData = socialData,
                    estimatedCost = estimatedCost,
                    visaInfo = visaInfo,
                    reviews = reviews,
                    filterRating = filterRating,
                    onFilterRatingChange = { filterRating = it }
                )
                1 -> HotelsTab(dest.hotels)
                2 -> RestaurantsTab(dest.restaurants)
                3 -> AttractionsTab(attractions)
            }

            Spacer(Modifier.height(24.dp))

            // Make a Plan Button
            Button(
                onClick = onMakePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Make a Plan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun OverviewTab(
    dest: Destination,
    aiDescription: String,
    isLoadingDescription: Boolean,
    socialData: SocialData?,
    estimatedCost: BudgetBreakdown?,
    visaInfo: VisaRequirements?,
    reviews: List<ReviewWithSentiment>,
    filterRating: Int,
    onFilterRatingChange: (Int) -> Unit
) {
    // Filter reviews based on selected rating
    val filteredReviews = reviews.filter { 
        filterRating == 0 || it.rating == filterRating 
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Rating Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Rating",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF6B7280)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${dest.rating}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            " / 5.0",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF6B7280)
                        )
                    }
                    Text(
                        "Based on ${dest.reviewCount} reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = Color(0xFFE5E7EB)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Currency",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        dest.currencyCode,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
            }
        }

        // AI-Generated Description
        Text(
            "About ${dest.name}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (isLoadingDescription) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                }
            } else {
                Text(
                    aiDescription.ifEmpty { dest.description },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp),
                    color = Color(0xFF4B5563),
                    lineHeight = 24.sp
                )
            }
        }

        // Trending Hashtags & Social Proof
        if (socialData != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        " Trending on Social (${socialData.postCount / 1000}k posts)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(socialData.hashtags) { hashtag ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFDBEAFE)
                            ) {
                                Text(
                                    "#$hashtag",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E40AF),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Estimated Budget
        if (estimatedCost != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        " Estimated Budget (per person, 5 days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    BudgetRow("Accommodation", estimatedCost.accommodation, dest.currencyCode)
                    BudgetRow("Food & Dining", estimatedCost.food, dest.currencyCode)
                    BudgetRow("Activities", estimatedCost.activities, dest.currencyCode)
                    BudgetRow("Transport", estimatedCost.transport, dest.currencyCode)

                    Divider(color = Color(0xFFE5E7EB))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            "${dest.currencyCode} ${String.format("%.2f", estimatedCost.total)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // Visa & Travel Requirements
        if (visaInfo != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        " Travel Requirements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Row {
                        Text(
                            "Visa Required: ",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4B5563)
                        )
                        Text(
                            if (visaInfo.visaRequired) " Yes" else " No",
                            color = if (visaInfo.visaRequired) Color(0xFFEF4444) else Color(
                                0xFF10B981
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Passport Validity: ${visaInfo.passportValidity} months",
                        color = Color(0xFF4B5563)
                    )
                    Text(
                        "Processing Time: ${visaInfo.processingDays} days",
                        color = Color(0xFF4B5563)
                    )

                    if (visaInfo.applicationUrl != null) {
                        TextButton(onClick = { /* Navigate to visa application */ }) {
                            Text("Learn More About Visa", color = Color(0xFF3B82F6))
                        }
                    }
                }
            }
        }

        // Reviews Section with Filtering
        Text(
            "Popular Reviews",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        // Rating filter buttons
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    selected = filterRating == 0,
                    onClick = { onFilterRatingChange(0) },
                    label = { Text("All (${reviews.size})") }
                )
            }
            (5 downTo 1).forEach { rating ->
                item {
                    FilterChip(
                        selected = filterRating == rating,
                        onClick = { onFilterRatingChange(rating) },
                        label = {
                            Text("$rating★ (${reviews.count { it.rating == rating }})")
                        }
                    )
                }
            }
        }

        // Review cards
        filteredReviews.forEach { review ->
            ReviewCardWithSentiment(review)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun BudgetRow(label: String, amount: Double, currencyCode: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            "$currencyCode ${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReviewCardWithSentiment(review: ReviewWithSentiment) {
    val sentimentColor = when (review.sentiment) {
        "positive" -> Color(0xFF10B981)
        "negative" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFDBEAFE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(review.userEmoji, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        review.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row {
                        repeat(review.rating) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = sentimentColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        review.sentiment.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = sentimentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563)
            )
            Text(
                review.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun HotelsTab(hotels: List<Hotel>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Hotels (${hotels.size})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        hotels.forEach { hotel ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(hotel.imageEmoji, fontSize = 40.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(hotel.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${hotel.rating}")
                        }
                        Text(hotel.pricePerNight, color = Color(0xFF10B981), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            hotel.amenities.joinToString(", "),
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantsTab(restaurants: List<Restaurant>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Restaurants (${restaurants.size})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        restaurants.forEach { restaurant ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(restaurant.imageEmoji, fontSize = 40.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(restaurant.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(restaurant.cuisine, fontSize = 12.sp, color = Color(0xFF6B7280))
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${restaurant.rating}")
                        }
                        Text(restaurant.priceRange, color = Color(0xFF10B981), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionsTab(attractions: List<Attraction>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            " Things To Do (${attractions.size})",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        attractions.forEach { attraction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = attraction.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(attraction.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${attraction.rating}")
                        }
                        Text("⏱ ${attraction.duration}", fontSize = 12.sp, color = Color(0xFF6B7280))
                        Text(" ${attraction.estimatedCost}", fontSize = 12.sp, color = Color(0xFF10B981))
                        Text(
                            attraction.description,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
