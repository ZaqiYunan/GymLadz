package com.example.gymladz.pose

import com.example.gymladz.data.pose.ExerciseFeedback
import com.example.gymladz.data.pose.ExerciseType
import com.example.gymladz.data.pose.RepState
import com.example.gymladz.utils.AngleCalculator
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Exercise counter that detects and counts reps for different exercises
 */
class ExerciseCounter(private val exerciseType: ExerciseType) {
    
    private var currentState = RepState.UP
    private var repCount = 0
    private var lastStateChangeTime = System.currentTimeMillis()
    
    // Minimum time between reps to avoid double counting (milliseconds)
    private val minRepDuration = 500L
    
    fun getRepCount(): Int = repCount
    
    fun resetCount() {
        repCount = 0
        currentState = RepState.UP
    }
    
    /**
     * Process pose and update rep count
     * @return Current rep state
     */
    fun processPose(pose: Pose): RepState {
        val newState = when (exerciseType) {
            ExerciseType.PUSH_UP -> detectPushUp(pose)
            ExerciseType.SQUAT -> detectSquat(pose)
            ExerciseType.PLANK -> RepState.UP // Plank doesn't have reps
            ExerciseType.JUMPING_JACKS -> RepState.UP // Default for jumping jacks
        }
        
        // Count rep when transitioning from DOWN to UP
        if (currentState == RepState.DOWN && newState == RepState.UP) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastStateChangeTime >= minRepDuration) {
                repCount++
                lastStateChangeTime = currentTime
            }
        }
        
        currentState = newState
        return newState
    }
    
    /**
     * Detect push-up position based on elbow angle
     */
    private fun detectPushUp(pose: Pose): RepState {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        
        // Check if all landmarks are visible
        if (!AngleCalculator.isLandmarkVisible(leftShoulder) ||
            !AngleCalculator.isLandmarkVisible(leftElbow) ||
            !AngleCalculator.isLandmarkVisible(leftWrist) ||
            !AngleCalculator.isLandmarkVisible(rightShoulder) ||
            !AngleCalculator.isLandmarkVisible(rightElbow) ||
            !AngleCalculator.isLandmarkVisible(rightWrist)) {
            return RepState.TRANSITIONING
        }
        
        // Calculate elbow angles
        val leftElbowAngle = AngleCalculator.calculateAngle(
            leftShoulder!!, leftElbow!!, leftWrist!!
        )
        val rightElbowAngle = AngleCalculator.calculateAngle(
            rightShoulder!!, rightElbow!!, rightWrist!!
        )
        
        // Average both arms
        val avgElbowAngle = (leftElbowAngle + rightElbowAngle) / 2
        
        return when {
            avgElbowAngle < 90 -> RepState.DOWN  // Arms bent (down position)
            avgElbowAngle > 160 -> RepState.UP   // Arms straight (up position)
            else -> RepState.TRANSITIONING
        }
    }
    
    /**
     * Detect squat position based on knee angle
     */
    private fun detectSquat(pose: Pose): RepState {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        
        // Check if all landmarks are visible
        if (!AngleCalculator.isLandmarkVisible(leftHip) ||
            !AngleCalculator.isLandmarkVisible(leftKnee) ||
            !AngleCalculator.isLandmarkVisible(leftAnkle) ||
            !AngleCalculator.isLandmarkVisible(rightHip) ||
            !AngleCalculator.isLandmarkVisible(rightKnee) ||
            !AngleCalculator.isLandmarkVisible(rightAnkle)) {
            return RepState.TRANSITIONING
        }
        
        // Calculate knee angles
        val leftKneeAngle = AngleCalculator.calculateAngle(
            leftHip!!, leftKnee!!, leftAnkle!!
        )
        val rightKneeAngle = AngleCalculator.calculateAngle(
            rightHip!!, rightKnee!!, rightAnkle!!
        )
        
        // Average both legs
        val avgKneeAngle = (leftKneeAngle + rightKneeAngle) / 2
        
        return when {
            avgKneeAngle < 100 -> RepState.DOWN  // Knees bent (down position)
            avgKneeAngle > 160 -> RepState.UP    // Legs straight (up position)
            else -> RepState.TRANSITIONING
        }
    }
    
    /**
     * Get form feedback based on current pose
     */
    fun getFormFeedback(pose: Pose): ExerciseFeedback {
        return when (exerciseType) {
            ExerciseType.PUSH_UP -> getPushUpFeedback(pose)
            ExerciseType.SQUAT -> getSquatFeedback(pose)
            ExerciseType.PLANK -> getPlankFeedback(pose)
            ExerciseType.JUMPING_JACKS -> ExerciseFeedback("Keep jumping! 🎯", true)
        }
    }
    
    private fun getPushUpFeedback(pose: Pose): ExerciseFeedback {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        if (leftShoulder != null && leftHip != null && leftAnkle != null) {
            // Check if body is straight (good form)
            val bodyAngle = AngleCalculator.calculateAngle(
                leftShoulder, leftHip, leftAnkle
            )
            
            return if (bodyAngle > 160) {
                ExerciseFeedback("Great form! Keep your body straight 💪", true)
            } else {
                ExerciseFeedback("Keep your back straight", false)
            }
        }
        
        return ExerciseFeedback("Position yourself in frame", false)
    }
    
    private fun getSquatFeedback(pose: Pose): ExerciseFeedback {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        
        if (leftHip != null && leftKnee != null && leftAnkle != null) {
            val kneeAngle = AngleCalculator.calculateAngle(
                leftHip, leftKnee, leftAnkle
            )
            
            return when {
                currentState == RepState.DOWN && kneeAngle < 90 -> 
                    ExerciseFeedback("Perfect depth! 🎯", true)
                currentState == RepState.DOWN && kneeAngle > 100 -> 
                    ExerciseFeedback("Go deeper", false)
                else -> 
                    ExerciseFeedback("Good form! Keep going 💪", true)
            }
        }
        
        return ExerciseFeedback("Position yourself in frame", false)
    }
    
    private fun getPlankFeedback(pose: Pose): ExerciseFeedback {
        return ExerciseFeedback("Hold steady! 💪", true)
    }
}
