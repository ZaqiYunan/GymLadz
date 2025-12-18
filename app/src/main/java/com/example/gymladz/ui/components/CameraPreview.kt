package com.example.gymladz.ui.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.gymladz.pose.PoseDetectorProcessor
import com.google.mlkit.vision.pose.Pose
import java.util.concurrent.Executors

/**
 * CameraX preview component for pose detection
 */
@Composable
fun CameraPreview(
    onPoseDetected: (Pose) -> Unit,
    onError: (Exception) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val poseDetectorProcessor = remember {
        PoseDetectorProcessor(
            onPoseDetected = onPoseDetected,
            onError = onError
        )
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            poseDetectorProcessor.close()
        }
    }
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                // Preview use case
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                // Image analysis use case for pose detection
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, poseDetectorProcessor)
                    }
                
                // Select front camera
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                
                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()
                    
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    onError(e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        }
    )
}
