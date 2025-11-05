package com.runanywhere.startup_hackathon20

import android.os.Bundle
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.startup_hackathon20.ui.AppRoot
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Startup_hackathon20Theme {
                AppRoot()
            }
        }
    }
}

// Travel-themed quick action suggestions
data class QuickAction(val emoji: String, val text: String, val prompt: String)

val travelQuickActions = listOf(
    QuickAction("✈️", "Plan Trip", "Help me plan a trip to "),
    QuickAction("🏨", "Find Hotels", "Suggest hotels in "),
    QuickAction("🍽️", "Local Food", "What are the must-try foods in "),
    QuickAction("📸", "Top Attractions", "What are the top attractions in "),
    QuickAction("💰", "Budget Tips", "Give me budget travel tips for "),
    QuickAction("🗺️", "Itinerary", "Create a 3-day itinerary for "),
    QuickAction("🎒", "Packing Guide", "What should I pack for "),
    QuickAction("🚕", "Local Transport", "How to get around in "),
    QuickAction("🌤️", "Best Time", "What's the best time to visit "),
    QuickAction("💱", "Currency Info", "Tell me about currency and costs in ")
)

// Map grid background for travel theme with blue gradient
@Composable
fun MapGridBackground(modifier: Modifier = Modifier, offset: Float) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C4A6E), // Deep ocean blue
                        Color(0xFF075985), // Dark sky blue
                        Color(0xFF0C4A6E)  // Deep ocean blue
                    )
                )
            )
    ) {
        // Grid overlay effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 50.dp.toPx()
            val alpha = 0.08f

            // Draw vertical lines
            var x = offset % gridSize
            while (x < size.width) {
                drawLine(
                    color = Color(0xFF0EA5E9).copy(alpha = alpha), // Sky blue
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += gridSize
            }

            // Draw horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = Color(0xFF0EA5E9).copy(alpha = alpha), // Sky blue
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += gridSize
            }
        }

        // Gradient overlay for depth
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0EA5E9).copy(alpha = 0.05f),
                            Color(0xFF3B82F6).copy(alpha = 0.1f),
                            Color(0xFF2563EB).copy(alpha = 0.05f)
                        )
                    )
                )
        )
    }
}

