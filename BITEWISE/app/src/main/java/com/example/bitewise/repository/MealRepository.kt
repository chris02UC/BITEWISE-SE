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
}