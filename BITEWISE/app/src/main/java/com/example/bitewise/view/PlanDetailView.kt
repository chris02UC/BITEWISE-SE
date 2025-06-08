package com.example.bitewise.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Import Delete icon
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import remember and mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bitewise.viewmodel.GenerateVM
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
// Removed redundant import androidx.compose.ui.unit.dp


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailView(
    vm: GenerateVM,
    onBack: () -> Unit,
    onMealClick: () -> Unit
) {
    val plan = vm.selectedPlan ?: return
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) } // State for dialog

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Plan #${plan.id}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { // Add actions for the delete button
                    IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Plan")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Created: ${plan.dateCreated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(plan.meals) { meal ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                vm.selectMeal(meal)
                                onMealClick()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = meal.thumb("small"),
                                contentDescription = meal.name,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete Plan") },
            text = { Text("Are you sure you want to delete this meal plan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deleteSelectedPlan() // Call ViewModel function to delete
                        showDeleteConfirmationDialog = false
                        onBack() // Navigate back after deletion
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}