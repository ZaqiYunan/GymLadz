package com.example.gymladz.pose

import com.example.gymladz.data.pose.*
import com.example.gymladz.utils.AngleCalculator
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs

/**
 * Enhanced exercise counter with improved accuracy using biomechanics data
 */
class EnhancedExerciseCounter(private val exerciseType: ExerciseType) {
    
    private var currentState = RepState.UP
    private var repCount = 0
    private var lastStateChangeTime = System.currentTimeMillis()
    private val biomechanics = ExerciseDatabase.getExerciseData(exerciseType)
    
    // Minimum time between reps to avoid double counting (REDUCED for faster response)
    private val minRepDuration = 400L  // Reduced from 600L
    
    // Confidence tracking (RELAXED for easier detection)
    private var consecutiveGoodFrames = 0
    private val minConsecutiveFrames = 1  // Reduced from 3 for immediate response
    
    fun getRepCount(): Int = repCount
    
    fun resetCount() {
        repCount = 0
        currentState = RepState.UP
        consecutiveGoodFrames = 0
    }
    
    /**
     * Process pose with enhanced accuracy
     */
    fun processPose(pose: Pose): RepState {
        val (newState, confidence) = when (exerciseType) {
            ExerciseType.PUSH_UP -> detectPushUpEnhanced(pose)
            ExerciseType.SQUAT -> detectSquatEnhanced(pose)
            ExerciseType.PLANK -> detectPlank(pose)
            ExerciseType.JUMPING_JACKS -> detectJumpingJacks(pose)
        }
        
        // RELAXED: Lower confidence threshold (0.4 instead of 0.65)
        if (confidence >= 0.4f) {
            consecutiveGoodFrames++
        } else {
            consecutiveGoodFrames = 0
            return RepState.TRANSITIONING
        }
        
        // Count rep when transitioning from DOWN to UP with good confidence
        if (currentState == RepState.DOWN && newState == RepState.UP) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastStateChangeTime >= minRepDuration && 
                consecutiveGoodFrames >= minConsecutiveFrames) {
                repCount++
                lastStateChangeTime = currentTime
                consecutiveGoodFrames = 0
            }
        }
        
        currentState = newState
        return newState
    }
    
    /**
     * Enhanced push-up detection with multiple validation points
     */
    private fun detectPushUpEnhanced(pose: Pose): Pair<RepState, Float> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        
        // RELAXED: Only check essential landmarks (removed hip/ankle requirement)
        val landmarks = listOf(
            leftShoulder, leftElbow, leftWrist,
            rightShoulder, rightElbow, rightWrist
        )
        
        // RELAXED: Lower visibility threshold (0.3 instead of 0.65)
        if (landmarks.any { !AngleCalculator.isLandmarkVisible(it, 0.3f) }) {
            return Pair(RepState.TRANSITIONING, 0.0f)
        }
        
        // Calculate elbow angles (both arms)
        val leftElbowAngle = AngleCalculator.calculateAngle(
            leftShoulder!!, leftElbow!!, leftWrist!!
        )
        val rightElbowAngle = AngleCalculator.calculateAngle(
            rightShoulder!!, rightElbow!!, rightWrist!!
        )
        
        // Average both arms for symmetry
        val avgElbowAngle = (leftElbowAngle + rightElbowAngle) / 2
        
        // SIMPLIFIED: Removed strict symmetry and alignment checks
        // Just use average landmark confidence
        val avgLandmarkConfidence = landmarks.mapNotNull { it?.inFrameLikelihood }
            .average().toFloat()
        
        // RELAXED: More lenient angle thresholds
        val state = when {
            avgElbowAngle <= 110 -> RepState.DOWN  // Relaxed from 100
            avgElbowAngle >= 150 -> RepState.UP    // Relaxed from 160
            else -> RepState.TRANSITIONING
        }
        
        return Pair(state, avgLandmarkConfidence)
    }
    
    /**
     * Enhanced squat detection with multiple validation points
     */
    private fun detectSquatEnhanced(pose: Pose): Pair<RepState, Float> {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        
        // RELAXED: Removed shoulder requirement
        val landmarks = listOf(
            leftHip, leftKnee, leftAnkle,
            rightHip, rightKnee, rightAnkle
        )
        
        // RELAXED: Lower visibility threshold
        if (landmarks.any { !AngleCalculator.isLandmarkVisible(it, 0.3f) }) {
            return Pair(RepState.TRANSITIONING, 0.0f)
        }
        
        // Calculate knee angles (both legs)
        val leftKneeAngle = AngleCalculator.calculateAngle(
            leftHip!!, leftKnee!!, leftAnkle!!
        )
        val rightKneeAngle = AngleCalculator.calculateAngle(
            rightHip!!, rightKnee!!, rightAnkle!!
        )
        
        // Average both legs for symmetry
        val avgKneeAngle = (leftKneeAngle + rightKneeAngle) / 2
        
        // SIMPLIFIED: Just use landmark confidence
        val avgLandmarkConfidence = landmarks.mapNotNull { it?.inFrameLikelihood }
            .average().toFloat()
        
        // RELAXED: More lenient angle thresholds
        val state = when {
            avgKneeAngle <= 120 -> RepState.DOWN  // Relaxed from 100
            avgKneeAngle >= 150 -> RepState.UP    // Relaxed from 160
            else -> RepState.TRANSITIONING
        }
        
        return Pair(state, avgLandmarkConfidence)
    }
    
    /**
     * Get detailed form feedback with accuracy metrics
     */
    fun getFormFeedback(pose: Pose): ExerciseFeedback {
        return when (exerciseType) {
            ExerciseType.PUSH_UP -> getPushUpFeedbackEnhanced(pose)
            ExerciseType.SQUAT -> getSquatFeedbackEnhanced(pose)
            ExerciseType.PLANK -> getPlankFeedback(pose)
            ExerciseType.JUMPING_JACKS -> getJumpingJacksFeedback(pose)
        }
    }
    
    /**
     * Plank detection - checks body alignment and hold time
     */
    private fun detectPlank(pose: Pose): Pair<RepState, Float> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        val landmarks = listOf(leftShoulder, leftHip, leftAnkle)
        
        if (landmarks.any { !AngleCalculator.isLandmarkVisible(it, 0.3f) }) {
            return Pair(RepState.TRANSITIONING, 0.0f)
        }
        
        // Check body alignment (should be straight line)
        val bodyAlignment = AngleCalculator.calculateAngle(
            leftShoulder!!, leftHip!!, leftAnkle!!
        )
        
        val avgLandmarkConfidence = landmarks.mapNotNull { it?.inFrameLikelihood }
            .average().toFloat()
        
        // For plank, we just check if they're holding proper form
        // Always return UP state (plank is a hold, not reps)
        val isGoodForm = bodyAlignment >= 165.0
        
        return Pair(RepState.UP, if (isGoodForm) avgLandmarkConfidence else 0.3f)
    }
    
    /**
     * Jumping Jacks detection - tracks arm position
     */
    private fun detectJumpingJacks(pose: Pose): Pair<RepState, Float> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        
        val landmarks = listOf(
            leftShoulder, leftElbow, leftWrist, leftHip,
            rightShoulder, rightWrist
        )
        
        if (landmarks.any { !AngleCalculator.isLandmarkVisible(it, 0.3f) }) {
            return Pair(RepState.TRANSITIONING, 0.0f)
        }
        
        // Calculate arm angle (shoulder-elbow-wrist)
        val leftArmAngle = AngleCalculator.calculateAngle(
            leftShoulder!!, leftElbow!!, leftWrist!!
        )
        
        // Also check wrist height relative to shoulder
        val leftWristY = leftWrist.position.y
        val leftShoulderY = leftShoulder.position.y
        val rightWristY = rightWrist!!.position.y
        val rightShoulderY = rightShoulder!!.position.y
        
        // Arms are UP when wrists are above shoulders
        val armsUp = (leftWristY < leftShoulderY) && (rightWristY < rightShoulderY)
        
        val avgLandmarkConfidence = landmarks.mapNotNull { it?.inFrameLikelihood }
            .average().toFloat()
        
        val state = if (armsUp) RepState.UP else RepState.DOWN
        
        return Pair(state, avgLandmarkConfidence)
    }
    
    private fun getPushUpFeedbackEnhanced(pose: Pose): ExerciseFeedback {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        
        if (leftShoulder != null && leftHip != null && leftAnkle != null &&
            leftElbow != null && leftWrist != null) {
            
            val bodyAngle = AngleCalculator.calculateAngle(leftShoulder, leftHip, leftAnkle)
            val elbowAngle = AngleCalculator.calculateAngle(leftShoulder, leftElbow, leftWrist)
            
            return when {
                bodyAngle < biomechanics.properFormAngles.bodyAlignment!!.min ->
                    ExerciseFeedback("Keep your back straight! 📏", false)
                currentState == RepState.DOWN && elbowAngle > biomechanics.properFormAngles.downPosition.max ->
                    ExerciseFeedback("Go lower! Aim for 90° 💪", false)
                bodyAngle >= biomechanics.properFormAngles.bodyAlignment.ideal ->
                    ExerciseFeedback("Perfect form! Keep it up! 🔥", true)
                else ->
                    ExerciseFeedback("Good! Stay controlled 💪", true)
            }
        }
        
        return ExerciseFeedback("Position yourself in frame", false)
    }
    
    private fun getSquatFeedbackEnhanced(pose: Pose): ExerciseFeedback {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        if (leftHip != null && leftKnee != null && leftAnkle != null) {
            val kneeAngle = AngleCalculator.calculateAngle(leftHip, leftKnee, leftAnkle)
            
            return when {
                currentState == RepState.DOWN && kneeAngle <= biomechanics.properFormAngles.downPosition.ideal ->
                    ExerciseFeedback("Perfect depth! 🎯", true)
                currentState == RepState.DOWN && kneeAngle > biomechanics.properFormAngles.downPosition.max ->
                    ExerciseFeedback("Go deeper! Aim for parallel 💪", false)
                kneeAngle >= biomechanics.properFormAngles.upPosition.ideal ->
                    ExerciseFeedback("Great form! Keep going! 🔥", true)
                else ->
                    ExerciseFeedback("Good! Stay controlled 💪", true)
            }
        }
        
        return ExerciseFeedback("Position yourself in frame", false)
    }
    
    private fun getPlankFeedback(pose: Pose): ExerciseFeedback {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        if (leftShoulder != null && leftHip != null && leftAnkle != null) {
            val bodyAlignment = AngleCalculator.calculateAngle(leftShoulder, leftHip, leftAnkle)
            
            return when {
                bodyAlignment < 165.0 -> ExerciseFeedback("Keep your body straight! 📏", false)
                bodyAlignment >= 175.0 -> ExerciseFeedback("Perfect plank! Hold it! 🔥", true)
                else -> ExerciseFeedback("Good! Keep breathing 💪", true)
            }
        }
        
        return ExerciseFeedback("Get into plank position", false)
    }
    
    private fun getJumpingJacksFeedback(pose: Pose): ExerciseFeedback {
        return when (currentState) {
            RepState.UP -> ExerciseFeedback("Arms up! Great! 🎯", true)
            RepState.DOWN -> ExerciseFeedback("Arms down! Keep going! 💪", true)
            else -> ExerciseFeedback("Keep jumping! 🔥", true)
        }
    }
}
