package com.runanywhere.startup_hackathon20

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.lifecycleScope
import com.runanywhere.startup_hackathon20.ui.AppRoot
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// 🔴 CRITICAL IMPORTS - Add these
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.llm.llamacpp.LlamaCppModule
// Add to your MainActivity.kt file - Replace existing TravelPlanCard

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun TravelPlanCard(planData: PlanDisplayData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Destination Image Header
            if (planData.imageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    // Load image using Coil
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(planData.imageUrl)
                            .crossfade(true)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .build(),
                        contentDescription = "Destination: ${planData.to}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for better text visibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    ),
                                    startY = 0f,
                                    endY = 800f
                                )
                            )
                    )

                    // Title overlaid on image
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Your Travel Plan",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Trip Route
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                planData.from,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Text(
                                "→",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                planData.to,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            } else {
                // Fallback gradient header if no image
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0EA5E9),
                                    Color(0xFF3B82F6)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Your Travel Plan",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Trip Route
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "FROM",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                            Text(
                                planData.from,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            "→",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "TO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                            Text(
                                planData.to,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Details Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date Card
                    DetailCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFDCFCE7),
                        iconColor = Color(0xFF065F46),
                        value = planData.startDate,
                        label = "Start Date",
                        icon = Icons.Filled.CalendarToday
                    )

                    // Nights Card
                    DetailCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFDBEAFE),
                        iconColor = Color(0xFF1E40AF),
                        value = "${planData.nights}",
                        label = "Nights",
                        icon = Icons.Filled.NightsStay
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // People Card
                    DetailCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFFEF3C7),
                        iconColor = Color(0xFF92400E),
                        value = "${planData.people}",
                        label = "Travelers",
                        icon = Icons.Filled.Group
                    )

                    // Budget Card
                    DetailCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFE0E7FF),
                        iconColor = Color(0xFF3730A3),
                        value = "₹${planData.budget}",
                        label = "Budget",
                        icon = Icons.Filled.AccountBalanceWallet
                    )
                }

                Spacer(Modifier.height(16.dp))

                Divider(color = Color(0xFFE5E7EB))

                Spacer(Modifier.height(16.dp))

                // Summary text (itinerary as summary)
                if (planData.itinerary.isNotEmpty()) {
                    Text(
                        planData.itinerary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF374151),
                        lineHeight = 20.sp,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(20.dp))
                }

                // Attraction Cards Section
                if (planData.attractions.isNotEmpty()) {
                    Text(
                        "Top Attractions & Activities",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        fontSize = 20.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Group attractions by day/category
                    val groupedAttractions = planData.attractions.groupBy { it.category }

                    groupedAttractions.forEach { (day, attractions) ->
                        if (day.isNotEmpty()) {
                            Text(
                                day,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        attractions.forEach { attraction ->
                            AttractionCard(attraction)
                            Spacer(Modifier.height(12.dp))
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    iconColor: Color,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontSize = 13.sp
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = iconColor.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔴 CRITICAL: Register LlamaCpp module FIRST
        Log.i("MainActivity", "Registering LlamaCpp module...")
        try {
            LlamaCppModule.register()
            Log.i("MainActivity", "✓ LlamaCpp module registered successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", " Failed to register LlamaCpp module: ${e.message}", e)
        }

        // 🔴 CRITICAL: Initialize RunAnywhere SDK
        Log.i("MainActivity", "Initializing RunAnywhere SDK...")
        lifecycleScope.launch {
            try {
                RunAnywhere.initialize(
                    apiKey = "dev",  // Use your actual API key if you have one
                    baseURL = "https://api.runanywhere.ai"
                )
                Log.i("MainActivity", "✓ RunAnywhere SDK initialized successfully")
            } catch (e: Exception) {
                Log.e("MainActivity", " Failed to initialize SDK: ${e.message}", e)
            }
        }

        enableEdgeToEdge()
        setContent {
            Startup_hackathon20Theme {
                AppRoot()
            }
        }
    }
}

// Quick reference suggestions for travel
data class QuickReference(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val text: String,
    val prompt: String
)

val quickReferences = listOf(
    QuickReference(Icons.Filled.Flight, "Plan a Trip", "I want to plan a trip"),
    QuickReference(Icons.Filled.Hotel, "Find Hotels", "Suggest hotels in Paris"),
    QuickReference(Icons.Filled.Restaurant, "Local Cuisine", "What are local foods in Tokyo?"),
    QuickReference(Icons.Filled.Place, "Top Spots", "Show me top attractions"),
    QuickReference(Icons.Filled.AttachMoney, "Budget Tips", "How to travel on a budget?"),
    QuickReference(Icons.Filled.Schedule, "Itinerary Help", "Create a 5-day itinerary"),
    QuickReference(Icons.Filled.Map, "Make a Plan", "NAVIGATE_MAKE_PLAN"),
    QuickReference(Icons.Filled.Home, "View Destinations", "NAVIGATE_HOME")
)

// Chat action buttons data
data class ChatAction(val icon: androidx.compose.ui.graphics.vector.ImageVector, val text: String, val action: String)


// Simplified top bar with settings only
@Composable
fun SimplifiedTopBar(
    modelLoaded: Boolean,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2563EB),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty spacer to balance the layout
            Spacer(modifier = Modifier.size(48.dp))

            // Title
            Text(
                "AI Travel Assistant",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp
            )

            // Settings button
            Surface(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF1E40AF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Model selector dialog
@Composable
fun ModernModelSelector(
    models: List<com.runanywhere.sdk.models.ModelInfo>,
    currentModelId: String?,
    onClose: () -> Unit,
    onDownload: (String) -> Unit,
    onLoad: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 32.dp
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "AI Models",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Choose your intelligent assistant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        onClick = onRefresh,
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = onClose,
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (models.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2563EB),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Discovering AI models...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                models.forEach { model ->
                    ModernModelCard(
                        model = model,
                        isActive = model.id == currentModelId,
                        onDownload = { onDownload(model.id) },
                        onLoad = { onLoad(model.id) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}


// Quick references section
@Composable
fun QuickReferencesSection(
    references: List<QuickReference>,
    onReferenceClick: (QuickReference) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            "Quick References",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(references) { reference ->
                Surface(
                    onClick = { onReferenceClick(reference) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFE2E8F0)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            reference.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF2563EB)
                )
                Text(
                    "Generating response...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Custom Mic Icon Composable
@Composable
fun MicIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw microphone body (rounded rectangle)
        drawRoundRect(
            color = tint,
            topLeft = Offset(width * 0.35f, height * 0.2f),
            size = androidx.compose.ui.geometry.Size(width * 0.3f, height * 0.35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(width * 0.15f, width * 0.15f)
        )

        // Draw microphone stand (vertical line)
        drawLine(
            color = tint,
            start = Offset(width * 0.5f, height * 0.55f),
            end = Offset(width * 0.5f, height * 0.75f),
            strokeWidth = width * 0.08f
        )

        // Draw microphone base (horizontal line)
        drawLine(
            color = tint,
            start = Offset(width * 0.3f, height * 0.75f),
            end = Offset(width * 0.7f, height * 0.75f),
            strokeWidth = width * 0.08f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

// Modern input field with voice button - fixed text visibility
@Composable
fun ModernInputWithVoice(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClick: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Type your message here",
                        color = Color(0xFF94A3B8),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                enabled = isEnabled,
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    disabledBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    disabledTextColor = Color(0xFF94A3B8),
                    cursorColor = Color(0xFF2563EB)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E293B)
                )
            )

            // Mic/Voice button
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier.size(56.dp),
                enabled = isEnabled
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = if (isEnabled) Color(0xFF64748B).copy(alpha = 0.1f) else Color(
                        0xFFE2E8F0
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        MicIcon(
                            modifier = Modifier.size(24.dp),
                            tint = if (isEnabled) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            val isActive = isEnabled && inputText.isNotBlank()
            FloatingActionButton(
                onClick = onSend,
                containerColor = if (isActive) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                modifier = Modifier.size(56.dp),
                shape = CircleShape
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = if (isActive) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Rest of the composables remain the same...
// (ChatScreen, ChatMessageBubble, etc.)

@Composable
fun AttractionCard(attraction: AttractionCardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            // Image
            if (attraction.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(attraction.imageUrl)
                        .crossfade(true)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .build(),
                    contentDescription = attraction.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder with gradient
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            )
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
            }

            Spacer(Modifier.width(10.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    attraction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(3.dp))

                // Rating and Duration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            attraction.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            attraction.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        attraction.cost,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(5.dp))

                Text(
                    attraction.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD1D5DB),
                    maxLines = 3,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    onNavigateToMakePlan: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val currentModelId by viewModel.currentModelId.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showModelSelector by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Auto-start model loading when chat screen opens
    LaunchedEffect(Unit) {
        viewModel.startModelLoading()
    }

    // Show snackbar when model loads or unloads
    LaunchedEffect(currentModelId) {
        if (currentModelId != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "AI model loaded successfully!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Box(
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
        Column(Modifier.fillMaxSize()) {
            // Header integrated directly - no separate Surface
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty spacer to balance the layout
                Spacer(modifier = Modifier.size(48.dp))

                // Title
                Text(
                    "AI Travel Assistant",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )

                // Settings button
                Surface(
                    onClick = {
                        if (currentModelId == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Loading AI model...",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            viewModel.manualLoadModel()
                        }
                        showModelSelector = !showModelSelector
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Model status banner when loading or no model
            if (statusMessage.isNotEmpty() || currentModelId == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentModelId == null) Color(0xFFFEF3C7) else Color(0xFFDBEAFE)
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
                                if (currentModelId == null) "AI Model Required" else "Loading...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (currentModelId == null) Color(0xFF92400E) else Color(
                                    0xFF1E40AF
                                )
                            )
                            Text(
                                statusMessage.ifEmpty { "Loading AI (this may take 10-30 seconds)..." },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (currentModelId == null) Color(0xFF92400E) else Color(
                                    0xFF1E40AF
                                )
                            )
                        }

                        if (currentModelId == null) {
                            Button(
                                onClick = { viewModel.manualLoadModel() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF59E0B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Load AI Model",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Model selector dialog
            if (showModelSelector) {
                ModernModelSelector(
                    models = availableModels,
                    currentModelId = currentModelId,
                    onClose = { showModelSelector = false },
                    onDownload = { viewModel.downloadModel(it) },
                    onLoad = {
                        viewModel.loadModel(it)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Loading model...",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onRefresh = { viewModel.refreshModels() }
                )
            }

            // Messages area
            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty()) {
                    WelcomeMessage()
                } else {
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(messages.size) { index ->
                            val message = messages[index]
                            if (message.isPlan && message.planData != null) {
                                TravelPlanCard(planData = message.planData)
                            } else {
                                ChatMessageBubble(message = message)
                            }
                        }

                        if (isLoading && messages.isNotEmpty()) {
                            item {
                                ModernTypingIndicator()
                            }
                        }
                    }

                    LaunchedEffect(messages.size) {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
            }

            QuickReferencesSection(
                references = quickReferences,
                onReferenceClick = { reference ->
                    when (reference.prompt) {
                        "NAVIGATE_MAKE_PLAN" -> onNavigateToMakePlan()
                        "NAVIGATE_HOME" -> onNavigateToHome()
                        else -> inputText = reference.prompt
                    }
                }
            )

            ModernInputWithVoice(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        if (currentModelId == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please wait for AI model to load",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        } else {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    }
                },
                onVoiceClick = {},
                isEnabled = !isLoading && currentModelId != null,
                isLoading = isLoading
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

// Chat message bubble - white box, no actions
@Composable
fun ChatMessageBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // AI avatar
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFF10B981).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isUser) 20.dp else 6.dp,
                bottomEnd = if (message.isUser) 6.dp else 20.dp
            ),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (message.isUser) "You" else "AI Assistant",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isUser) Color(0xFF2563EB) else Color(0xFF10B981),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1E293B),
                    lineHeight = 22.sp,
                    fontSize = 15.sp
                )
            }
        }

        if (message.isUser) {
            Spacer(Modifier.width(8.dp))
            // User avatar
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = Color(0xFF2563EB).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ModernModelCard(
    model: com.runanywhere.sdk.models.ModelInfo,
    isActive: Boolean,
    onDownload: () -> Unit,
    onLoad: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) Color(0xFFDCFCE7) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isActive) Color(0xFF10B981) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        model.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 16.sp
                    )
                    if (isActive) {
                        Surface(
                            color = Color(0xFF10B981),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "ACTIVE",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (model.isDownloaded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("✓", fontSize = 12.sp, color = Color(0xFF10B981))
                        Text(
                            "Ready to use",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.width(14.dp))

            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF10B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else if (!model.isDownloaded) {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Download",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Load",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Welcome to AI Help Assistant",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
            Text(
                "How can I help you?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}