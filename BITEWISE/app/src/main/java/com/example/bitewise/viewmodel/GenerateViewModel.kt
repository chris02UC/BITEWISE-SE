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
    val searchResults: List<Meal> = emptyList()
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
        val results = repo.searchMealsByName(query)
        _uiState.value = _uiState.value.copy(searchResults = results)
    }

    fun searchByIngredients(ings: List<String>) = viewModelScope.launch {
        val joined = ings.joinToString(",") { it.lowercase().replace(" ", "_") }
        val results = repo.searchMealsByIngredient(joined)
        _uiState.value = _uiState.value.copy(searchResults = results)
    }

    fun generateFullPlan(ings: List<String>) = viewModelScope.launch {
        val plan = repo.generateThreeMeals(ings)
        _uiState.value = _uiState.value.copy(currentPlan = plan)
    }

    // --- editing currentPlan ---
    fun addMeal(meal: Meal) {
        val updated = (_uiState.value.currentPlan + meal).distinct().take(3)
        _uiState.value = _uiState.value.copy(currentPlan = updated)
    }

    fun removeMeal(meal: Meal) {
        _uiState.value = _uiState.value.copy(
            currentPlan = _uiState.value.currentPlan.filterNot { it.id == meal.id }
        )
    }

    fun clearCurrentPlan() {
        _uiState.value = _uiState.value.copy(currentPlan = emptyList())
    }

    // --- saving to savedPlans ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentPlan() {
        val meals = _uiState.value.currentPlan
        if (meals.size == 3) {
            val newPlan = MealPlan(
                id = planIdCounter.getAndIncrement(),
                dateCreated = LocalDateTime.now(),
                meals = meals
            )
            _savedPlans.add(newPlan)
            _uiState.value = _uiState.value.copy(currentPlan = emptyList())
        }
    }

    // --- selection for navigation ---
    fun selectMeal(meal: Meal) {
        _selectedMeal.value = meal
    }

    fun selectPlan(plan: MealPlan) {
        _selectedPlan.value = plan
    }
}