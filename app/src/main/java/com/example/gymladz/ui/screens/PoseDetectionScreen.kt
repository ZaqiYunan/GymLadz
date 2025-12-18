package com.example.gymladz.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.gymladz.data.pose.ExerciseType
import com.example.gymladz.pose.EnhancedExerciseCounter
import com.example.gymladz.ui.components.CameraPreview
import com.example.gymladz.ui.components.PoseOverlay
import com.example.gymladz.ui.theme.*
import com.google.mlkit.vision.pose.Pose

@Composable
fun PoseDetectionScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var selectedExercise by remember { mutableStateOf(ExerciseType.PUSH_UP) }
    var isTracking by remember { mutableStateOf(false) }
    var currentPose by remember { mutableStateOf<Pose?>(null) }
    var feedbackMessage by remember { mutableStateOf("Select an exercise to start") }
    
    val exerciseCounter = remember(selectedExercise) {
        EnhancedExerciseCounter(selectedExercise)
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    // Process pose when tracking
    LaunchedEffect(currentPose, isTracking) {
        if (isTracking && currentPose != null) {
            exerciseCounter.processPose(currentPose!!)
            val feedback = exerciseCounter.getFormFeedback(currentPose!!)
            feedbackMessage = feedback.message
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OrangePeach,
                        OrangeLight,
                        BackgroundLight
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Exercise Tracker",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "ℹ️",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (!hasCameraPermission) {
                // Camera permission request
                CameraPermissionRequest(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            } else {
                // Exercise selection
                Text(
                    text = "Select Exercise",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExerciseTypeButton(
                        type = ExerciseType.PUSH_UP,
                        isSelected = selectedExercise == ExerciseType.PUSH_UP,
                        onClick = {
                            selectedExercise = ExerciseType.PUSH_UP
                            exerciseCounter.resetCount()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ExerciseTypeButton(
                        type = ExerciseType.SQUAT,
                        isSelected = selectedExercise == ExerciseType.SQUAT,
                        onClick = {
                            selectedExercise = ExerciseType.SQUAT
                            exerciseCounter.resetCount()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Camera preview with pose overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isTracking) {
                                // Live camera preview
                                CameraPreview(
                                    onPoseDetected = { pose ->
                                        currentPose = pose
                                    },
                                    onError = { exception ->
                                        feedbackMessage = "Camera error: ${exception.message}"
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                                
                                // Pose skeleton overlay
                                PoseOverlay(
                                    pose = currentPose,
                                    modifier = Modifier.fillMaxSize()
                                )
                                
                                // Rep counter overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(
                                                color = OrangePeach.copy(alpha = 0.9f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${exerciseCounter.getRepCount()}",
                                                fontSize = 40.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "REPS",
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                                
                                // Feedback message
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.Black.copy(alpha = 0.7f)
                                        )
                                    ) {
                                        Text(
                                            text = feedbackMessage,
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            } else {
                                // Not tracking - show instructions
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "📷",
                                        fontSize = 64.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Ready to Track",
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap Start to begin counting reps",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Start/Stop button
                Button(
                    onClick = {
                        isTracking = !isTracking
                        if (!isTracking) {
                            exerciseCounter.resetCount()
                            currentPose = null
                            feedbackMessage = "Workout complete! Great job! 🎉"
                        } else {
                            feedbackMessage = "Get in position..."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) Color(0xFFF44336) else OrangePeach
                    )
                ) {
                    Text(
                        text = if (isTracking) "Stop Tracking" else "Start Tracking",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CameraPermissionRequest(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📷",
                fontSize = 64.sp
            )
            Text(
                text = "Camera Permission Required",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "To track your exercises, we need access to your camera. Your privacy is important - no videos are stored.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePeach
                )
            ) {
                Text(
                    text = "Grant Permission",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ExerciseTypeButton(
    type: ExerciseType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (emoji, name) = when (type) {
        ExerciseType.PUSH_UP -> "💪" to "Push-ups"
        ExerciseType.SQUAT -> "🏋️" to "Squats"
        ExerciseType.PLANK -> "🧘" to "Plank"
        ExerciseType.JUMPING_JACKS -> "🎯" to "Jumping Jacks"
    }
    
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OrangePeach else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TextLight else TextPrimary
            )
        }
    }
}
