package com.example.bitewise.view

// import android.annotation.SuppressLint // Not strictly needed for this composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Assuming this is for the remove meal button
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import remember and getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import androidx.room.Delete // This import is incorrect here; material icons are used.
import coil.compose.AsyncImage
import com.example.bitewise.viewmodel.GenerateViewModel
// Redundant imports like Row, Arrangement, size are covered by androidx.compose.foundation.layout.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    vm: GenerateViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // Collect the entire UI state to access both currentPlan and infoMessage
    val uiState by vm.uiState.collectAsState()
    val plan = uiState.currentPlan
    val infoMessage = uiState.infoMessage

    // SnackbarHostState for managing Snackbar display
    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect to show Snackbar when infoMessage changes
    LaunchedEffect(infoMessage) {
        infoMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            vm.clearInfoMessage() // Clear the message in ViewModel after showing
        }
    }

    Scaffold(
        // Add SnackbarHost to the Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Current Meal Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { vm.clearCurrentPlan() }) {
                        Text("Clear All")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Conditional text if the plan is empty.
                // The Snackbar will show specific messages like "No meals found for your filters."
                // This text can be a more generic fallback.
                if (plan.isEmpty()) {
                    // If an info message exists (e.g., "No meals found..."),
                    // the Snackbar will show it. This text can be a fallback.
                    // Or, if you prefer the message to be directly in the content area:
                    // Text(infoMessage ?: "No meals added yet.", style = MaterialTheme.typography.bodyMedium)
                    Text("No meals added yet.", style = MaterialTheme.typography.bodyMedium)
                }

                // LazyColumn will be empty if 'plan' is empty.
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // Ensure LazyColumn takes available space
                ) {
                    items(plan) { meal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = meal.thumb("small"),
                                    contentDescription = meal.name,
                                    modifier = Modifier.size(56.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                                }
                                IconButton(
                                    onClick = { vm.removeMeal(meal) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete, // Corrected icon
                                        contentDescription = "Remove ${meal.name}"
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onSave,
                    enabled = plan.size == 3, // Keep existing save logic
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Meal Plan")
                }
            }
        }
    )
}