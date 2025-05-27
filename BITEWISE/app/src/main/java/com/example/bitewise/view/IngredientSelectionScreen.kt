package com.example.bitewise.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bitewise.viewmodel.GenerateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSelectionScreen(
    onSearch: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    // Hard-coded catalog of common ingredients
    val allIngredients = listOf(
        "Chicken", "Beef", "Pork", "Fish", "Shrimp",
        "Tomato", "Onion", "Garlic", "Potato", "Carrot",
        "Rice", "Pasta", "Cheese", "Milk", "Egg",
        "Pepper", "Salt", "Olive Oil", "Butter", "Basil",
        "Cilantro", "Ginger", "Mushroom", "Spinach", "Broccoli"
    ).sorted()

    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        allIngredients.filter {
            it.contains(query.trim(), ignoreCase = true)
        }
    }

    val selected = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Ingredients") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search Ingredients") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filtered) { ingredient ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selected.contains(ingredient),
                                onCheckedChange = { checked ->
                                    if (checked) selected.add(ingredient)
                                    else selected.remove(ingredient)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(ingredient, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Button(
                    onClick = { onSearch(selected.toList()) },
                    enabled = selected.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Search by ${selected.size} Ingredient(s)")
                }
            }
        }
    )
}