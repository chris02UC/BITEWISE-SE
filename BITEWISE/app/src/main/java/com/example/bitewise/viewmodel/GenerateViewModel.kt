package com.example.bitewise.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitewise.model.Meal // Assuming Meal.kt is in this package
import com.example.bitewise.model.MealPlan // Assuming MealPlan.kt is in this package
import com.example.bitewise.repository.MealRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Data class GenerateUiState from your original project structure
data class GenerateUiState(
    val currentPlan: List<Meal> = emptyList(),
    val searchResults: List<Meal> = emptyList(),
    val infoMessage: String? = null
)

class GenerateViewModel : ViewModel() {
    private val repo = MealRepository()
    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState

    // Firebase references - Updated with your specific URL
    private val database = FirebaseDatabase.getInstance("https://se-alp-auth-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var userId: String? = null
    private var userPlansRef: DatabaseReference? = null
    private var nextPlanIdRef: DatabaseReference? = null
    private var plansValueEventListener: ValueEventListener? = null


    private val _savedPlans = mutableStateListOf<MealPlan>()
    val savedPlans: List<MealPlan> get() = _savedPlans

    private val _selectedMeal = mutableStateOf<Meal?>(null)
    val selectedMeal: Meal? get() = _selectedMeal.value

    private val _selectedPlan = mutableStateOf<MealPlan?>(null)
    val selectedPlan: MealPlan? get() = _selectedPlan.value

    init {
        // Attempt to initialize with current user, if any.
        // Proper initialization will happen via reinitializeForUser upon login.
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        currentFirebaseUser?.uid?.let {
            reinitializeForUser(it)
        }
    }

    fun reinitializeForUser(newUserId: String) {
        if (userId == newUserId && plansValueEventListener != null) return // Already initialized for this user

        clearUserSpecificData() // Clear data and listeners for any previous user

        userId = newUserId
        userPlansRef = database.getReference("user_saved_plans").child(newUserId).child("meal_plans")
        nextPlanIdRef = database.getReference("user_saved_plans").child(newUserId).child("nextPlanId")
        fetchSavedPlans()
    }

    private fun fetchSavedPlans() {
        // Remove any existing listener before adding a new one
        plansValueEventListener?.let { listener ->
            userPlansRef?.removeEventListener(listener)
        }

        plansValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _savedPlans.clear()
                snapshot.children.forEach { planSnapshot ->
                    val planMap = planSnapshot.getValue<Map<String, Any>>()
                    if (planMap != null) {
                        try {
                            val dateCreatedStr = planMap["dateCreated"] as? String
                            val dateCreated = if (dateCreatedStr != null) {
                                try { LocalDateTime.parse(dateCreatedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
                                catch (e: DateTimeParseException) { LocalDateTime.now() /* Fallback or log error */ }
                            } else {
                                LocalDateTime.now() // Fallback if dateCreated is missing
                            }

                            val mealsList = (planMap["meals"] as? List<Map<String, Any>>)?.mapNotNull {
                                mealMapToMeal(it)
                            } ?: emptyList()

                            val plan = MealPlan(
                                id = (planMap["id"] as? Number)?.toLong() ?: 0L, // Handle potential Long or Double from Firebase
                                dateCreated = dateCreated,
                                meals = mealsList
                            )
                            _savedPlans.add(plan)
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(infoMessage = "Error parsing a saved plan: ${e.localizedMessage}")
                        }
                    }
                }
                _savedPlans.sortBy { it.id }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = _uiState.value.copy(infoMessage = "Failed to load plans: ${error.message}")
            }
        }
        userPlansRef?.addValueEventListener(plansValueEventListener!!)
    }

    private fun mealMapToMeal(map: Map<String, Any>): Meal? {
        return try {
            Meal(
                id = map["id"] as String,
                name = map["name"] as String,
                category = map["category"] as? String,
                area = map["area"] as? String,
                instructions = map["instructions"] as String,
                thumbnail = map["thumbnail"] as String,
                tags = map["tags"] as? String,
                youtube = map["youtube"] as? String,
                ingredients = map["ingredients"] as? List<String> ?: emptyList()
            )
        } catch (e: Exception) {
            // Log error or handle it
            null
        }
    }

    fun searchByName(query: String) = viewModelScope.launch {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                infoMessage = "Please enter meal name"
            )
            return@launch
        }
        val results = repo.searchMealsByName(query)
        _uiState.value = _uiState.value.copy(
            searchResults = results,
            infoMessage = if (results.isEmpty()) "No meals found. Try different keywords?" else null
        )
    }

    fun searchByIngredients(ings: List<String>) = viewModelScope.launch {
        if (ings.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                infoMessage = "Please select ingredients to filter."
            )
            return@launch
        }
        val joined = ings.joinToString(",") { it.lowercase().replace(" ", "_") }
        val results = repo.searchMealsByIngredient(joined)
        _uiState.value = _uiState.value.copy(
            searchResults = results,
            infoMessage = if (results.isEmpty()) "No meals found for your filters." else null
        )
    }

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
            else -> {
                val randomThreeMeals = distinctMeals.shuffled().take(3)
                _uiState.value = _uiState.value.copy(
                    currentPlan = randomThreeMeals,
                    searchResults = emptyList(),
                    infoMessage = "3 random meals added to your current plan."
                )
            }
        }
    }

    fun generateFullPlan(ings: List<String>) = viewModelScope.launch {
        if (ings.isEmpty()) {
            _uiState.value = _uiState.value.copy(infoMessage = "Cannot generate full plan without ingredients.")
            return@launch
        }
        val plan = repo.generateThreeMeals(ings)
        _uiState.value = _uiState.value.copy(
            currentPlan = plan,
            searchResults = emptyList(),
            infoMessage = if (plan.isEmpty()) "No meals found for your filters." else "${plan.size} meals generated for the plan."
        )
    }

    fun addMeal(meal: Meal) {
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
            _uiState.value = _uiState.value.copy(infoMessage = null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentPlan() {
        if (userPlansRef == null || nextPlanIdRef == null || userId == null) {
            _uiState.value = _uiState.value.copy(infoMessage = "Cannot save plan: User not identified or not logged in.")
            return
        }
        val mealsToSave = _uiState.value.currentPlan
        if (mealsToSave.size == 3) {
            nextPlanIdRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentNextId = snapshot.getValue(Long::class.java) ?: 1L
                    val newPlanFirebaseId = currentNextId // This ID is for the node in Firebase for this plan

                    val newPlan = MealPlan(
                        id = newPlanFirebaseId, // Using the Firebase-managed ID for consistency
                        dateCreated = LocalDateTime.now(),
                        meals = mealsToSave
                    )

                    val planMap = mapOf(
                        "id" to newPlan.id,
                        "dateCreated" to newPlan.dateCreated.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "meals" to newPlan.meals.map { mealToMap(it) }
                    )

                    userPlansRef!!.child(newPlanFirebaseId.toString()).setValue(planMap)
                        .addOnSuccessListener {
                            nextPlanIdRef!!.setValue(newPlanFirebaseId + 1) // Increment for the next plan
                            _uiState.value = _uiState.value.copy(
                                currentPlan = emptyList(),
                                searchResults = emptyList(),
                                infoMessage = "Plan #${newPlan.id} saved successfully!"
                            )
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(infoMessage = "Failed to save plan: ${e.message}")
                        }
                }
                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = _uiState.value.copy(infoMessage = "Failed to get next plan ID: ${error.message}")
                }
            })
        } else {
            _uiState.value = _uiState.value.copy(infoMessage = "Your plan needs 3 meals to be saved.")
        }
    }

    private fun mealToMap(meal: Meal): Map<String, Any?> {
        return mapOf(
            "id" to meal.id,
            "name" to meal.name,
            "category" to meal.category,
            "area" to meal.area,
            "instructions" to meal.instructions,
            "thumbnail" to meal.thumbnail,
            "tags" to meal.tags,
            "youtube" to meal.youtube,
            "ingredients" to meal.ingredients
        )
    }

    fun clearInfoMessage() {
        _uiState.value = _uiState.value.copy(infoMessage = null)
    }

    fun selectMeal(meal: Meal) {
        _selectedMeal.value = meal
    }

    fun selectPlan(plan: MealPlan) {
        _selectedPlan.value = plan
    }

    fun deleteSelectedPlan() {
        val planToDelete = _selectedPlan.value
        if (userPlansRef == null || userId == null) {
            _uiState.value = _uiState.value.copy(infoMessage = "Cannot delete plan: User not identified.")
            return
        }
        if (planToDelete != null) {
            val planIdForDeletion = planToDelete.id.toString()
            userPlansRef!!.child(planIdForDeletion).removeValue()
                .addOnSuccessListener {
                    _uiState.value = _uiState.value.copy(
                        infoMessage = "Plan #$planIdForDeletion deleted."
                    )
                    _selectedPlan.value = null
                }
                .addOnFailureListener { e ->
                    _uiState.value = _uiState.value.copy(
                        infoMessage = "Error deleting Plan #$planIdForDeletion: ${e.message}"
                    )
                }
        } else {
            _uiState.value = _uiState.value.copy(infoMessage = "No plan selected to delete.")
        }
    }

    fun clearUserSpecificData() {
        plansValueEventListener?.let { listener ->
            userPlansRef?.removeEventListener(listener) // Important: remove listener
        }
        plansValueEventListener = null
        _savedPlans.clear()
        _uiState.value = _uiState.value.copy(
            currentPlan = emptyList(),
            searchResults = emptyList(),
            // Keep infoMessage or clear it as needed, e.g., "User logged out, data cleared."
            // infoMessage = "User logged out, local data cleared."
        )
        userId = null
        userPlansRef = null
        nextPlanIdRef = null
        _selectedPlan.value = null
        _selectedMeal.value = null
    }
}