package com.example.bitewise.repository


import com.example.bitewise.model.Meal
import com.example.bitewise.network.NetworkModule
import com.example.bitewise.network.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MealRepository {
    private val api = NetworkModule.mealApiService

    suspend fun searchMealsByName(query: String): List<Meal> = withContext(Dispatchers.IO) {
        api.searchByName(query).meals.orEmpty().map { it.toDomain() }
    }

    suspend fun searchMealsByIngredient(ingredient: String): List<Meal> = withContext(Dispatchers.IO) {
        val ids = api.filterByIngredients(ingredient).meals.orEmpty().map { it.idMeal }
        ids.map { id ->
            api.lookupMeal(id).meals!!.first().toDomain()
        }
    }

    suspend fun generateThreeMeals(ingredients: List<String>): List<Meal> = withContext(Dispatchers.IO) {
        val param = ingredients.joinToString(",")
        val ids = api.filterByIngredients(param).meals.orEmpty().map { it.idMeal }
        ids.shuffled().take(3).map { id ->
            api.lookupMeal(id).meals!!.first().toDomain()
        }
    }

    suspend fun generateMealsFromIngredients(ingredients: List<String>): List<Meal> = withContext(Dispatchers.IO) {
        if (ingredients.isEmpty()) {
            return@withContext emptyList()
        }
        val joinedIngredients = ingredients.joinToString(",") { it.lowercase().replace(" ", "_") }
        val filteredResult = api.filterByIngredients(joinedIngredients) // Assuming this returns meals with ALL ingredients

        // If the API returns null or an empty list of meals in the 'meals' property of FilterResponse
        if (filteredResult.meals.isNullOrEmpty()) {
            return@withContext emptyList()
        }

        // Fetch full details for each meal ID obtained
        filteredResult.meals.mapNotNull { filterMealDto ->
            try {
                api.lookupMeal(filterMealDto.idMeal).meals?.firstOrNull()?.toDomain()
            } catch (e: Exception) {
                // Log error or handle it if a specific meal lookup fails
                null // Skip this meal if lookup fails
            }
        }.distinctBy { it.id } // Ensure uniqueness, though API usually provides this
    }

}