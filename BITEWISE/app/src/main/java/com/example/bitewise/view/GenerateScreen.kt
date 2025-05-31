package com.example.bitewise.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.ArrowBack // Not used directly here
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star // For the new button icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bitewise.model.Meal
import com.example.bitewise.viewmodel.GenerateViewModel
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    vm: GenerateViewModel = viewModel(),
    onDetail: () -> Unit,
    onPlan: () -> Unit,
    onIngredientSelect: () -> Unit,
    onAutoGenerateIngredientSelect: () -> Unit // New callback from AppNavHost
) {
    val state by vm.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    // For showing snackbar messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Show info message from ViewModel in a Snackbar
    // This LaunchedEffect will trigger when state.infoMessage changes.
    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            vm.clearInfoMessage() // Important: Clear the message in VM after showing
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost to Scaffold
        topBar = {
            TopAppBar(
                title = { Text("Generate Meal Plan") },
                actions = {
                    IconButton(onClick = onPlan, enabled = state.currentPlan.isNotEmpty()) {
                        Icon(Icons.Default.List, contentDescription = "View Plan")
                        Spacer(Modifier.width(4.dp))
                        Text("${state.currentPlan.size}/3")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding) // Apply padding from Scaffold
                    .padding(16.dp) // Your screen's own padding
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Meal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // Row for existing search buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { vm.searchByName(input) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Search by Name")
                    }
                    Button(
                        onClick = onIngredientSelect, // This is for the general ingredient filter
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("By Ingredients") // Suggestion: Rename for clarity vs. new button
                    }
                }

                Spacer(Modifier.height(12.dp)) // Space before the new button

                // New Button for Auto-Generate Plan by Ingredients
                Button(
                    onClick = onAutoGenerateIngredientSelect, // Use the new callback
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Star, contentDescription = "Auto-generate") // Example Icon
                    Spacer(Modifier.width(4.dp))
                    Text("Auto-Generate Plan by Ingredients")
                }

                Spacer(Modifier.height(16.dp))

                Text("Search Results:", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))

                // Displaying search results or a message if search results are empty
                if (state.searchResults.isEmpty()) {
                    // You can choose to show a default message here if needed,
                    // or let the Snackbar handle all informational messages.
                    // For instance, if a search by name yielded no results,
                    // the ViewModel would set infoMessage, handled by Snackbar.
                    Text(
                        "No search results to display. Try a different search or filter.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Ensure LazyColumn takes available space
                    ) {
                        items(state.searchResults) { meal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        vm.selectMeal(meal)
                                        onDetail()
                                    }
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AsyncImage(
                                        model = meal.thumb("small"),
                                        contentDescription = meal.name,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) { // Allow text to take space
                                        Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                                        Text(
                                            "Tap to view details",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}