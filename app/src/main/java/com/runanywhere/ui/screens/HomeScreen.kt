package com.runanywhere.startup_hackathon20.ui.screens

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
import kotlinx.coroutines.flow.StateFlow
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    // Use StateFlow for reactive updates
    val likedDestinations by repo.likedDestinations.collectAsState()
    val likedDestinationsList = likedDestinations.toList()
    val likedPlans by repo.likedPlans.collectAsState()
    val plansVersion by repo.plansVersion.collectAsState()

    // Get current user for greeting
    val currentUser = remember { repo.getCurrentUser() }
    val userName = currentUser?.username ?: "Traveler"

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
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier
                            .weight(1f)
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

                    // Map controls overlay - only expand/collapse button
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Expand/Collapse button (can keep functionality but map never 'expands' visually in this design)
                        FloatingActionButton(
                            onClick = {
                                mapViewState =
                                    if (mapViewState == "compact") "expanded" else "compact"
                            },
                            containerColor = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (mapViewState == "compact") Icons.Filled.Star else Icons.Filled.Close,
                                contentDescription = if (mapViewState == "compact") "Expand Map" else "Collapse Map",
                                tint = Color(0xFF0EA5E9),
                                modifier = Modifier.size(20.dp)
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

            // Destination Cards with Blue Gradient
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                filtered.forEach { d ->
                    Card(
                        onClick = { onOpenDestination(d.id) },
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
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Image placeholder with blue gradient
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
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
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            d.name.take(2).uppercase(),
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        d.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Place,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF6B7280)
                                        )
                                        Text(
                                            d.country,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFDBEAFE),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                "",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                d.currencyCode,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = Color(0xFF1E40AF),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
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
                                        if (likedDestinations.contains(d.id)) {
                                            repo.unlikeDestination(d.id)
                                        } else {
                                            repo.likeDestination(d.id)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.95f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        if (likedDestinations.contains(d.id))
                                            Icons.Filled.Favorite
                                        else
                                            Icons.Filled.FavoriteBorder,
                                        contentDescription = "Add to Wishlist",
                                        tint = if (likedDestinations.contains(d.id))
                                            Color(0xFF3B82F6)
                                        else
                                            Color(0xFF9CA3AF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
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
    val user = remember { repo.getCurrentUser() }

    var isEditing by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.name ?: "User") }
    var username by remember { mutableStateOf(user?.username ?: "username") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var countryCode by remember { mutableStateOf(user?.countryCode ?: "+91") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }

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
                username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            // User Stats Card
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

                    // Stats grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "12",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0EA5E9)
                            )
                            Text(
                                "Countries",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "45",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                "Cities",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "8",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                "Trips",
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
                        IconButton(onClick = { isEditing = !isEditing }) {
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
                            label = { Text("Name") },
                            leadingIcon = { Text("👤", fontSize = 20.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
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
                    } else {
                        // Display mode
                        ProfileDetailRow("🔑", "Username", username)
                        ProfileDetailRow("📧", "Email", email)
                        ProfileDetailRow(
                            "📱",
                            "Phone",
                            if (phone.isNotEmpty()) "$countryCode $phone" else "Not set"
                        )
                        ProfileDetailRow("🎂", "Member Since", "January 2024")
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
