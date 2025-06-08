package com.example.bitewise.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bitewise.viewmodel.GenerateViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    vm: GenerateViewModel,
    onBack: () -> Unit,
    fromSaved: Boolean         // new flag
) {
    val meal = vm.selectedMeal ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meal.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = meal.thumb("large"),
                    contentDescription = meal.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(Modifier.height(16.dp))

                // Added Ingredients section
                Text("Ingredients:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                meal.ingredients.forEach { ingredient ->
                    Text(
                        text = "• $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))

                Text("Instructions:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(meal.instructions, style = MaterialTheme.typography.bodyMedium)

                // only show this when NOT viewing a saved plan
                if (!fromSaved) {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            vm.addMeal(meal)
                            onBack()
                        },
                        enabled = vm.uiState.value.currentPlan.size < 3,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Meal Plan")
                    }
                }
            }
        }
    )
}