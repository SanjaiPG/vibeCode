package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.startup_hackathon20.ChatViewModel
import com.runanywhere.startup_hackathon20.data.model.PlanForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePlanScreen(
    destinationId: String? = null,
    onPlanCreated: (String) -> Unit,
    vm: ChatViewModel = viewModel()
) {
    val repo = remember { com.runanywhere.startup_hackathon20.data.DI.repo }
    val destination = remember(destinationId) {
        destinationId?.let { repo.getDestination(it) }
    }

    var from by remember { mutableStateOf("") }
    var to by remember(destination) { mutableStateOf(destination?.name ?: "Paris") }
    var startDate by remember { mutableStateOf("") }
    var nights by remember { mutableStateOf("3") }
    var budget by remember { mutableStateOf("50000") }
    var people by remember { mutableStateOf("2") }
    var foodCategory by remember { mutableStateOf("Veg") }
    var transportMode by remember { mutableStateOf("") }

    var expandedFood by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }

    val isLoading by vm.isLoading.collectAsState()
    val modelLoaded by vm.currentModelId.collectAsState()
    val scrollState = rememberScrollState()

    val foodOptions = listOf("Veg", "Non-Veg", "Both")
    val transportOptions = listOf("Flight", "Train", "Bus", "Car", "Bike")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    "Plan Your Trip",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Let AI create a perfect itinerary for you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            // Trip Details Section
            Text(
                "Trip Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            CustomTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = "Start Date",
                placeholder = "2024-12-25",
                icon = Icons.Filled.DateRange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = nights,
                        onValueChange = { nights = it },
                        label = "Nights",
                        placeholder = "3",
                        icon = Icons.Filled.Home
                    )
                }

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

                        androidx.compose.material3.Divider(
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
                            onClick = { vm.manualLoadModel() },
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
            }

            Spacer(Modifier.height(8.dp))

            // Generate Button
            Button(
                onClick = {
                    val form = PlanForm(from, to, startDate, nights.toIntOrNull() ?: 3, budget.toIntOrNull() ?: 50000, people.toIntOrNull() ?: 2)
                    // Use generatePlanDirect to skip chatbot and go straight to result screen
                    vm.generatePlanDirect(form) { id ->
                        onPlanCreated(id)
                        // Don't navigate to chat - the onPlanCreated will navigate to plan result
                    }
                },
                enabled = !isLoading && modelLoaded != null && from.isNotBlank() && to.isNotBlank() && transportMode.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    if (isLoading) "Generating..." else "Generate Itinerary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
