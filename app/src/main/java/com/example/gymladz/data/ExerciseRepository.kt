package com.example.gymladz.data

import com.example.gymladz.data.api.CategoryMapper
import com.example.gymladz.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseRepository {
    
    private val apiService = RetrofitClient.apiService
    
    suspend fun getExercises(): Result<List<Exercise>> = withContext(Dispatchers.IO) {
        try {
            // Fetch both exercises and exercise info
            val exercisesResponse = apiService.getExercises(limit = 50)
            val exerciseInfoResponse = apiService.getExerciseInfo(limit = 100)
            
            // Create a map of exercise ID to name/description
            val exerciseInfoMap = exerciseInfoResponse.results
                .groupBy { it.exercise }
                .mapValues { it.value.firstOrNull() }
            
            // Convert API models to UI models
            val exercises = exercisesResponse.results.mapNotNull { apiExercise ->
                val info = exerciseInfoMap[apiExercise.id]
                if (info != null && info.name.isNotBlank()) {
                    val categoryName = CategoryMapper.getCategoryName(apiExercise.category)
                    val filterCategory = CategoryMapper.getCategoryForFilter(categoryName)
                    
                    Exercise(
                        id = apiExercise.id,
                        name = info.name,
                        category = filterCategory,
                        duration = (15..60).random(), // Random duration since API doesn't provide
                        calories = (50..500).random(), // Random calories
                        difficulty = when {
                            apiExercise.equipment.isEmpty() -> Difficulty.BEGINNER
                            apiExercise.equipment.size == 1 -> Difficulty.INTERMEDIATE
                            else -> Difficulty.ADVANCED
                        },
                        emoji = getEmojiForCategory(categoryName)
                    )
                } else null
            }
            
            Result.success(exercises)
        } catch (e: Exception) {
            // Return fallback data on error
            Result.success(getFallbackExercises())
        }
    }
    
    fun getCategories(): List<String> = listOf(
        "All",
        "Cardio",
        "Strength",
        "Flexibility"
    )
    
    private fun getEmojiForCategory(category: String): String = when (category) {
        "Arms" -> "💪"
        "Legs" -> "🦵"
        "Abs" -> "🧘"
        "Chest" -> "🏋️"
        "Back" -> "🏋️"
        "Shoulders" -> "💪"
        "Calves" -> "🦵"
        "Cardio" -> "🏃"
        else -> "🤸"
    }
    
    private fun getFallbackExercises(): List<Exercise> = listOf(
        Exercise(
            id = 1,
            name = "Morning Run",
            category = "Cardio",
            duration = 30,
            calories = 300,
            difficulty = Difficulty.BEGINNER,
            emoji = "🏃"
        ),
        Exercise(
            id = 2,
            name = "Cycling",
            category = "Cardio",
            duration = 45,
            calories = 400,
            difficulty = Difficulty.INTERMEDIATE,
            emoji = "🚴"
        ),
        Exercise(
            id = 3,
            name = "Push-ups",
            category = "Strength",
            duration = 15,
            calories = 100,
            difficulty = Difficulty.BEGINNER,
            emoji = "💪"
        ),
        Exercise(
            id = 4,
            name = "Yoga Flow",
            category = "Flexibility",
            duration = 30,
            calories = 150,
            difficulty = Difficulty.BEGINNER,
            emoji = "🧘"
        ),
        Exercise(
            id = 5,
            name = "Weight Training",
            category = "Strength",
            duration = 60,
            calories = 450,
            difficulty = Difficulty.ADVANCED,
            emoji = "🏋️"
        )
    )
}
