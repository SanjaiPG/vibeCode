package com.runanywhere.startup_hackathon20.ui.screens


import com.runanywhere.startup_hackathon20.data.model.User
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.data.DI
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SwapHoriz
import com.runanywhere.startup_hackathon20.data.api.DestinationApiService
import com.runanywhere.startup_hackathon20.data.model.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDestination: (String) -> Unit,
    onOpenMap: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val repo = remember { DI.repo }
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val all = remember { repo.getPopularDestinations() }
    val filtered = all.filter { it.name.contains(query.text, true) || it.country.contains(query.text, true) }
    
    // Real-time search with online destinations
    var searchResults by remember { mutableStateOf<List<Destination>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Comparison feature
    var selectedForComparison by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showComparisonDialog by remember { mutableStateOf(false) }
    
    // Expandable cards
    var expandedCard by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    
    // Online search when user types
    LaunchedEffect(query.text) {
        if (query.text.isNotEmpty() && query.text.length >= 2) {
            isSearching = true
            scope.launch {
                val results = DestinationApiService.searchDestinationsOnline(query.text)
                searchResults = results
                isSearching = false
            }
        } else {
            searchResults = emptyList()
            isSearching = false
        }
    }

    // Use StateFlow for reactive updates
    val likedDestinations by repo.likedDestinations.collectAsState()
    val likedDestinationsList = likedDestinations.toList()
    val likedPlans by repo.likedPlans.collectAsState()
    val plansVersion by repo.plansVersion.collectAsState()
    val user by repo.currentUser.collectAsState()
    val userName = user?.name ?: "Traveler"


    // Get current user for greeting - make it reactive to changes



    // Get all plans count - will update when plansVersion changes (increments when new plan is created)
    val allPlansCount = remember(plansVersion) {
        repo.getAllPlans().size
    }

    // Map view state - can be: "compact" (default), "expanded"
    var mapViewState by remember { mutableStateOf("compact") }
    var selectedDestinationOnMap by remember { mutableStateOf<com.runanywhere.startup_hackathon20.data.model.Destination?>(null) }

    // Center camera on first destination or world center
    val initialPosition = if (filtered.isNotEmpty()) {
        LatLng(20.0, 0.0)
    } else {
        LatLng(0.0, 0.0)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 2f)
    }

    // Show full screen map when expanded
    if (mapViewState == "expanded") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C4A6E))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false,
                    compassEnabled = true,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = false,
                    zoomGesturesEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                )
            ) {
                // Add markers for filtered destinations
                filtered.forEach { destination ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(destination.lat, destination.lng)
                        ),
                        title = destination.name,
                        snippet = "${destination.country} - ${destination.rating}⭐",
                        onClick = {
                            selectedDestinationOnMap = destination
                            false
                        }
                    )
                }
            }

            // Close button
            IconButton(
                onClick = { mapViewState = "compact" },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.95f), CircleShape)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Collapse Map",
                    tint = Color(0xFF0EA5E9),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Map label
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "${filtered.size} destinations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
            }

            // Zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.move(
                            CameraUpdateFactory.zoomTo((currentZoom + 1f).coerceAtMost(20f))
                        )
                    },
                    containerColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(
                        "+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.move(
                            CameraUpdateFactory.zoomTo((currentZoom - 1f).coerceAtLeast(2f))
                        )
                    },
                    containerColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(
                        "−",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(initialPosition, 2f)
                        )
                    },
                    containerColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Reset",
                        tint = Color(0xFF0EA5E9)
                    )
                }
            }
        }

        // Destination Details Dialog from map marker click (show over expanded map)
        if (selectedDestinationOnMap != null) {
            DestinationDetailsDialog(
                destination = selectedDestinationOnMap!!,
                isLiked = likedDestinations.contains(selectedDestinationOnMap!!.id),
                onDismiss = { selectedDestinationOnMap = null },
                onToggleLike = { id ->
                    if (likedDestinations.contains(id)) {
                        repo.unlikeDestination(id)
                    } else {
                        repo.likeDestination(id)
                    }
                },
                onMakePlan = {
                    selectedDestinationOnMap = null
                    onOpenDestination(it)
                }
            )
        }

        return
    }

    // Normal view when map is not expanded
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F9FF))
    ) {
        // User Profile Section at Top with blue gradient - FIXED
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0EA5E9),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User Profile Avatar with better styling - Make it clickable
                        Surface(
                            onClick = onOpenProfile,
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Profile",
                                    tint = Color(0xFF0EA5E9),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                "Hello, $userName! ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Where do you want to go?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar - FIXED
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0EA5E9),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Bar
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            placeholder = {
                                Text(
                                    " Search destinations...",
                                    color = Color.Gray.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF0EA5E9)
                                )
                            },
                            trailingIcon = {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF0EA5E9)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )
                        
                        // Search suggestions dropdown
                        if (searchResults.isNotEmpty() && query.text.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                    .padding(top = 56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(searchResults) { suggestion ->
                                        SearchSuggestionItem(
                                            destination = suggestion,
                                            onClick = {
                                                query = TextFieldValue(suggestion.name)
                                                searchResults = emptyList()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SCROLLABLE CONTENT - Everything below the search bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Quick Stats Cards - Blue Theme with more cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Destinations Count Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCFCE7)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFF10B981).copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Place,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    "${filtered.size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    "Places",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF065F46).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Wishlist Count Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDBEAFE)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFF3B82F6).copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    "${likedDestinations.size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    "Saved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1E40AF).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Additional Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Countries Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFFF59E0B).copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🌍",
                                    fontSize = 24.sp
                                )
                            }
                            Column {
                                Text(
                                    "${all.map { it.country }.distinct().size}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    "Countries",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF92400E).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Plans Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0E7FF)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFF6366F1).copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "✈️",
                                    fontSize = 24.sp
                                )
                            }
                            Column {
                                Text(
                                    "$allPlansCount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3730A3)
                                )
                                Text(
                                    "Plans",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF3730A3).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Map View Section (Always visible, inline, NOT expanded style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(300.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false,
                            compassEnabled = true,
                            rotationGesturesEnabled = true,
                            scrollGesturesEnabled = true,
                            tiltGesturesEnabled = false,
                            zoomGesturesEnabled = true
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = false,
                            mapType = MapType.NORMAL
                        )
                    ) {
                        // Add markers for filtered destinations
                        filtered.forEach { destination ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(destination.lat, destination.lng)
                                ),
                                title = destination.name,
                                snippet = "${destination.country} - ${destination.rating}⭐",
                                onClick = {
                                    selectedDestinationOnMap = destination
                                    false
                                }
                            )
                        }
                    }

                    // Map controls overlay - expand/collapse button
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Expand/Collapse button for fullscreen map
                        FloatingActionButton(
                            onClick = {
                                mapViewState =
                                    if (mapViewState == "compact") "expanded" else "compact"
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text(
                                if (mapViewState == "compact") "⛶" else "✕",
                                fontSize = 20.sp,
                                color = Color(0xFF0EA5E9)
                            )
                        }
                    }

                    // Zoom controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                val currentZoom = cameraPositionState.position.zoom
                                cameraPositionState.move(
                                    CameraUpdateFactory.zoomTo((currentZoom + 1f).coerceAtMost(20f))
                                )
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text(
                                "+",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                val currentZoom = cameraPositionState.position.zoom
                                cameraPositionState.move(
                                    CameraUpdateFactory.zoomTo((currentZoom - 1f).coerceAtLeast(2f))
                                )
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text(
                                "−",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                        }
                    }

                    // Map label
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF0EA5E9),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "${filtered.size} destinations",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Famous Destinations Section Header - removed toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "Famous Destinations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Enhanced Destination Cards with Images, Expandable Tabs, and Comparison
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                filtered.forEach { d ->
                    EnhancedDestinationCard(
                        destination = d,
                        isLiked = likedDestinations.contains(d.id),
                        isSelectedForComparison = selectedForComparison.contains(d.id),
                        isExpanded = expandedCard == d.id,
                        selectedTab = selectedTab,
                        onToggleLike = {
                            if (likedDestinations.contains(d.id)) {
                                repo.unlikeDestination(d.id)
                            } else {
                                repo.likeDestination(d.id)
                            }
                        },
                        onToggleComparison = {
                            selectedForComparison = if (selectedForComparison.contains(d.id)) {
                                selectedForComparison - d.id
                            } else {
                                selectedForComparison + d.id
                            }
                        },
                        onExpand = {
                            expandedCard = if (expandedCard == d.id) null else d.id
                        },
                        onTabSelected = { tabIndex ->
                            selectedTab = tabIndex
                        },
                        onOpenDestination = { onOpenDestination(d.id) }
                    )
                }
            }
            

            Spacer(Modifier.height(16.dp))
        }
    }
    
    // Comparison Floating Action Button (outside scrollable content)
    if (selectedForComparison.size >= 2) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { showComparisonDialog = true },
                containerColor = Color(0xFF0EA5E9),
                modifier = Modifier.size(64.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.SwapHoriz,
                        contentDescription = "Compare",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "${selectedForComparison.size}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Comparison Dialog
    if (showComparisonDialog) {
        ComparisonDialog(
            destinationIds = selectedForComparison.toList(),
            destinations = all.filter { selectedForComparison.contains(it.id) },
            onDismiss = { showComparisonDialog = false },
            onClear = { 
                selectedForComparison = emptySet()
                showComparisonDialog = false
            }
        )
    }

    // Destination Details Dialog from map marker click
    if (selectedDestinationOnMap != null) {
        DestinationDetailsDialog(
            destination = selectedDestinationOnMap!!,
            isLiked = likedDestinations.contains(selectedDestinationOnMap!!.id),
            onDismiss = { selectedDestinationOnMap = null },
            onToggleLike = { id ->
                if (likedDestinations.contains(id)) {
                    repo.unlikeDestination(id)
                } else {
                    repo.likeDestination(id)
                }
            },
            onMakePlan = {
                selectedDestinationOnMap = null
                onOpenDestination(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    onMakePlan: (String) -> Unit = {}
) {
    val repo = remember { DI.repo }
    val destinations = remember { repo.getPopularDestinations() }
    val likedDestinations by repo.likedDestinations.collectAsState()
    val likedDestinationsList = likedDestinations.toList()

    var selectedDestination by remember { mutableStateOf<com.runanywhere.startup_hackathon20.data.model.Destination?>(null) }
    var showListView by remember { mutableStateOf(false) }

    // Center camera on first destination or world center
    val initialPosition = if (destinations.isNotEmpty()) {
        LatLng(20.0, 0.0)
    } else {
        LatLng(0.0, 0.0)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 2f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C4A6E))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0EA5E9).copy(alpha = 0.95f),
                                    Color(0xFF0284C7).copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    "🗺️ World Map",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "${destinations.size} destinations",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // List View Toggle
                        IconButton(
                            onClick = { showListView = !showListView },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (showListView) Color.White else Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Filled.List,
                                contentDescription = if (showListView) "Map View" else "List View",
                                tint = if (showListView) Color(0xFF0EA5E9) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Main Content
            if (showListView) {
                // List View
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val groupedByCountry = destinations.groupBy { it.country }

                    groupedByCountry.forEach { (country, countryDestinations) ->
                        item {
                            CountryHeader(country = country, count = countryDestinations.size)
                        }

                        items(countryDestinations) { destination ->
                            MapDestinationCard(
                                destination = destination,
                                isLiked = likedDestinations.contains(destination.id),
                                onClick = { selectedDestination = destination },
                                onToggleLike = {
                                    if (likedDestinations.contains(destination.id)) {
                                        repo.unlikeDestination(destination.id)
                                    } else {
                                        repo.likeDestination(destination.id)
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // Google Maps View
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false,
                            compassEnabled = true,
                            rotationGesturesEnabled = true,
                            scrollGesturesEnabled = true,
                            tiltGesturesEnabled = false,
                            zoomGesturesEnabled = true
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = false,
                            mapType = MapType.NORMAL
                        )
                    ) {
                        // Add markers for all destinations
                        destinations.forEach { destination ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(destination.lat, destination.lng)
                                ),
                                title = destination.name,
                                snippet = "${destination.country} - ${destination.rating}⭐",
                                onClick = {
                                    selectedDestination = destination
                                    false
                                }
                            )
                        }
                    }

                    // Custom Zoom Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                val currentZoom = cameraPositionState.position.zoom
                                cameraPositionState.move(
                                    CameraUpdateFactory.zoomTo((currentZoom + 1f).coerceAtMost(20f))
                                )
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(
                                "+",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                val currentZoom = cameraPositionState.position.zoom
                                cameraPositionState.move(
                                    CameraUpdateFactory.zoomTo((currentZoom - 1f).coerceAtLeast(2f))
                                )
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(
                                "−",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(initialPosition, 2f)
                                )
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Reset",
                                tint = Color(0xFF0EA5E9)
                            )
                        }
                    }
                }
            }
        }

        // Destination Details Dialog
        if (selectedDestination != null) {
            DestinationDetailsDialog(
                destination = selectedDestination!!,
                isLiked = likedDestinations.contains(selectedDestination!!.id),
                onDismiss = { selectedDestination = null },
                onToggleLike = { id ->
                    if (likedDestinations.contains(id)) {
                        repo.unlikeDestination(id)
                    } else {
                        repo.likeDestination(id)
                    }
                },
                onMakePlan = {
                    selectedDestination = null
                    onMakePlan(it)
                }
            )
        }
    }
}


