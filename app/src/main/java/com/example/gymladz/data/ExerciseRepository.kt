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
    
    private fun getFallbackExercises(): List<Exercise> {
        return listOf(
            // Upper Body - Push Movements
            Exercise(
                id = 1,
                name = "Push-ups",
                category = "Strength",
                duration = 15,
                calories = 120,
                difficulty = Difficulty.BEGINNER,
                emoji = "💪"
            ),
            Exercise(
                id = 2,
                name = "Diamond Push-ups",
                category = "Strength",
                duration = 12,
                calories = 100,
                difficulty = Difficulty.INTERMEDIATE,
                emoji = "💎"
            ),
            Exercise(
                id = 3,
                name = "Wide Push-ups",
                category = "Strength",
                duration = 15,
                calories = 130,
                difficulty = Difficulty.BEGINNER,
                emoji = "🦅"
            ),
            
            // Lower Body - Squats
            Exercise(
                id = 4,
                name = "Squats",
                category = "Strength",
                duration = 20,
                calories = 150,
                difficulty = Difficulty.BEGINNER,
                emoji = "🏋️"
            ),
            Exercise(
                id = 5,
                name = "Jump Squats",
                category = "Cardio",
                duration = 15,
                calories = 180,
                difficulty = Difficulty.INTERMEDIATE,
                emoji = "🚀"
            ),
            Exercise(
                id = 6,
                name = "Sumo Squats",
                category = "Strength",
                duration = 18,
                calories = 140,
                difficulty = Difficulty.BEGINNER,
                emoji = "🤺"
            ),
            
            // Core - Planks
            Exercise(
                id = 7,
                name = "Plank",
                category = "Flexibility",
                duration = 10,
                calories = 80,
                difficulty = Difficulty.BEGINNER,
                emoji = "🧘"
            ),
            Exercise(
                id = 8,
                name = "Side Plank",
                category = "Flexibility",
                duration = 12,
                calories = 90,
                difficulty = Difficulty.INTERMEDIATE,
                emoji = "🌟"
            ),
            
            // Lower Body - Lunges
            Exercise(
                id = 9,
                name = "Lunges",
                category = "Strength",
                duration = 15,
                calories = 130,
                difficulty = Difficulty.BEGINNER,
                emoji = "🦵"
            ),
            Exercise(
                id = 10,
                name = "Jump Lunges",
                category = "Cardio",
                duration = 12,
                calories = 160,
                difficulty = Difficulty.ADVANCED,
                emoji = "⚡"
            ),
            
            // Cardio
            Exercise(
                id = 11,
                name = "Jumping Jacks",
                category = "Cardio",
                duration = 10,
                calories = 100,
                difficulty = Difficulty.BEGINNER,
                emoji = "🎯"
            ),
            Exercise(
                id = 12,
                name = "High Knees",
                category = "Cardio",
                duration = 10,
                calories = 110,
                difficulty = Difficulty.BEGINNER,
                emoji = "🏃"
            ),
            Exercise(
                id = 13,
                name = "Mountain Climbers",
                category = "Cardio",
                duration = 12,
                calories = 140,
                difficulty = Difficulty.INTERMEDIATE,
                emoji = "⛰️"
            ),
            
            // Core - Dynamic
            Exercise(
                id = 14,
                name = "Crunches",
                category = "Flexibility",
                duration = 15,
                calories = 90,
                difficulty = Difficulty.BEGINNER,
                emoji = "📐"
            ),
            Exercise(
                id = 15,
                name = "Bicycle Crunches",
                category = "Flexibility",
                duration = 12,
                calories = 110,
                difficulty = Difficulty.INTERMEDIATE,
                emoji = "🚴"
            ),
            
            // Advanced Full Body
            Exercise(
                id = 16,
                name = "Burpees",
                category = "Cardio",
                duration = 15,
                calories = 200,
                difficulty = Difficulty.ADVANCED,
                emoji = "🔥"
            )
        )
    }
    
    private fun getEmojiForCategory(category: String): String {
        return when (category.lowercase()) {
            "arms", "chest", "shoulders" -> "💪"
            "legs", "calves" -> "🦵"
            "abs", "back" -> "🧘"
            "cardio" -> "🏃"
            else -> "🏋️"
        }
    }
}
