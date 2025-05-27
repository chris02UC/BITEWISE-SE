package com.example.bitewise.model

data class Meal(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String,
    val thumbnail: String,       // original: ".../meals/xyz.jpg"
    val tags: String?,
    val youtube: String?,
    val ingredients: List<String>
) {
    /** Returns the URL for the given size: “small”, “medium”, or “large” */
    fun thumb(size: String = "small"): String =
        thumbnail.trimEnd('/') + "/$size"
}