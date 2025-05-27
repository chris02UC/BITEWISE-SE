package com.example.bitewise.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): SearchResponse

    @GET("filter.php")
    suspend fun filterByIngredients(@Query("i") ingredients: String): FilterResponse

    @GET("lookup.php")
    suspend fun lookupMeal(@Query("i") id: String): LookupResponse

    @GET("random.php")
    suspend fun randomMeal(): SearchResponse

    @GET("randomselection.php")
    suspend fun randomSelection(): RandomSelectionResponse
}