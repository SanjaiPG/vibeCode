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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runanywhere.startup_hackathon20.ChatViewModel
import com.runanywhere.startup_hackathon20.data.model.PlanForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePlanScreen(onPlanCreated: (String) -> Unit, vm: ChatViewModel = viewModel()) {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var nights by remember { mutableStateOf("3") }
    var budget by remember { mutableStateOf("50000") }
    var people by remember { mutableStateOf("2") }

    val isLoading by vm.isLoading.collectAsState()
    val modelLoaded by vm.currentModelId.collectAsState()
    val scrollState = rememberScrollState()

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

            CustomTextField(
                value = to,
                onValueChange = { to = it },
                label = "To",
                placeholder = "Paris",
                icon = Icons.Filled.Place
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

            // Status Message
            if (modelLoaded == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "⚠️ Please load an AI model first from the Chat tab",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Generate Button
            Button(
                onClick = {
                    val form = PlanForm(from, to, startDate, nights.toIntOrNull() ?: 3, budget.toIntOrNull() ?: 50000, people.toIntOrNull() ?: 2)
                    vm.generatePlanFromForm(form) { id -> onPlanCreated(id) }
                },
                enabled = !isLoading && modelLoaded != null && from.isNotBlank() && to.isNotBlank(),
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
