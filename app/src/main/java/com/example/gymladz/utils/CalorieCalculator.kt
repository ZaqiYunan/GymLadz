package com.example.gymladz.utils

/**
 * Calculate calories burned during exercise
 * Based on MET (Metabolic Equivalent of Task) values
 */
object CalorieCalculator {
    
    // MET values for different exercises
    private val MET_VALUES = mapOf(
        "push-up" to 8.0,
        "pushup" to 8.0,
        "squat" to 5.0,
        "plank" to 4.0,
        "sit-up" to 8.0,
        "situp" to 8.0,
        "burpee" to 10.0,
        "jumping jack" to 8.0,
        "lunge" to 6.0,
        "pull-up" to 8.0,
        "pullup" to 8.0,
        "default" to 6.0
    )
    
    /**
     * Calculate calories burned
     * @param exerciseName Name of the exercise
     * @param reps Number of reps completed
     * @param userWeight User's weight in kg (default 70kg)
     * @return Estimated calories burned
     */
    fun calculateCalories(
        exerciseName: String,
        reps: Int,
        userWeight: Float = 70f
    ): Int {
        val metValue = getMETValue(exerciseName)
        
        // Estimate duration: ~3-4 seconds per rep for most exercises
        val secondsPerRep = when {
            exerciseName.contains("plank", ignoreCase = true) -> 60.0 // Plank is time-based
            exerciseName.contains("burpee", ignoreCase = true) -> 5.0
            else -> 3.5
        }
        
        val durationMinutes = (reps * secondsPerRep) / 60.0
        
        // Formula: Calories = (MET * weight in kg * duration in hours)
        val calories = metValue * userWeight * (durationMinutes / 60.0)
        
        return calories.toInt().coerceAtLeast(1)
    }
    
    /**
     * Calculate calories for a timed exercise (like plank)
     * @param exerciseName Name of the exercise
     * @param durationSeconds Duration in seconds
     * @param userWeight User's weight in kg
     * @return Estimated calories burned
     */
    fun calculateCaloriesForDuration(
        exerciseName: String,
        durationSeconds: Long,
        userWeight: Float = 70f
    ): Int {
        val metValue = getMETValue(exerciseName)
        val durationMinutes = durationSeconds / 60.0
        val calories = metValue * userWeight * (durationMinutes / 60.0)
        return calories.toInt().coerceAtLeast(1)
    }
    
    private fun getMETValue(exerciseName: String): Double {
        val key = exerciseName.lowercase().trim()
        return MET_VALUES.entries.find { key.contains(it.key) }?.value 
            ?: MET_VALUES["default"]!!
    }
}
