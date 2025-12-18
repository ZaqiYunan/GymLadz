package com.example.gymladz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Overlay that draws skeleton on top of camera preview
 */
@Composable
fun PoseOverlay(
    pose: Pose?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (pose == null) return@Canvas
        
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) return@Canvas
        
        // Draw landmarks as circles
        landmarks.forEach { landmark ->
            if (landmark.inFrameLikelihood > 0.5f) {
                drawCircle(
                    color = Color.Green,
                    radius = 8f,
                    center = Offset(landmark.position.x, landmark.position.y)
                )
            }
        }
        
        // Draw connections between landmarks
        drawPoseConnections(pose)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPoseConnections(pose: Pose) {
    // Define connections between landmarks
    val connections = listOf(
        // Face
        Pair(PoseLandmark.NOSE, PoseLandmark.LEFT_EYE_INNER),
        Pair(PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE),
        Pair(PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER),
        Pair(PoseLandmark.LEFT_EYE_OUTER, PoseLandmark.LEFT_EAR),
        Pair(PoseLandmark.NOSE, PoseLandmark.RIGHT_EYE_INNER),
        Pair(PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE),
        Pair(PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER),
        Pair(PoseLandmark.RIGHT_EYE_OUTER, PoseLandmark.RIGHT_EAR),
        
        // Upper body
        Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
        Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW),
        Pair(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST),
        Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW),
        Pair(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST),
        
        // Torso
        Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
        Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
        Pair(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP),
        
        // Lower body
        Pair(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
        Pair(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
        Pair(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
        Pair(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
    )
    
    connections.forEach { (start, end) ->
        val startLandmark = pose.getPoseLandmark(start)
        val endLandmark = pose.getPoseLandmark(end)
        
        if (startLandmark != null && endLandmark != null &&
            startLandmark.inFrameLikelihood > 0.5f && endLandmark.inFrameLikelihood > 0.5f) {
            drawLine(
                color = Color.Yellow,
                start = Offset(startLandmark.position.x, startLandmark.position.y),
                end = Offset(endLandmark.position.x, endLandmark.position.y),
                strokeWidth = 4f
            )
        }
    }
}
