package com.example.gymladz.data.workout

import java.util.UUID

/**
 * Data models for workout tracking system
 */

// Workout session data
data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: Int,
    val exerciseName: String,
    val goalReps: Int,
    val completedReps: Int,
    val duration: Long, // milliseconds
    val caloriesBurned: Int,
    val formQuality: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true
)

// Workout plan
data class WorkoutPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exercises: List<PlannedExercise>,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long? = null
)

// Exercise in a workout plan
data class PlannedExercise(
    val exerciseId: Int,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val restTime: Int = 60 // seconds between sets
)

// Workout history summary
data class WorkoutHistory(
    val sessions: List<WorkoutSession> = emptyList(),
    val totalWorkouts: Int = 0,
    val totalReps: Int = 0,
    val totalCalories: Int = 0,
    val currentStreak: Int = 0
) {
    companion object {
        fun fromSessions(sessions: List<WorkoutSession>): WorkoutHistory {
            val totalReps = sessions.sumOf { it.completedReps }
            val totalCalories = sessions.sumOf { it.caloriesBurned }
            val streak = calculateStreak(sessions)
            
            return WorkoutHistory(
                sessions = sessions,
                totalWorkouts = sessions.size,
                totalReps = totalReps,
                totalCalories = totalCalories,
                currentStreak = streak
            )
        }
        
        private fun calculateStreak(sessions: List<WorkoutSession>): Int {
            if (sessions.isEmpty()) return 0
            
            val sortedSessions = sessions.sortedByDescending { it.timestamp }
            var streak = 0
            var lastDate = System.currentTimeMillis()
            
            for (session in sortedSessions) {
                val daysDiff = (lastDate - session.timestamp) / (1000 * 60 * 60 * 24)
                if (daysDiff <= 1) {
                    streak++
                    lastDate = session.timestamp
                } else {
                    break
                }
            }
            
            return streak
        }
    }
}

// Personal best for an exercise
data class PersonalBest(
    val exerciseId: Int,
    val exerciseName: String,
    val maxReps: Int,
    val date: Long
)
