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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.gymladz.data.Exercise
import com.example.gymladz.data.pose.ExerciseType
import com.example.gymladz.data.workout.WorkoutRepository
import com.example.gymladz.data.workout.WorkoutSession
import com.example.gymladz.pose.EnhancedExerciseCounter
import com.example.gymladz.ui.components.CameraPreview
import com.example.gymladz.ui.components.PoseOverlay
import com.example.gymladz.ui.theme.*
import com.example.gymladz.utils.CalorieCalculator
import com.google.mlkit.vision.pose.Pose
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ActiveWorkoutScreen(
    exercise: Exercise,
    goalReps: Int,
    onWorkoutComplete: (WorkoutSession) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val workoutRepository = remember { WorkoutRepository(context) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var isTracking by remember { mutableStateOf(false) }
    var currentPose by remember { mutableStateOf<Pose?>(null) }
    var feedbackMessage by remember { mutableStateOf("Get in position...") }
    var startTime by remember { mutableStateOf(0L) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    
    // Map exercise to pose detection type
    val exerciseType = remember(exercise.name) {
        when {
            exercise.name.contains("push", ignoreCase = true) -> ExerciseType.PUSH_UP
            exercise.name.contains("squat", ignoreCase = true) -> ExerciseType.SQUAT
            exercise.name.contains("plank", ignoreCase = true) -> ExerciseType.PLANK
            exercise.name.contains("jumping jack", ignoreCase = true) -> ExerciseType.JUMPING_JACKS
            else -> ExerciseType.PUSH_UP // Default
        }
    }
    
    val exerciseCounter = remember(exerciseType) {
        EnhancedExerciseCounter(exerciseType)
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
            
            // Check if goal reached
            if (exerciseCounter.getRepCount() >= goalReps) {
                delay(500) // Small delay before showing dialog
                isTracking = false
                showCompleteDialog = true
            }
        }
    }
    
    // Complete workout dialog
    if (showCompleteDialog) {
        val duration = System.currentTimeMillis() - startTime
        val completedReps = exerciseCounter.getRepCount()
        val calories = CalorieCalculator.calculateCalories(exercise.name, completedReps)
        
        val session = WorkoutSession(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            goalReps = goalReps,
            completedReps = completedReps,
            duration = duration,
            caloriesBurned = calories,
            formQuality = 0.85f
        )
        
        WorkoutCompleteDialog(
            exercise = exercise,
            completedReps = completedReps,
            goalReps = goalReps,
            duration = duration,
            calories = calories,
            onSave = {
                kotlinx.coroutines.GlobalScope.launch {
                    workoutRepository.saveWorkoutSession(session)
                }
                onWorkoutComplete(session)
            },
            onDismiss = { onBack() }
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!hasCameraPermission) {
            // Permission request
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📷",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Permission Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePeach)
                ) {
                    Text("Grant Permission")
                }
            }
        } else {
            // Camera preview
            if (isTracking) {
                CameraPreview(
                    onPoseDetected = { pose -> currentPose = pose },
                    onError = { feedbackMessage = "Camera error" },
                    modifier = Modifier.fillMaxSize()
                )
                
                PoseOverlay(
                    pose = currentPose,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Ready screen
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = exercise.emoji,
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ready to Start?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Goal: $goalReps ${exercise.name}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Top overlay - Exercise info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.TopCenter)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = exercise.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Goal: $goalReps reps",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        
                        if (isTracking) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(OrangePeach, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${exerciseCounter.getRepCount()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Bottom overlay - Controls and feedback
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress bar
                if (isTracking) {
                    val progress = exerciseCounter.getRepCount().toFloat() / goalReps.toFloat()
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = OrangePeach,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                    
                    // Feedback message
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
                
                // Start/Stop button
                Button(
                    onClick = {
                        if (!isTracking) {
                            startTime = System.currentTimeMillis()
                            exerciseCounter.resetCount()
                        }
                        isTracking = !isTracking
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
                        text = if (isTracking) "Stop Workout" else "Start Workout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Back button
                TextButton(onClick = onBack) {
                    Text(
                        text = "Cancel",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutCompleteDialog(
    exercise: Exercise,
    completedReps: Int,
    goalReps: Int,
    duration: Long,
    calories: Int,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Workout Complete!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Reps", "$completedReps/$goalReps")
                    StatItem("Time", formatDuration(duration))
                    StatItem("Calories", "$calories")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePeach)
            ) {
                Text("Save & Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Discard")
            }
        }
    )
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = OrangePeach
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val secs = seconds % 60
    return "${minutes}:${secs.toString().padStart(2, '0')}"
}
