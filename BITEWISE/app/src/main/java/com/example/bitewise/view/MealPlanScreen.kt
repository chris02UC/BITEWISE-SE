package com.example.bitewise.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Delete
import coil.compose.AsyncImage
import com.example.bitewise.viewmodel.GenerateViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    vm: GenerateViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val plan = vm.uiState.collectAsState().value.currentPlan

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Meal Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Clear All button
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
                if (plan.isEmpty()) {
                    Text("No meals added yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
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
                                            Icons.Default.Delete,
                                            contentDescription = "Remove ${meal.name}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onSave,
                    enabled = plan.size == 3,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Meal Plan")
                }
            }
        }
    )
}