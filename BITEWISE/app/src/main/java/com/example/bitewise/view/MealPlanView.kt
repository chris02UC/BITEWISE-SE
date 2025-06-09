package com.example.bitewise.view

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bitewise.viewmodel.GenerateVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanView(
    vm: GenerateVM,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val plan = uiState.currentPlan
    val infoMessage = uiState.infoMessage
    val snackbarHostState = remember { SnackbarHostState() }

    // --- State for Drag and Drop ---
    val lazyListState = rememberLazyListState()
    var draggedDistance by remember { mutableStateOf(0f) }
    // The index of the item being dragged
    var currentlyDraggingIndex by remember { mutableStateOf<Int?>(null) }
    // The LazyListItemInfo of the item when the drag started
    var initialDraggingItemInfo by remember { mutableStateOf<LazyListItemInfo?>(null) }

    LaunchedEffect(infoMessage) {
        infoMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            vm.clearInfoMessage()
        }
    }

    // This is the core of the fix. It runs when an item is being dragged.
    LaunchedEffect(draggedDistance) {
        val draggingItem = initialDraggingItemInfo ?: return@LaunchedEffect
        val draggingIndex = currentlyDraggingIndex ?: return@LaunchedEffect

        // Calculate the new absolute Y position of the dragged item
        val draggingItemY = draggingItem.offset + draggedDistance

        // Find the item that the dragged item is currently hovering over
        val targetItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            // Check if the center of the dragging item is within the bounds of another item
            draggingItemY > item.offset && draggingItemY < (item.offset + item.size) && draggingIndex != item.index
        }

        if (targetItem != null) {
            // If we found a target, trigger the reorder in the ViewModel
            // This creates the "live" reordering effect
            vm.reorderCurrentPlan(draggingIndex, targetItem.index)
            // Update the currently dragging index to the new position
            currentlyDraggingIndex = targetItem.index
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Current Meal Plan (Drag to Reorder)") },
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
                if (plan.isEmpty()) {
                    Text("No meals added yet.", style = MaterialTheme.typography.bodyMedium)
                }

                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(plan, key = { _, meal -> meal.id }) { index, meal ->
                        val isDragging = index == currentlyDraggingIndex

                        // Apply a visual effect to the item being dragged
                        val cardModifier = if (isDragging) {
                            Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = draggedDistance
                                    shadowElevation = 8f
                                }
                        } else {
                            Modifier.fillMaxWidth()
                        }

                        // The Card UI, wrapped with the drag-and-drop logic
                        Box(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            // Directly use the known index instead of recalculating from offsets
                                            currentlyDraggingIndex = index
                                            initialDraggingItemInfo = lazyListState
                                                .layoutInfo
                                                .visibleItemsInfo
                                                .firstOrNull { it.index == index }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            draggedDistance += dragAmount.y
                                        },
                                        onDragEnd = {
                                            // Reset states when drag ends
                                            draggedDistance = 0f
                                            currentlyDraggingIndex = null
                                            initialDraggingItemInfo = null
                                        }
                                    )
                                }
                        ) {
                            Card(modifier = cardModifier) {
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
                                    IconButton(onClick = { vm.removeMeal(meal) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove ${meal.name}")
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