@Composable
fun EmptyStateView(onClearFilters: () -> Unit) {
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
            Text(
                "🔍",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "No destinations found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Try a different search or filter",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text("Clear Filters", color = Color(0xFF0EA5E9))
            }
        }
    }
}

@Composable
fun CountryHeader(country: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                country,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            "$count",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MapDestinationCard(
    destination: com.runanywhere.startup_hackathon20.data.model.Destination,
    isLiked: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Destination Icon with Gradient
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        destination.name.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Destination Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    destination.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF6B7280)
                    )
                    Text(
                        destination.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFFBBF24)
                        )
                        Text(
                            "${destination.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    // Currency
                    Surface(
                        color = Color(0xFFDBEAFE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            destination.currencyCode,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Hotels count
                    if (destination.hotels.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🏨", fontSize = 14.sp)
                            Text(
                                "${destination.hotels.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Like Button
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isLiked) Color(0xFFDBEAFE) else Color(0xFFF3F4F6),
                        CircleShape
                    )
            ) {
                Icon(
                    if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) Color(0xFF3B82F6) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DestinationMapCard(
    destination: com.runanywhere.startup_hackathon20.data.model.Destination,
    isLiked: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder with gradient
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
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
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    destination.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFBBF24)
                    )
                    Text(
                        "${destination.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        "(${destination.reviewCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFDBEAFE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            destination.currencyCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (destination.hotels.isNotEmpty()) {
                        Text(
                            "🏨 ${destination.hotels.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                    if (destination.restaurants.isNotEmpty()) {
                        Text(
                            "🍽️ ${destination.restaurants.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Icon(
                if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Liked",
                tint = if (isLiked) Color(0xFF3B82F6) else Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDetailsDialog(
    destination: com.runanywhere.startup_hackathon20.data.model.Destination,
    isLiked: Boolean,
    onDismiss: () -> Unit,
    onToggleLike: (String) -> Unit,
    onMakePlan: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9),
                                        Color(0xFF3B82F6),
                                        Color(0xFF2563EB)
                                    )
                                )
                            )
                    )

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Text("✕", fontSize = 20.sp, color = Color.White)
                    }

                    // Destination name and info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            destination.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                destination.country,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                "• ${destination.currencyCode}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Rating & Wishlist Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    "${destination.rating} / 5.0",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    "${destination.reviewCount} reviews",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        // Wishlist button
                        IconButton(
                            onClick = { onToggleLike(destination.id) },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isLiked) Color(0xFFDBEAFE) else Color(0xFFF3F4F6),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isLiked) "Remove from Wishlist" else "Add to Wishlist",
                                tint = if (isLiked) Color(0xFF3B82F6) else Color(0xFF6B7280),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Divider(color = Color(0xFFE5E7EB))

                    // About Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "About",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            destination.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4B5563),
                            lineHeight = 24.sp
                        )
                    }

                    // Hotels Section
                    if (destination.hotels.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🏨", fontSize = 24.sp)
                                Text(
                                    "Top Hotels (${destination.hotels.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            destination.hotels.forEach { hotel ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF9FAFB)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    hotel.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1F2937)
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Star,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = Color(0xFFFBBF24)
                                                    )
                                                    Text(
                                                        "${hotel.rating}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF1F2937)
                                                    )
                                                }
                                            }
                                            Text(
                                                hotel.pricePerNight,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                        // Amenities
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            hotel.amenities.take(3).forEach { amenity ->
                                                Surface(
                                                    color = Color(0xFFDBEAFE),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        amenity,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color(0xFF1E40AF),
                                                        modifier = Modifier.padding(
                                                            horizontal = 6.dp,
                                                            vertical = 3.dp
                                                        )
                                                    )
                                                }
                                            }
                                            if (hotel.amenities.size > 3) {
                                                Text(
                                                    "+${hotel.amenities.size - 3}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Restaurants Section
                    if (destination.restaurants.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🍽️", fontSize = 24.sp)
                                Text(
                                    "Top Restaurants (${destination.restaurants.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            destination.restaurants.forEach { restaurant ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF9FAFB)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                restaurant.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1F2937)
                                            )
                                            Text(
                                                restaurant.cuisine,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF6B7280)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Star,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = Color(0xFFFBBF24)
                                                    )
                                                    Text(
                                                        "${restaurant.rating}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF1F2937)
                                                    )
                                                }
                                                Text(
                                                    "• ${restaurant.priceRange}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF059669),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                        Text(
                                            restaurant.imageEmoji,
                                            fontSize = 32.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Reviews Section
                    if (destination.topReviews.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("💬", fontSize = 24.sp)
                                Text(
                                    "Popular Reviews",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            destination.topReviews.forEach { review ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF9FAFB)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(review.userEmoji, fontSize = 32.sp)
                                                Column {
                                                    Text(
                                                        review.userName,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1F2937)
                                                    )
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            2.dp
                                                        )
                                                    ) {
                                                        repeat(review.rating) {
                                                            Icon(
                                                                Icons.Filled.Star,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp),
                                                                tint = Color(0xFFFBBF24)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Text(
                                                review.date,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF9CA3AF)
                                            )
                                        }
                                        Text(
                                            review.comment,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF4B5563),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Make a Plan Button
                    Button(
                        onClick = { onMakePlan(destination.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text(
                            "✈️ Make a Plan to ${destination.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val repo = remember { DI.repo }
    // Get fresh user data each time the composable recomposes instead of using remember
    val user = repo.getCurrentUser()

    var isEditing by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }

    // Initialize with current user data, update when user changes
    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var username by remember(user) { mutableStateOf(user?.username ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var countryCode by remember(user) { mutableStateOf(user?.countryCode ?: "+91") }
    var phone by remember(user) { mutableStateOf(user?.phone ?: "") }

    // Password change fields
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordSuccess by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F9FF))
    ) {
        // Profile Header with blue gradient
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0EA5E9),
                                Color(0xFF3B82F6)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Profile content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Profile picture
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0EA5E9),
                                    Color(0xFF3B82F6)
                                )
                            )
                        )
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Text(
                if (name.isNotBlank()) name else username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            // User Stats Card - Get actual data from repository
            val likedDestinations by repo.likedDestinations.collectAsState()
            val allPlans = remember { repo.getAllPlans() }
            val allDestinations = remember { repo.getPopularDestinations() }
            val uniqueCountries = remember { allDestinations.map { it.country }.distinct().size }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Travel Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Divider(color = Color(0xFFE5E7EB))

                    // Stats grid with actual data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${likedDestinations.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                            Text(
                                "Saved Places",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${allPlans.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                "Travel Plans",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$uniqueCountries",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                "Countries",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            // User Details Card with Edit functionality
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Personal Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        IconButton(onClick = {
                            if (isEditing) {
                                // Save changes when exiting edit mode
                                val updatedUser =
                                    com.runanywhere.startup_hackathon20.data.model.User(
                                        username = username,
                                        name = name,
                                        email = email,
                                        countryCode = countryCode,
                                        phone = phone
                                    )
                                repo.updateUser(updatedUser)
                            }
                            isEditing = !isEditing
                        }) {
                            Text(
                                if (isEditing) "✓" else "✏️",
                                fontSize = 20.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFE5E7EB))

                    if (isEditing) {
                        // Editable fields
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Text("👤", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            leadingIcon = { Text("🔑", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Text("📧", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it.filter { char -> char.isDigit() } },
                            label = { Text("Phone") },
                            leadingIcon = { Text("📱", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = countryCode,
                            onValueChange = { countryCode = it },
                            label = { Text("Country Code") },
                            leadingIcon = { Text("🌍", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        // Display mode with actual user data
                        ProfileDetailRow("👤", "Full Name", if (name.isNotBlank()) name else "Not set")
                        ProfileDetailRow("🔑", "Username", username)
                        ProfileDetailRow("📧", "Email", if (email.isNotBlank()) email else "Not set")
                        ProfileDetailRow(
                            "📱",
                            "Phone",
                            if (phone.isNotBlank()) "$countryCode $phone" else "Not set"
                        )
                        ProfileDetailRow("🌍", "Country Code", countryCode)
                    }
                }
            }

            // Password Change Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔒 Change Password",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        IconButton(onClick = {
                            isChangingPassword = !isChangingPassword
                            if (!isChangingPassword) {
                                oldPassword = ""
                                newPassword = ""
                                confirmPassword = ""
                                passwordError = ""
                                passwordSuccess = ""
                            }
                        }) {
                            Text(
                                if (isChangingPassword) "✕" else "🔑",
                                fontSize = 20.sp
                            )
                        }
                    }

                    Divider(color = Color(0xFFE5E7EB))

                    if (isChangingPassword) {
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Current Password") },
                            leadingIcon = { Text("🔒", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            leadingIcon = { Text("🔑", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm New Password") },
                            leadingIcon = { Text("✅", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )

                        if (passwordError.isNotEmpty()) {
                            Text(
                                passwordError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (passwordSuccess.isNotEmpty()) {
                            Text(
                                passwordSuccess,
                                color = Color(0xFF10B981),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                passwordError = ""
                                passwordSuccess = ""

                                when {
                                    oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                                        passwordError = "Please fill all password fields"
                                    }

                                    newPassword.length < 6 -> {
                                        passwordError = "New password must be at least 6 characters"
                                    }

                                    newPassword != confirmPassword -> {
                                        passwordError = "New passwords don't match"
                                    }

                                    oldPassword == newPassword -> {
                                        passwordError =
                                            "New password must be different from old password"
                                    }

                                    else -> {
                                        val success =
                                            repo.updatePassword(username, oldPassword, newPassword)
                                        if (success) {
                                            passwordSuccess = "✅ Password updated successfully!"
                                            oldPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""
                                        } else {
                                            passwordError = "Current password is incorrect"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Update Password")
                        }
                    } else {
                        Text(
                            "Click the key icon to change your password",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = {
                    if (isEditing) {
                        // Save changes
                        val updatedUser = com.runanywhere.startup_hackathon20.data.model.User(
                            username = username,
                            name = name,
                            email = email,
                            countryCode = countryCode,
                            phone = phone
                        )
                        repo.updateUser(updatedUser)
                        isEditing = false
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Text(
                    if (isEditing) "Save Changes" else "Edit Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF3B82F6)
                )
            ) {
                Text(
                    "Back to Home",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFEF4444)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEF4444))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚪", fontSize = 20.sp)
                    Text(
                        "Logout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileDetailRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            emoji,
            fontSize = 24.sp
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF6B7280),
                fontSize = 12.sp
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Search Suggestion Item
@Composable
fun SearchSuggestionItem(
    destination: com.runanywhere.startup_hackathon20.data.model.Destination,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.Place,
                contentDescription = null,
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    destination.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    destination.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

// Enhanced Destination Card with Images, Expandable Tabs, and Comparison
@Composable
fun EnhancedDestinationCard(
    destination: com.runanywhere.startup_hackathon20.data.model.Destination,
    isLiked: Boolean,
    isSelectedForComparison: Boolean,
    isExpanded: Boolean,
    selectedTab: Int,
    onToggleLike: () -> Unit,
    onToggleComparison: () -> Unit,
    onExpand: () -> Unit,
    onTabSelected: (Int) -> Unit,
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
        onClick = { onExpand() },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column {
            // Card Header with Image
            Box(modifier = Modifier.fillMaxWidth()) {
                // Image or gradient placeholder
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = destination.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0EA5E9),
                                        Color(0xFF3B82F6),
                                        Color(0xFF2563EB)
                                    )
                                )
                            )
                    ) {
                        Text(
                            destination.name.take(2).uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
                
                // Card details over image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        destination.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        destination.country,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                // Action buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Comparison checkbox
                    IconButton(
                        onClick = { onToggleComparison() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (isSelectedForComparison) Icons.Filled.Check else Icons.Filled.Add,
                            contentDescription = if (isSelectedForComparison) "Selected for comparison" else "Select for comparison",
                            tint = if (isSelectedForComparison) Color(0xFF0EA5E9) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Like button
                    IconButton(
                        onClick = { onToggleLike() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color(0xFFEF4444) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Expandable content with tabs
            if (isExpanded) {
                Column {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) }
                        ) {
                            Text("Overview", fontSize = 12.sp)
                        }
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) }
                        ) {
                            Text("Hotels", fontSize = 12.sp)
                        }
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { onTabSelected(2) }
                        ) {
                            Text("Food", fontSize = 12.sp)
                        }
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { onTabSelected(3) }
                        ) {
                            Text("Things To Do", fontSize = 12.sp)
                        }
                    }
                    
                    // Tab content
                    when (selectedTab) {
                        0 -> OverviewTabContent(destination)
                        1 -> HotelsTabContent(destination.hotels)
                        2 -> RestaurantsTabContent(destination.restaurants)
                        3 -> ThingsToDoTabContent(destination.id, destination.name, destination.lat, destination.lng)
                    }
                }
            } else {
                // Expand button and open destination button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { onExpand() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Expand Details")
                    }
                    Button(
                        onClick = { onOpenDestination(destination.id) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Full")
                    }
                }
            }
        }
    }
}

// Tab Content Composables
@Composable
fun OverviewTabContent(destination: com.runanywhere.startup_hackathon20.data.model.Destination) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "About ${destination.name}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            destination.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDBEAFE)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF0EA5E9))
                    Text("${destination.rating}", fontWeight = FontWeight.Bold)
                    Text("Rating", fontSize = 10.sp)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDCFCE7)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💰", fontSize = 20.sp)
                    Text(destination.currencyCode, fontWeight = FontWeight.Bold)
                    Text("Currency", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun HotelsTabContent(hotels: List<com.runanywhere.startup_hackathon20.data.model.Hotel>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Hotels (${hotels.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        hotels.forEach { hotel ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(hotel.imageEmoji, fontSize = 32.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(hotel.name, fontWeight = FontWeight.Bold)
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${hotel.rating}")
                        }
                        Text(hotel.pricePerNight, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantsTabContent(restaurants: List<com.runanywhere.startup_hackathon20.data.model.Restaurant>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Restaurants (${restaurants.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        restaurants.forEach { restaurant ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(restaurant.imageEmoji, fontSize = 32.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(restaurant.name, fontWeight = FontWeight.Bold)
                        Text(restaurant.cuisine, fontSize = 12.sp, color = Color(0xFF6B7280))
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${restaurant.rating}")
                        }
                        Text(restaurant.priceRange, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@Composable
fun ThingsToDoTabContent(destinationId: String, destinationName: String, lat: Double, lng: Double) {
    var attractions by remember { mutableStateOf<List<com.runanywhere.startup_hackathon20.data.model.Attraction>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(destinationId) {
        scope.launch {
            attractions = DestinationApiService.fetchAttractions(destinationName, lat, lng)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Things To Do (${attractions.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        attractions.forEach { attraction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
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
                        Text(attraction.name, fontWeight = FontWeight.Bold)
                        Row {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("${attraction.rating}")
                        }
                        Text("⏱️ ${attraction.duration}", fontSize = 12.sp)
                        Text("💵 ${attraction.estimatedCost}", fontSize = 12.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

// Comparison Dialog
@Composable
fun ComparisonDialog(
    destinationIds: List<String>,
    destinations: List<com.runanywhere.startup_hackathon20.data.model.Destination>,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Compare Destinations (${destinations.size})")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                destinations.forEach { dest ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(dest.name, fontWeight = FontWeight.Bold)
                            Text(dest.country)
                            Row {
                                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("${dest.rating}")
                            }
                            Text("Currency: ${dest.currencyCode}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(onClick = onClear) {
                Text("Clear All")
            }
        }
    )
}
