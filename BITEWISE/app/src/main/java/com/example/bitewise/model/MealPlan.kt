package com.example.bitewise.model

import java.time.LocalDateTime

data class MealPlan(
    val id: Long,                          // auto‐increment
    val dateCreated: LocalDateTime,
    val meals: List<Meal>
)