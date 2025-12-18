package com.example.gymladz.utils

import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Utility class for calculating angles between body joints
 */
object AngleCalculator {
    
    /**
     * Calculate angle between three points (in degrees)
     * @param firstPoint First landmark (e.g., shoulder)
     * @param midPoint Middle landmark (e.g., elbow)
     * @param lastPoint Last landmark (e.g., wrist)
     * @return Angle in degrees (0-180)
     */
    fun calculateAngle(
        firstPoint: PoseLandmark,
        midPoint: PoseLandmark,
        lastPoint: PoseLandmark
    ): Double {
        val radians = atan2(
            lastPoint.position.y - midPoint.position.y,
            lastPoint.position.x - midPoint.position.x
        ) - atan2(
            firstPoint.position.y - midPoint.position.y,
            firstPoint.position.x - midPoint.position.x
        )
        
        var angle = abs(radians * 180.0 / Math.PI)
        if (angle > 180.0) {
            angle = 360.0 - angle
        }
        return angle
    }
    
    /**
     * Calculate angle using coordinates directly
     */
    fun calculateAngle(
        firstX: Float, firstY: Float,
        midX: Float, midY: Float,
        lastX: Float, lastY: Float
    ): Double {
        val radians = atan2(lastY - midY, lastX - midX) - 
                     atan2(firstY - midY, firstX - midX)
        
        var angle = abs(radians * 180.0 / Math.PI)
        if (angle > 180.0) {
            angle = 360.0 - angle
        }
        return angle
    }
    
    /**
     * Check if landmark is visible and has good confidence
     */
    fun isLandmarkVisible(landmark: PoseLandmark?, minConfidence: Float = 0.5f): Boolean {
        return landmark != null && landmark.inFrameLikelihood >= minConfidence
    }
}
