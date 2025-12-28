package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.gymladz.data.user.UserRepository
import com.example.gymladz.data.workout.WorkoutRepository
import com.example.gymladz.data.workout.WorkoutSession

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userRepository = remember { com.example.gymladz.data.user.UserRepository(context) }
    val workoutRepository = remember { com.example.gymladz.data.workout.WorkoutRepository(context) }
    
    // Get current username
    val currentUsername by userRepository.currentUsername.collectAsState(initial = null)
    
    // Get workout sessions
    val workoutSessions by workoutRepository.getWorkoutSessions().collectAsState(initial = emptyList())
    
    // Calculate stats
    val totalWorkouts = workoutSessions.size
    val totalCalories = workoutSessions.sumOf { it.caloriesBurned }
    val workoutStreak = calculateStreak(workoutSessions)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkTealBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = getCurrentProfileDate(),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "✏️",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkTealSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 60.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = currentUsername?.capitalize() ?: "User",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Text(
                text = "Member since ${getCurrentYear()}",
                fontSize = 16.sp,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Workouts",
                    value = totalWorkouts.toString(),
                    emoji = "🏋️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Calories",
                    value = formatCalories(totalCalories),
                    emoji = "🔥",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Streak",
                    value = "$workoutStreak days",
                    emoji = "⚡",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Goals Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkTealSurface
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Your Goals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Calculate progress based on actual data
                    val weightLossProgress = calculateWeightLossProgress(totalWorkouts)
                    val muscleBuildProgress = calculateMuscleBuildProgress(totalCalories)
                    val flexibilityProgress = calculateFlexibilityProgress(workoutSessions)
                    
                    GoalItem(
                        title = "Weight Loss",
                        progress = weightLossProgress,
                        emoji = "🎯"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    GoalItem(
                        title = "Build Muscle",
                        progress = muscleBuildProgress,
                        emoji = "💪"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    GoalItem(
                        title = "Flexibility",
                        progress = flexibilityProgress,
                        emoji = "🧘"
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun GoalItem(
    title: String,
    progress: Float,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 20.sp)
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrightCyan
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = BrightCyan,
            trackColor = DarkTealElevated
        )
    }
}

// Helper Functions
private fun getCurrentProfileDate(): String {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

private fun getCurrentYear(): String {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.YEAR).toString()
}

// Calculate workout streak (consecutive days with workouts)
private fun calculateStreak(sessions: List<WorkoutSession>): Int {
    if (sessions.isEmpty()) return 0
    
    val calendar = Calendar.getInstance()
    val today = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    // Group sessions by date
    val sessionsByDate = sessions
        .groupBy { session ->
            val sessionCal = Calendar.getInstance().apply {
                timeInMillis = session.timestamp
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            sessionCal
        }
        .keys
        .sortedDescending()
    
    if (sessionsByDate.isEmpty()) return 0
    
    // Check if there's a workout today or yesterday
    val oneDayMs = 24 * 60 * 60 * 1000L
    val mostRecentWorkout = sessionsByDate.first()
    
    if (mostRecentWorkout > today + oneDayMs) {
        // Workout is in future, doesn't count
        return 0
    }
    
    if (mostRecentWorkout < today - oneDayMs) {
        // Last workout was more than 1 day ago, streak broken
        return 0
    }
    
    // Count consecutive days
    var streak = 1
    var currentDate = sessionsByDate.first()
    
    for (i in 1 until sessionsByDate.size) {
        val prevDate = sessionsByDate[i]
        val daysDiff = (currentDate - prevDate) / oneDayMs
        
        if (daysDiff == 1L) {
            streak++
            currentDate = prevDate
        } else {
            break
        }
    }
    
    return streak
}

// Format calories (e.g., 12500 -> "12.5k")
private fun formatCalories(calories: Int): String {
    return when {
        calories >= 1000 -> {
            val k = calories / 1000.0
            if (k % 1.0 == 0.0) "${k.toInt()}k"
            else String.format("%.1fk", k)
        }
        else -> calories.toString()
    }
}

// Calculate weight loss progress (based on workouts completed)
// Target: 30 workouts for full progress
private fun calculateWeightLossProgress(totalWorkouts: Int): Float {
    val target = 30
    return (totalWorkouts.toFloat() / target).coerceIn(0f, 1f)
}

// Calculate muscle build progress (based on calories burned)
// Target: 10,000 calories for full progress
private fun calculateMuscleBuildProgress(totalCalories: Int): Float {
    val target = 10000
    return (totalCalories.toFloat() / target).coerceIn(0f, 1f)
}

// Calculate flexibility progress (based on variety of exercises)
private fun calculateFlexibilityProgress(sessions: List<WorkoutSession>): Float {
    if (sessions.isEmpty()) return 0f
    
    // Count unique exercises
    val uniqueExercises = sessions.map { it.exerciseName }.distinct().size
    
    // Target: 10 different exercises for full progress
    val target = 10
    return (uniqueExercises.toFloat() / target).coerceIn(0f, 1f)
}
