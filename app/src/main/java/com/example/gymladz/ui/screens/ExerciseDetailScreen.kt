package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.gymladz.data.Exercise
import com.example.gymladz.data.workout.PersonalBest
import com.example.gymladz.data.workout.WorkoutRepository
import com.example.gymladz.data.workout.WorkoutSession
import com.example.gymladz.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    onStartWorkout: (Exercise, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val workoutRepository = remember { WorkoutRepository(context) }
    
    var selectedReps by remember { mutableStateOf(10) }
    var recentSessions by remember { mutableStateOf<List<WorkoutSession>>(emptyList()) }
    var personalBest by remember { mutableStateOf<PersonalBest?>(null) }
    
    val repOptions = listOf(5, 10, 15, 20, 25, 30)
    
    // Load exercise history
    LaunchedEffect(exercise.id) {
        workoutRepository.getExerciseSessions(exercise.id).collect { sessions ->
            recentSessions = sessions.take(5)
        }
        
        personalBest = workoutRepository.getPersonalBest(exercise.id, exercise.name).firstOrNull()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        fontSize = 28.sp,
                        color = TextPrimary,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(
                        text = "Exercise Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }
            
            // Exercise Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = exercise.emoji,
                            fontSize = 48.sp
                        )
                        Text(
                            text = exercise.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = exercise.category,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoBadge(
                                icon = "⏱️",
                                text = "${exercise.duration} min"
                            )
                            InfoBadge(
                                icon = "🔥",
                                text = "${exercise.calories} cal"
                            )
                            InfoBadge(
                                icon = "📊",
                                text = exercise.difficulty.name
                            )
                        }
                    }
                }
            }
            
            // Personal Best
            personalBest?.let { pb ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = OrangePeach.copy(alpha = 0.2f)
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
                                    text = "Personal Best 🏆",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${pb.maxReps} reps",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePeach
                                )
                            }
                            Text(
                                text = formatDate(pb.date),
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            
            // Rep Goal Selector
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Set Your Goal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repOptions.forEach { reps ->
                            RepOptionButton(
                                reps = reps,
                                isSelected = selectedReps == reps,
                                onClick = { selectedReps = reps },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            // Recent Sessions
            if (recentSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Workouts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(recentSessions) { session ->
                    SessionCard(session)
                }
            }
        }
        
        // Start Workout Button
        Button(
            onClick = { onStartWorkout(exercise, selectedReps) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 100.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePeach)
        ) {
            Text(
                text = "Start Workout ($selectedReps reps)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RepOptionButton(
    reps: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OrangePeach else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$reps",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

@Composable
fun InfoBadge(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 14.sp)
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SessionCard(session: WorkoutSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    text = "${session.completedReps}/${session.goalReps} reps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${session.caloriesBurned} cal • ${formatDuration(session.duration)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text(
                text = formatDate(session.timestamp),
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDuration(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) "${minutes}m ${secs}s" else "${secs}s"
}
