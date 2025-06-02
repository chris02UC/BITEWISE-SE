package com.example.bitewise.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("search.php")
    suspend fun searchByName(@retrofit2.http.Query("s") name: String): SearchResponse

    @GET("filter.php")
    suspend fun filterByIngredients(@retrofit2.http.Query("i") ingredients: String): FilterResponse

    @GET("lookup.php")
    suspend fun lookupMeal(@retrofit2.http.Query("i") id: String): LookupResponse

    @GET("random.php")
    suspend fun randomMeal(): SearchResponse

    @GET("randomselection.php")
    suspend fun randomSelection(): RandomSelectionResponse
}