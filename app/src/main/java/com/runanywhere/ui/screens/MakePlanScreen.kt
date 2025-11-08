package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.startup_hackathon20.ChatViewModel
import com.runanywhere.data.model.PlanForm
import com.runanywhere.data.DI
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePlanScreen(
    destinationId: String? = null,
    planIdToEdit: String? = null,
    onPlanCreated: (String) -> Unit,
    onNavigateToChat: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    vm: ChatViewModel = viewModel()
) {
    val repo = remember { DI.repo }
    val destination = remember(destinationId) {
        destinationId?.let { repo.getDestinationById(it) }
    }

    // Auto-start model loading when screen opens
    LaunchedEffect(Unit) {
        vm.startModelLoading()
    }

    // Check if we're in edit mode
    val isEditMode = planIdToEdit != null

    var from by remember { mutableStateOf("") }
    var to by remember(destination) { mutableStateOf(destination?.name ?: "Paris") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("50000") }
    var people by remember { mutableStateOf("2") }
    var foodCategory by remember { mutableStateOf("Veg") }
    var transportMode by remember { mutableStateOf("") }

    var expandedFood by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Date formatter
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Calculate days and nights dynamically
    val (days, nights) = remember(startDate, endDate) {
        if (startDate.isNotBlank() && endDate.isNotBlank()) {
            try {
                val start = dateFormatter.parse(startDate)
                val end = dateFormatter.parse(endDate)
                if (start != null && end != null && end.after(start)) {
                    val diffInMillis = end.time - start.time
                    val calculatedDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
                    val calculatedNights = calculatedDays - 1
                    Pair(calculatedDays, calculatedNights)
                } else {
                    Pair(0, 0)
                }
            } catch (e: Exception) {
                Pair(0, 0)
            }
        } else {
            Pair(0, 0)
        }
    }

    val isLoading by vm.isLoading.collectAsState()
    val isModelLoading by vm.isModelLoading.collectAsState()
    val modelLoaded by vm.currentModelId.collectAsState()
    val statusMessage by vm.statusMessage.collectAsState()
    val downloadProgress by vm.downloadProgress.collectAsState()
    val scrollState = rememberScrollState()

    val foodOptions = listOf("Veg", "Non-Veg", "Both")
    val transportOptions = listOf("Flight", "Train", "Bus", "Car", "Bike")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header Section with Back Button
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
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Back button - only show if onBack is provided
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (isEditMode) "Create New Plan" else "Plan Your Trip",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            if (isEditMode) "Enter correct details for your trip" else "Let AI create a perfect itinerary for you",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show edit mode banner if editing
            if (isEditMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("✏️", fontSize = 24.sp)
                        Column {
                            Text(
                                "Make Corrections",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                "Fill in the correct details and generate a new plan",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Journey Section
            Text(
                "Journey Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            CustomTextField(
                value = from,
                onValueChange = { from = it },
                label = "From",
                placeholder = "Delhi",
                icon = Icons.Filled.Place
            )

            // "To" field - auto-filled based on destination selected
            OutlinedTextField(
                value = to,
                onValueChange = { to = it },
                label = { Text("To (Destination)") },
                placeholder = { Text("Paris") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true,
                enabled = false,
                trailingIcon = {
                    Text(
                        "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )

            // Map Preview of destination
            if (destination != null && destination.lat != null && destination.lng != null) {
                val destLatLng = LatLng(destination.lat, destination.lng)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(destLatLng, 11.5f)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    GoogleMap(
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false
                        ),
                        properties = MapProperties(
                            isMyLocationEnabled = false,
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = destLatLng),
                            title = destination.name
                        )
                    }
                }
            }

            // Trip Details Section
            Text(
                "Trip Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Date Picker with Calendar
            OutlinedTextField(
                value = startDate,
                onValueChange = {},
                label = { Text("Start Date") },
                placeholder = { Text("Select date") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                singleLine = true,
                readOnly = true,
                enabled = false
            )

            // Start Date Picker Dialog
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis()
                )

                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    startDate = dateFormatter.format(Date(millis))
                                }
                                showStartDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = endDate,
                onValueChange = {},
                label = { Text("End Date") },
                placeholder = { Text("Select date") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                singleLine = true,
                readOnly = true,
                enabled = false
            )

            // End Date Picker Dialog
            if (showEndDatePicker) {
                val initialDateMillis = if (startDate.isNotBlank()) {
                    try {
                        dateFormatter.parse(startDate)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                } else {
                    System.currentTimeMillis()
                }

                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialDateMillis
                )

                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    // Validate that selected end date is after start date
                                    if (startDate.isNotBlank()) {
                                        try {
                                            val startMillis = dateFormatter.parse(startDate)?.time
                                            if (startMillis != null && millis >= startMillis) {
                                                endDate = dateFormatter.format(Date(millis))
                                            }
                                        } catch (e: Exception) {
                                            endDate = dateFormatter.format(Date(millis))
                                        }
                                    } else {
                                        endDate = dateFormatter.format(Date(millis))
                                    }
                                }
                                showEndDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (days > 0) days.toString() else "",
                        onValueChange = { },
                        label = { Text("Days") },
                        placeholder = { Text("Auto") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (days > 0) {
                                Text(
                                    "📅",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        singleLine = true,
                        readOnly = true,
                        enabled = false
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (nights > 0) nights.toString() else "",
                        onValueChange = { },
                        label = { Text("Nights") },
                        placeholder = { Text("Auto") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (nights > 0) {
                                Text(
                                    "🌙",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        singleLine = true,
                        readOnly = true,
                        enabled = false
                    )
                }
            }

            // Add info card about auto-calculation
            if (days > 0 && nights > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDCFCE7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✅", fontSize = 20.sp)
                        Text(
                            "Trip duration: $days days, $nights nights",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (startDate.isNotBlank() && endDate.isNotBlank() && days == 0) {
                // Show error if dates are invalid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEE2E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️", fontSize = 20.sp)
                        Text(
                            "End date must be after start date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = people,
                        onValueChange = { people = it },
                        label = "People",
                        placeholder = "2",
                        icon = Icons.Filled.Person
                    )
                }
            }

            CustomTextField(
                value = budget,
                onValueChange = { budget = it },
                label = "Budget (₹)",
                placeholder = "50000",
                icon = Icons.Filled.Info
            )

            // Food Category Dropdown
            Text(
                "Preferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedFood,
                onExpandedChange = { expandedFood = !expandedFood }
            ) {
                OutlinedTextField(
                    value = foodCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Food Category") },
                    leadingIcon = {
                        Text(
                            "🍽️",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedFood,
                    onDismissRequest = { expandedFood = false }
                ) {
                    foodOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                foodCategory = option
                                expandedFood = false
                            }
                        )
                    }
                }
            }

            // Mode of Transport Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedTransport,
                onExpandedChange = { expandedTransport = !expandedTransport }
            ) {
                OutlinedTextField(
                    value = transportMode.ifEmpty { "Select Transport" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mode of Transport") },
                    leadingIcon = {
                        Text(
                            "🚗",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedTransport,
                    onDismissRequest = { expandedTransport = false }
                ) {
                    transportOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(option)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        when (option) {
                                            "Flight" -> ""
                                            "Train" -> ""
                                            "Bus" -> ""
                                            "Car" -> ""
                                            "Bike" -> ""
                                            else -> ""
                                        }
                                    )
                                }
                            },
                            onClick = {
                                transportMode = option
                                expandedTransport = false
                            }
                        )
                    }
                }
            }

            // Status Message - Improved UI
            if (modelLoaded == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFDBEAFE) // Light blue instead of error red
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.size(48.dp)
                            ) {
                                drawCircle(
                                    color = androidx.compose.ui.graphics.Color(0xFF3B82F6),
                                    radius = size.minDimension / 2
                                )
                            }
                            Column {
                                Text(
                                    "AI Model Required",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color(0xFF1E40AF)
                                )
                                Text(
                                    "Load the model to continue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = androidx.compose.ui.graphics.Color(0xFF1E40AF)
                                        .copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Show status message if present
                        if (!statusMessage.isNullOrBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = androidx.compose.ui.graphics.Color(0xFF1E3A8A)
                            )
                        }

                        val currentProgress = downloadProgress
                        if (currentProgress != null && currentProgress in 0f..1f && isModelLoading) {
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = currentProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp),
                                color = androidx.compose.ui.graphics.Color(0xFF3B82F6),
                                trackColor = androidx.compose.ui.graphics.Color(0xFFDBEAFE)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${(currentProgress * 100).toInt()}% downloaded",
                                color = androidx.compose.ui.graphics.Color(0xFF1E40AF),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        androidx.compose.material3.HorizontalDivider(
                            color = androidx.compose.ui.graphics.Color(0xFF93C5FD),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            "💡 Your form data will be preserved during loading",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF1E40AF),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Load Model Button - Improved design
                        Button(
                            onClick = {
                                android.util.Log.d("MakePlanScreen", "Load Model button clicked!")
                                vm.manualLoadModel()
                            },
                            enabled = !isModelLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFF3B82F6)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                "🚀",
                                fontSize = 20.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Load AI Model Now",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Show success message when model is loaded
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDCFCE7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("✅", fontSize = 32.sp)
                        Column {
                            Text(
                                "AI Model Ready!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                "You can now generate your travel plan",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF166534).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Loading indicator and helpful tip
            if (isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFEF3C7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                            strokeWidth = 4.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "⚡ AI is working on your plan...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color(0xFF92400E)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "This may take 30-60 seconds on-device",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color(0xFF92400E)
                                    .copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Replace the Generate Button section in MakePlanScreen with this:

// Remove the "Model Required" card section since we're using APIs now

// Update the Generate Button at the bottom:

            Button(
                onClick = {
                    val form = PlanForm(
                        from = from,
                        to = to,
                        startDate = startDate,
                        nights = nights,
                        budget = budget.toIntOrNull() ?: 50000,
                        people = people.toIntOrNull() ?: 2
                    )

                    // Use API generation (no model loading required!)
                    vm.generatePlanWithAPIs(form) { planId ->
                        // Navigate to chat to show the generated plan
                        onNavigateToChat?.invoke() ?: onPlanCreated(planId)
                    }
                },
                enabled = !isLoading && from.isNotBlank() && to.isNotBlank() &&
                        transportMode.isNotBlank() && startDate.isNotBlank() &&
                        endDate.isNotBlank() && days > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoading) Color(0xFF94A3B8) else Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFFE2E8F0)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "AI is working...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        "✨",
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Generate Plan with AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

// Add helpful info card above the button
            if (!isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDBEAFE)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("💡", fontSize = 28.sp)
                        Column {
                            Text(
                                "Powered by OpenAI & Unsplash",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                "Your plan will be ready in 20-30 seconds",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1E40AF).copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

// Loading indicator with status
            if (isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF3C7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color(0xFFF59E0B),
                                strokeWidth = 4.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "⚡ AI is creating your perfect itinerary...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    statusMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF92400E).copy(alpha = 0.8f)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFF59E0B),
                            trackColor = Color(0xFFFEF3C7)
                        )

                        Text(
                            "💡 Tip: Your plan will include day-by-day activities, hotel recommendations, and budget breakdown",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E).copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        singleLine = true
    )
}
