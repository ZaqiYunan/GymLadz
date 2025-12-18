package com.example.gymladz.data.pose

/**
 * Exercise-specific biomechanics data for accurate pose detection
 * Based on fitness research and proper form guidelines
 */
data class ExerciseBiomechanics(
    val name: String,
    val properFormAngles: FormAngles,
    val keyLandmarks: List<String>,
    val commonMistakes: List<String>,
    val minConfidence: Float = 0.6f
)

data class FormAngles(
    val downPosition: AngleRange,
    val upPosition: AngleRange,
    val bodyAlignment: AngleRange? = null
)

data class AngleRange(
    val min: Double,
    val max: Double,
    val ideal: Double
)

object ExerciseDatabase {
    
    /**
     * Push-up biomechanics based on fitness standards
     */
    val PUSH_UP = ExerciseBiomechanics(
        name = "Push-up",
        properFormAngles = FormAngles(
            downPosition = AngleRange(
                min = 70.0,    // Minimum elbow bend
                max = 100.0,   // Maximum elbow bend
                ideal = 90.0   // Perfect 90-degree angle
            ),
            upPosition = AngleRange(
                min = 160.0,   // Slightly bent is okay
                max = 180.0,   // Fully extended
                ideal = 170.0  // Ideal extension
            ),
            bodyAlignment = AngleRange(
                min = 160.0,   // Shoulder-hip-ankle alignment
                max = 180.0,
                ideal = 175.0  // Nearly straight line
            )
        ),
        keyLandmarks = listOf(
            "LEFT_SHOULDER", "LEFT_ELBOW", "LEFT_WRIST",
            "RIGHT_SHOULDER", "RIGHT_ELBOW", "RIGHT_WRIST",
            "LEFT_HIP", "LEFT_ANKLE"
        ),
        commonMistakes = listOf(
            "Hips sagging (body not straight)",
            "Not going low enough (< 90° elbow)",
            "Elbows flaring out too wide",
            "Head dropping or looking up"
        ),
        minConfidence = 0.65f
    )
    
    /**
     * Squat biomechanics based on fitness standards
     */
    val SQUAT = ExerciseBiomechanics(
        name = "Squat",
        properFormAngles = FormAngles(
            downPosition = AngleRange(
                min = 80.0,    // Deep squat
                max = 110.0,   // Parallel or slightly above
                ideal = 90.0   // Perfect parallel squat
            ),
            upPosition = AngleRange(
                min = 160.0,   // Nearly straight
                max = 180.0,   // Fully extended
                ideal = 170.0  // Ideal standing position
            ),
            bodyAlignment = AngleRange(
                min = 70.0,    // Torso angle (hip-shoulder-vertical)
                max = 100.0,
                ideal = 85.0   // Slight forward lean is good
            )
        ),
        keyLandmarks = listOf(
            "LEFT_HIP", "LEFT_KNEE", "LEFT_ANKLE",
            "RIGHT_HIP", "RIGHT_KNEE", "RIGHT_ANKLE",
            "LEFT_SHOULDER", "RIGHT_SHOULDER"
        ),
        commonMistakes = listOf(
            "Knees caving inward",
            "Not going deep enough (> 110° knee)",
            "Heels lifting off ground",
            "Leaning too far forward",
            "Knees going past toes excessively"
        ),
        minConfidence = 0.65f
    )
    
    /**
     * Plank biomechanics
     */
    val PLANK = ExerciseBiomechanics(
        name = "Plank",
        properFormAngles = FormAngles(
            downPosition = AngleRange(160.0, 180.0, 175.0),
            upPosition = AngleRange(160.0, 180.0, 175.0),
            bodyAlignment = AngleRange(
                min = 165.0,   // Body should be straight
                max = 180.0,
                ideal = 175.0
            )
        ),
        keyLandmarks = listOf(
            "LEFT_SHOULDER", "LEFT_HIP", "LEFT_ANKLE",
            "RIGHT_SHOULDER", "RIGHT_HIP", "RIGHT_ANKLE"
        ),
        commonMistakes = listOf(
            "Hips sagging",
            "Hips too high",
            "Head dropping"
        ),
        minConfidence = 0.7f
    )
    
    /**
     * Jumping Jacks biomechanics
     */
    val JUMPING_JACKS = ExerciseBiomechanics(
        name = "Jumping Jacks",
        properFormAngles = FormAngles(
            downPosition = AngleRange(
                min = 20.0,    // Arms down (angle between arm and body)
                max = 50.0,
                ideal = 30.0
            ),
            upPosition = AngleRange(
                min = 150.0,   // Arms up (angle between arm and body)
                max = 180.0,
                ideal = 170.0
            ),
            bodyAlignment = null  // Not critical for jumping jacks
        ),
        keyLandmarks = listOf(
            "LEFT_SHOULDER", "LEFT_ELBOW", "LEFT_WRIST",
            "RIGHT_SHOULDER", "RIGHT_ELBOW", "RIGHT_WRIST",
            "LEFT_HIP", "LEFT_ANKLE", "RIGHT_HIP", "RIGHT_ANKLE"
        ),
        commonMistakes = listOf(
            "Not raising arms fully overhead",
            "Not jumping wide enough",
            "Landing too hard"
        ),
        minConfidence = 0.5f  // Lower confidence for dynamic movement
    )
    
    fun getExerciseData(exerciseType: ExerciseType): ExerciseBiomechanics {
        return when (exerciseType) {
            ExerciseType.PUSH_UP -> PUSH_UP
            ExerciseType.SQUAT -> SQUAT
            ExerciseType.PLANK -> PLANK
            ExerciseType.JUMPING_JACKS -> JUMPING_JACKS
        }
    }
}
