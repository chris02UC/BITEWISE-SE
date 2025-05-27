package com.example.bitewise.network

import com.example.bitewise.model.Meal
import com.google.gson.annotations.SerializedName

// --- Full-detail DTO for search/lookup/random endpoints ---
data class MealDto(
    @SerializedName("idMeal")           val idMeal: String,
    @SerializedName("strMeal")          val strMeal: String,
    @SerializedName("strCategory")      val strCategory: String?,
    @SerializedName("strArea")          val strArea: String?,
    @SerializedName("strInstructions")  val strInstructions: String?,
    @SerializedName("strMealThumb")     val strMealThumb: String?,
    @SerializedName("strTags")          val strTags: String?,
    @SerializedName("strYoutube")       val strYoutube: String?,
    @SerializedName("strSource")        val strSource: String?,

    @SerializedName("strIngredient1")   val ing1: String?,
    @SerializedName("strIngredient2")   val ing2: String?,
    @SerializedName("strIngredient3")   val ing3: String?,
    @SerializedName("strIngredient4")   val ing4: String?,
    @SerializedName("strIngredient5")   val ing5: String?,
    @SerializedName("strIngredient6")   val ing6: String?,
    @SerializedName("strIngredient7")   val ing7: String?,
    @SerializedName("strIngredient8")   val ing8: String?,
    @SerializedName("strIngredient9")   val ing9: String?,
    @SerializedName("strIngredient10")  val ing10: String?,
    @SerializedName("strIngredient11")  val ing11: String?,
    @SerializedName("strIngredient12")  val ing12: String?,
    @SerializedName("strIngredient13")  val ing13: String?,
    @SerializedName("strIngredient14")  val ing14: String?,
    @SerializedName("strIngredient15")  val ing15: String?,
    @SerializedName("strIngredient16")  val ing16: String?,
    @SerializedName("strIngredient17")  val ing17: String?,
    @SerializedName("strIngredient18")  val ing18: String?,
    @SerializedName("strIngredient19")  val ing19: String?,
    @SerializedName("strIngredient20")  val ing20: String?,

    @SerializedName("strMeasure1")      val measure1: String?,
    @SerializedName("strMeasure2")      val measure2: String?,
    @SerializedName("strMeasure3")      val measure3: String?,
    @SerializedName("strMeasure4")      val measure4: String?,
    @SerializedName("strMeasure5")      val measure5: String?,
    @SerializedName("strMeasure6")      val measure6: String?,
    @SerializedName("strMeasure7")      val measure7: String?,
    @SerializedName("strMeasure8")      val measure8: String?,
    @SerializedName("strMeasure9")      val measure9: String?,
    @SerializedName("strMeasure10")     val measure10: String?,
    @SerializedName("strMeasure11")     val measure11: String?,
    @SerializedName("strMeasure12")     val measure12: String?,
    @SerializedName("strMeasure13")     val measure13: String?,
    @SerializedName("strMeasure14")     val measure14: String?,
    @SerializedName("strMeasure15")     val measure15: String?,
    @SerializedName("strMeasure16")     val measure16: String?,
    @SerializedName("strMeasure17")     val measure17: String?,
    @SerializedName("strMeasure18")     val measure18: String?,
    @SerializedName("strMeasure19")     val measure19: String?,
    @SerializedName("strMeasure20")     val measure20: String?
)

// --- Minimal DTO for filter.php responses ---
data class FilterMealDto(
    @SerializedName("idMeal")       val idMeal: String,
    @SerializedName("strMeal")      val strMeal: String,
    @SerializedName("strMealThumb") val strMealThumb: String
)

// --- Wrapper responses ---
data class SearchResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

data class LookupResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

data class FilterResponse(
    @SerializedName("meals") val meals: List<FilterMealDto>?
)

data class RandomSelectionResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

// --- Mapper extension ---
fun MealDto.toDomain(): Meal {
    val raw = listOf(
        ing1, ing2, ing3, ing4, ing5, ing6, ing7, ing8, ing9, ing10,
        ing11, ing12, ing13, ing14, ing15, ing16, ing17, ing18, ing19, ing20
    )
    val measures = listOf(
        measure1, measure2, measure3, measure4, measure5, measure6, measure7, measure8, measure9, measure10,
        measure11, measure12, measure13, measure14, measure15, measure16, measure17, measure18, measure19, measure20
    )
    val ingredients = raw.mapIndexedNotNull { idx, ing ->
        ing?.takeIf(String::isNotBlank)?.let { "$it – ${measures.getOrNull(idx).orEmpty()}" }
    }
    return Meal(
        id           = idMeal,
        name         = strMeal,
        category     = strCategory,
        area         = strArea,
        instructions = strInstructions.orEmpty(),
        thumbnail    = strMealThumb.orEmpty(),
        tags         = strTags,
        youtube      = strYoutube,
        ingredients  = ingredients
    )
}