package com.example.gymladz.data

data class Exercise(
    val id: Int,
    val name: String,
    val category: String,
    val duration: Int, // in minutes
    val calories: Int,
    val difficulty: Difficulty,
    val emoji: String // Using emoji as icon for simplicity
)

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}
