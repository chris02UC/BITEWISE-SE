package com.example.bitewise.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitewise.model.Meal
import com.example.bitewise.model.MealPlan
import com.example.bitewise.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

data class GenerateUiState(
    val currentPlan: List<Meal> = emptyList(),
    val searchResults: List<Meal> = emptyList(),
    val infoMessage: String? = null // For messages like "No meals found" or "Plan populated"
)

class GenerateViewModel : ViewModel() {
    private val repo = MealRepository()
    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState

    // --- MealPlan storage ---
    private val planIdCounter = AtomicLong(1)
    private val _savedPlans = mutableStateListOf<MealPlan>()
    val savedPlans: List<MealPlan> get() = _savedPlans

    // --- current selections ---
    private val _selectedMeal     = mutableStateOf<Meal?>(null)
    val selectedMeal: Meal?      get() = _selectedMeal.value

    private val _selectedPlan     = mutableStateOf<MealPlan?>(null)
    val selectedPlan: MealPlan?  get() = _selectedPlan.value

    // --- API searches ---
    fun searchByName(query: String) = viewModelScope.launch {
        // **MODIFIED for "Please enter meal name"**
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(), // Clear previous results
                infoMessage = "Please enter meal name"
            )
            return@launch
        }

        val results = repo.searchMealsByName(query)
        _uiState.value = _uiState.value.copy(
            searchResults = results,
            // **MODIFIED for "No meals found. Try different keywords?"**
            infoMessage = if (results.isEmpty()) "No meals found. Try different keywords?" else null
        )
    }

    fun searchByIngredients(ings: List<String>) = viewModelScope.launch {
        if (ings.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                infoMessage = "Please select ingredients to filter." // Default message for empty selection
            )
            return@launch
        }
        val joined = ings.joinToString(",") { it.lowercase().replace(" ", "_") }
        val results = repo.searchMealsByIngredient(joined)
        _uiState.value = _uiState.value.copy(
            searchResults = results,
            // **MODIFIED for "No meals found for your filters." (when searching by ingredients)**
            infoMessage = if (results.isEmpty()) "No meals found for your filters." else null
        )
    }

    // New function for auto-generating and populating the plan
    fun autoGeneratePlanFromIngredients(selectedIngredients: List<String>) = viewModelScope.launch {
        if (selectedIngredients.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                currentPlan = emptyList(),
                searchResults = emptyList(),
                infoMessage = "Please select ingredients first for auto-generation."
            )
            return@launch
        }

        val mealsFound = repo.generateMealsFromIngredients(selectedIngredients)
        val distinctMeals = mealsFound.distinctBy { it.id }

        when {
            distinctMeals.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    currentPlan = emptyList(),
                    searchResults = emptyList(),
                    // **MODIFIED for "No meals found for your filters." (when auto-generating)**
                    infoMessage = "No meals found for your filters."
                )
            }
            distinctMeals.size == 1 -> {
                _uiState.value = _uiState.value.copy(
                    currentPlan = distinctMeals,
                    searchResults = emptyList(),
                    infoMessage = "1 meal added to your current plan."
                )
            }
            distinctMeals.size == 2 -> {
                _uiState.value = _uiState.value.copy(
                    currentPlan = distinctMeals,
                    searchResults = emptyList(),
                    infoMessage = "2 meals added to your current plan."
                )
            }
            else -> { // 3 or more meals
                val randomThreeMeals = distinctMeals.shuffled().take(3)
                _uiState.value = _uiState.value.copy(
                    currentPlan = randomThreeMeals,
                    searchResults = emptyList(),
                    infoMessage = "3 random meals added to your current plan."
                )
            }
        }
    }


    // This function was for the old "Generate Full Plan" button, might be redundant or repurposed.
    // If keeping, ensure it also handles infoMessage.
    fun generateFullPlan(ings: List<String>) = viewModelScope.launch {
        if (ings.isEmpty()) {
            _uiState.value = _uiState.value.copy(infoMessage = "Cannot generate full plan without ingredients.")
            return@launch
        }
        val plan = repo.generateThreeMeals(ings)
        _uiState.value = _uiState.value.copy(
            currentPlan = plan,
            searchResults = emptyList(),
            // **MODIFIED for "No meals found for your filters." (for consistency if plan.isEmpty())**
            infoMessage = if (plan.isEmpty()) "No meals found for your filters." else "${plan.size} meals generated for the plan."
        )
    }

    // --- editing currentPlan ---
    fun addMeal(meal: Meal) {
        // Prevent adding if plan is full, unless you allow replacing.
        if (_uiState.value.currentPlan.size >= 3 && !_uiState.value.currentPlan.any { it.id == meal.id }) {
            _uiState.value = _uiState.value.copy(infoMessage = "Plan is full (3 meals). Remove a meal to add another.")
            return
        }
        val updated = (_uiState.value.currentPlan + meal).distinctBy { it.id }.take(3)
        _uiState.value = _uiState.value.copy(
            currentPlan = updated,
            infoMessage = if (updated.size > _uiState.value.currentPlan.size || meal !in _uiState.value.currentPlan) "${meal.name} added." else null
        )
    }

    fun removeMeal(meal: Meal) {
        val oldSize = _uiState.value.currentPlan.size
        val updatedPlan = _uiState.value.currentPlan.filterNot { it.id == meal.id }
        _uiState.value = _uiState.value.copy(
            currentPlan = updatedPlan,
            infoMessage = if (updatedPlan.size < oldSize) "${meal.name} removed." else null
        )
    }

    fun clearCurrentPlan() {
        if (_uiState.value.currentPlan.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(currentPlan = emptyList(), infoMessage = "Current plan cleared.")
        } else {
            _uiState.value = _uiState.value.copy(infoMessage = null) // Or "Plan is already empty."
        }
    }

    // --- saving to savedPlans ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentPlan() {
        val meals = _uiState.value.currentPlan
        // Allow saving if there's at least one meal, or enforce 3.
        // For this example, let's stick to the 3-meal requirement for saving.
        if (meals.size == 3) {
            val newPlan = MealPlan(
                id = planIdCounter.getAndIncrement(),
                dateCreated = LocalDateTime.now(),
                meals = meals
            )
            _savedPlans.add(newPlan)
            _uiState.value = _uiState.value.copy(
                currentPlan = emptyList(), // Clear after saving
                searchResults = emptyList(), // Clear search results
                infoMessage = "Plan #${newPlan.id} saved successfully!"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                infoMessage = "Your plan needs 3 meals to be saved."
            )
        }
    }

    // Function to manually clear the info message, e.g., when user dismisses it
    fun clearInfoMessage() {
        _uiState.value = _uiState.value.copy(infoMessage = null)
    }

    // --- selection for navigation ---
    fun selectMeal(meal: Meal) {
        _selectedMeal.value = meal
        // _uiState.value = _uiState.value.copy(infoMessage = null) // Clear message on navigation
    }

    fun selectPlan(plan: MealPlan) {
        _selectedPlan.value = plan
        // _uiState.value = _uiState.value.copy(infoMessage = null) // Clear message on navigation
    }

    // --- New function to delete the selected saved plan ---
    fun deleteSelectedPlan() {
        val planToDelete = _selectedPlan.value
        if (planToDelete != null) {
            val planId = planToDelete.id // Capture id for the message
            val removed = _savedPlans.remove(planToDelete)
            if (removed) {
                _uiState.value = _uiState.value.copy(
                    infoMessage = "Plan #$planId deleted."
                )
                _selectedPlan.value = null // Clear the selected plan as it's now deleted
            } else {
                _uiState.value = _uiState.value.copy(
                    infoMessage = "Error: Plan #$planId not found or could not be deleted."
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                infoMessage = "No plan selected to delete."
            )
        }
    }
}