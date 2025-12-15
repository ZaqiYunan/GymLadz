package com.example.gymladz.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// API Response Models
@JsonClass(generateAdapter = true)
data class ExerciseResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ExerciseApiModel>
)

@JsonClass(generateAdapter = true)
data class ExerciseApiModel(
    val id: Int,
    val uuid: String,
    val created: String,
    @Json(name = "last_update") val lastUpdate: String,
    val category: Int,
    val muscles: List<Int>,
    @Json(name = "muscles_secondary") val musclesSecondary: List<Int>,
    val equipment: List<Int>,
    val variations: Int?,
    @Json(name = "license_author") val licenseAuthor: String
)

@JsonClass(generateAdapter = true)
data class ExerciseInfoResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ExerciseInfoApiModel>
)

@JsonClass(generateAdapter = true)
data class ExerciseInfoApiModel(
    val id: Int,
    val exercise: Int,
    val language: Int,
    val name: String,
    val description: String?,
    val created: String,
    @Json(name = "last_update") val lastUpdate: String
)

@JsonClass(generateAdapter = true)
data class CategoryResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<CategoryApiModel>
)

@JsonClass(generateAdapter = true)
data class CategoryApiModel(
    val id: Int,
    val name: String
)

// Category ID to Name mapping (from wger API)
object CategoryMapper {
    private val categoryMap = mapOf(
        8 to "Arms",
        9 to "Legs",
        10 to "Abs",
        11 to "Chest",
        12 to "Back",
        13 to "Shoulders",
        14 to "Calves",
        15 to "Cardio"
    )
    
    fun getCategoryName(id: Int): String = categoryMap[id] ?: "Other"
    
    fun getCategoryForFilter(name: String): String = when (name) {
        "Arms", "Chest", "Back", "Shoulders" -> "Strength"
        "Legs", "Calves" -> "Cardio"
        "Abs" -> "Flexibility"
        else -> "Cardio"
    }
}
