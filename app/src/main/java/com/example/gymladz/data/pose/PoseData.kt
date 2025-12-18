package com.example.gymladz.data.pose

/**
 * Data classes for pose detection and exercise tracking
 */

// Exercise types supported
enum class ExerciseType {
    PUSH_UP,
    SQUAT,
    PLANK,
    JUMPING_JACKS
}

// Rep counting states
enum class RepState {
    UP,
    DOWN,
    TRANSITIONING
}

// Workout session data
data class ExerciseSession(
    val exerciseType: ExerciseType,
    val reps: Int,
    val duration: Long,
    val formQuality: Float,
    val timestamp: Long = System.currentTimeMillis()
)

// Pose landmark data
data class PoseLandmarkData(
    val x: Float,
    val y: Float,
    val z: Float,
    val confidence: Float
)

// Exercise feedback
data class ExerciseFeedback(
    val message: String,
    val isGoodForm: Boolean
)