@Composable
fun TravelChatHeader(
    modelStatus: Boolean,
    onSettingsClick: () -> Unit,
    statusMessage: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Minimalist Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            "AI Travel Assistant",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            fontSize = 18.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (modelStatus) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        CircleShape
                                    )
                            )
                            Text(
                                if (modelStatus) "Online" else "Setup Required",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Clean Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF3F4F6), CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Subtle separator
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE5E7EB),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun TechnicalStatusBar(
    statusMessage: String,
    downloadProgress: Float?,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF075985).copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (downloadProgress != null) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF0EA5E9)
                )
            } else if (isLoading) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF0EA5E9), CircleShape)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                }
            } else {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )

                downloadProgress?.let { progress ->
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF0EA5E9),
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun TravelModelSelector(
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
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 24.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "✨ AI Models",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9)
                    )
                    Text(
                        "Select your travel assistant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280),
                        fontSize = 13.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (models.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6),
                            strokeWidth = 3.dp
                        )
                        Text(
                            "Scanning for AI models...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                models.forEach { model ->
                    TechnicalModelCard(
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

@Composable
fun TravelChatEmptyState(
    isModelLoaded: Boolean,
    onLoadModel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Professional Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Color(0xFF0EA5E9).copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(40.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "AI-Powered Travel Planning",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            Text(
                "Get instant answers about destinations, accommodations,\nactivities, and personalized travel recommendations",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontSize = 14.sp
            )
        }

        if (!isModelLoaded) {
            Button(
                onClick = onLoadModel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0EA5E9)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text(
                    "🔄",
                    fontSize = 18.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Load AI Model",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TravelQuickActions(
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        Text(
            "Quick Actions",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(actions) { action ->
                QuickActionChip(
                    action = action,
                    onClick = { onActionClick(action) }
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    action: QuickAction,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0F9FF),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF0EA5E9).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                action.emoji,
                fontSize = 16.sp
            )
            Text(
                action.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🗺️", fontSize = 16.sp)
                Text(
                    "AI Guide is typing",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                // Animated dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { index ->
                        val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = index * 200),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha$index"
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .alpha(alpha)
                                .background(Color(0xFF3B82F6), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TravelInputField(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF075985).copy(alpha = 0.95f),
        shadowElevation = 16.dp
    ) {
        Column {
            // Top border line
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask about destinations, hotels, tips...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    enabled = isEnabled,
                    maxLines = 4,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0EA5E9),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        disabledBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                        disabledContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF0EA5E9)
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )

                // Send Button
                FloatingActionButton(
                    onClick = onSend,
                    containerColor = if (isEnabled && inputText.isNotBlank())
                        Color(0xFF3B82F6)
                    else
                        Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = if (isEnabled && inputText.isNotBlank())
                                Color.White
                            else
                                Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_VALUE", "ASSIGNED_VALUE_IS_NEVER_READ", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val currentModelId by viewModel.currentModelId.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showModelSelector by remember { mutableStateOf(false) }
    var showQuickActions by remember { mutableStateOf(true) }
    
    // Start model loading when chat screen opens (only once)
    LaunchedEffect(Unit) {
        viewModel.startModelLoading()
    }

    // Animated grid pattern for travel map theme
    val infiniteTransition = rememberInfiniteTransition(label = "mapGrid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        Modifier
            .fillMaxSize()
    ) {
        // Animated background grid (map-like pattern)
        MapGridBackground(modifier = Modifier.fillMaxSize(), offset = gridOffset)

        Column(
            Modifier
                .fillMaxSize()
        ) {
            // Compact Travel-themed Header
            TravelChatHeader(
                modelStatus = currentModelId != null,
                onSettingsClick = { showModelSelector = !showModelSelector },
                statusMessage = statusMessage
            )

            // Technical Status Bar
            if (statusMessage.isNotEmpty()) {
                TechnicalStatusBar(
                    statusMessage = statusMessage,
                    downloadProgress = downloadProgress,
                    isLoading = isLoading
                )
            }

            // Model Selector Overlay
            AnimatedVisibility(
                visible = showModelSelector,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                TravelModelSelector(
                    models = availableModels,
                    currentModelId = currentModelId,
                    onClose = { showModelSelector = false },
                    onDownload = { viewModel.downloadModel(it) },
                    onLoad = { viewModel.loadModel(it) },
                    onRefresh = { viewModel.refreshModels() }
                )
            }

            // Messages Area with Travel Context
            Box(modifier = Modifier.weight(1f)) {
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (messages.isEmpty()) {
                        item {
                            TravelChatEmptyState(
                                isModelLoaded = currentModelId != null,
                                onLoadModel = { viewModel.manualLoadModel() }
                            )
                        }
                    }

                    items(messages) { message ->
                        TravelMessageBubble(message)
                    }

                    // Typing indicator
                    if (isLoading && messages.isNotEmpty()) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                // Auto-scroll
                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }

            // Quick Actions (Travel Suggestions)
            AnimatedVisibility(
                visible = showQuickActions && messages.isEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                TravelQuickActions(
                    actions = travelQuickActions,
                    onActionClick = { action ->
                        inputText = action.prompt
                    }
                )
            }

            // Premium Input Area
            TravelInputField(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                        showQuickActions = false
                    }
                },
                isEnabled = !isLoading && currentModelId != null,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun TravelMessageBubble(message: ChatMessage) {
    if (message.isPlan && message.planData != null) {
        // Display plan card with unique UI
        TravelPlanCard(planData = message.planData)
    } else {
        // Regular message bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                modifier = Modifier.widthIn(max = 300.dp),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser)
                    Color(0xFF3B82F6) // Blue
                else
                    Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (message.isUser) "👤" else "🗺️",
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (message.isUser) "You" else "AI Guide",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (message.isUser)
                                Color.White.copy(alpha = 0.9f)
                            else
                                Color(0xFF0EA5E9), // Sky blue
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.isUser) Color.White else Color(0xFF1F2937),
                        lineHeight = 20.sp,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TravelPlanCard(planData: com.runanywhere.startup_hackathon20.PlanDisplayData) {
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
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0EA5E9),
                            Color(0xFF3B82F6),
                            Color.White
                        ),
                        startY = 0f,
                        endY = 400f
                    )
                )
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "✈️",
                        fontSize = 32.sp
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
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCFCE7)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "📅",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                planData.startDate,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp
                            )
                            Text(
                                "Start Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF065F46).copy(alpha = 0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Nights Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDBEAFE)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "🌙",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${planData.nights}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                "Nights",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1E40AF).copy(alpha = 0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // People Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "👥",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${planData.people}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                "Travelers",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF92400E).copy(alpha = 0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Budget Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0E7FF)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "💰",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "₹${planData.budget}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3730A3),
                                fontSize = 11.sp
                            )
                            Text(
                                "Budget",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF3730A3).copy(alpha = 0.7f),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Divider(color = Color(0xFFE5E7EB))

                Spacer(Modifier.height(16.dp))

                // AI Generated Itinerary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "🗺️",
                        fontSize = 20.sp
                    )
                    Text(
                        "AI-Generated Itinerary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Scrollable itinerary content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .background(
                            Color(0xFFF9FAFB),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        planData.itinerary.ifEmpty { "Generating your personalized itinerary..." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF374151),
                        lineHeight = 22.sp,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicalModelCard(
    model: com.runanywhere.sdk.models.ModelInfo,
    isActive: Boolean,
    onDownload: () -> Unit,
    onLoad: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isActive)
            Color(0xFFDBEAFE) // Light blue
        else
            Color(0xFFF9FAFB),
        border = if (isActive)
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6))
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        model.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color(0xFF0EA5E9) else Color(0xFF1F2937),
                        fontSize = 15.sp
                    )
                    if (isActive) {
                        Surface(
                            color = Color(0xFF10B981),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "ACTIVE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                if (model.isDownloaded) {
                    Text(
                        "✓ Model ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF10B981),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            if (isActive) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(24.dp)
                )
            } else if (!model.isDownloaded) {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Download",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6) // Blue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Load",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Startup_hackathon20Theme {
        ChatScreen()
    }
}