package com.example.bitewise.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitewise.model.Meal
import com.example.bitewise.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GenerateUiState(
    val currentPlan: List<Meal> = emptyList(),
    val searchResults: List<Meal> = emptyList()
)

class GenerateViewModel : ViewModel() {
    private val repo = MealRepository()
    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState
    private val _savedPlans = mutableStateListOf<List<Meal>>()
    val savedPlans: List<List<Meal>> get() = _savedPlans

    fun searchByName(query: String) = viewModelScope.launch {
        val results = repo.searchMealsByName(query)
        _uiState.value = _uiState.value.copy(searchResults = results)
    }

    fun addMeal(meal: Meal) {
        val updated = _uiState.value.currentPlan + meal
        _uiState.value = _uiState.value.copy(currentPlan = updated)
    }

    fun generateFullPlan(ings: List<String>) = viewModelScope.launch {
        val plan = repo.generateThreeMeals(ings)
        _uiState.value = _uiState.value.copy(currentPlan = plan)
    }

    fun saveCurrentPlan() {
        if (_uiState.value.currentPlan.size == 3) {
            _savedPlans.add(_uiState.value.currentPlan)
            // reset currentPlan if you like
            _uiState.value = _uiState.value.copy(currentPlan = emptyList())
        }
    }

    fun removeMeal(meal: Meal) {
        _uiState.value = _uiState.value.copy(
            currentPlan = _uiState.value.currentPlan.filterNot { it.id == meal.id }
        )
    }

    // Clear all meals
    fun clearCurrentPlan() {
        _uiState.value = _uiState.value.copy(currentPlan = emptyList())
    }

    fun searchByIngredients(ings: List<String>) = viewModelScope.launch {
        val joined = ings.joinToString(",") { it.lowercase().replace(" ", "_") }
        val results = repo.searchMealsByIngredient(joined)
        _uiState.value = _uiState.value.copy(searchResults = results)
    }

    // add this to track which meal is selected for detail:
    private val _selected = mutableStateOf<Meal?>(null)
    val selectedMeal: Meal? get() = _selected.value
    fun selectMeal(meal: Meal) { _selected.value = meal }
}