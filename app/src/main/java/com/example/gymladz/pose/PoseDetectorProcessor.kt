package com.example.gymladz.pose

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

/**
 * Processes camera frames and detects poses using ML Kit
 */
class PoseDetectorProcessor(
    private val onPoseDetected: (Pose) -> Unit,
    private val onError: (Exception) -> Unit
) : ImageAnalysis.Analyzer {
    
    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()
    
    private val poseDetector: PoseDetector = PoseDetection.getClient(options)
    
    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            
            poseDetector.process(image)
                .addOnSuccessListener { pose ->
                    onPoseDetected(pose)
                }
                .addOnFailureListener { exception ->
                    Log.e("PoseDetector", "Pose detection failed", exception)
                    onError(exception)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    
    fun close() {
        poseDetector.close()
    }
}
