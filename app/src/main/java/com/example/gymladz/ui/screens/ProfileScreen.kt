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

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
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
                    .background(NavyBlue),
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
                text = System.getProperty("user.name")?.capitalize() ?: "User",
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
                    value = "42",
                    emoji = "🏋️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Calories",
                    value = "12.5k",
                    emoji = "🔥",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Streak",
                    value = "7 days",
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
                    containerColor = SurfaceWhite
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
                    
                    GoalItem(
                        title = "Weight Loss",
                        progress = 0.7f,
                        emoji = "🎯"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    GoalItem(
                        title = "Build Muscle",
                        progress = 0.5f,
                        emoji = "💪"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    GoalItem(
                        title = "Flexibility",
                        progress = 0.3f,
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
            containerColor = SurfaceWhite
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
                color = OrangePeach
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = OrangePeach,
            trackColor = BackgroundLight
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
