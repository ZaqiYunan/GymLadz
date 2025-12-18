package com.example.gymladz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.data.Difficulty
import com.example.gymladz.data.Exercise
import com.example.gymladz.ui.theme.*
import kotlin.math.min

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Fitbit-style color based on category
    val accentColor = when (exercise.category) {
        "Cardio" -> Color(0xFF00D4AA)      // Fitbit Teal
        "Strength" -> Color(0xFFFF6B6B)    // Fitbit Coral
        "Flexibility" -> Color(0xFF9B59B6) // Fitbit Purple
        else -> Color(0xFFFFB347)          // Fitbit Orange
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Circular progress with emoji
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular progress ring (Fitbit style)
                CircularProgressIndicator(
                    progress = min(exercise.duration / 60f, 1f),
                    color = accentColor,
                    modifier = Modifier.size(100.dp)
                )
                
                // Center emoji icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = exercise.emoji,
                        fontSize = 32.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // Right side: Exercise info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Exercise name
                Text(
                    text = exercise.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2
                )
                
                // Category badge
                Box(
                    modifier = Modifier
                        .background(
                            color = accentColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = exercise.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }
                
                // Stats row (Fitbit style)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Duration stat
                    FitbitStat(
                        value = "${exercise.duration}",
                        unit = "MIN",
                        color = accentColor
                    )
                    
                    // Calories stat
                    FitbitStat(
                        value = "${exercise.calories}",
                        unit = "CAL",
                        color = accentColor
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 8f
) {
    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val radius = diameter / 2
        val arcSize = diameter - strokeWidth
        
        // Background circle (light gray)
        drawCircle(
            color = Color(0xFFF0F0F0),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )
        
        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            ),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            topLeft = androidx.compose.ui.geometry.Offset(
                strokeWidth / 2,
                strokeWidth / 2
            )
        )
    }
}

@Composable
fun FitbitStat(
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = unit,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF999999),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun InfoChip(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 12.sp
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (difficulty) {
        Difficulty.BEGINNER -> Pair(Color(0xFF4CAF50), "Easy")
        Difficulty.INTERMEDIATE -> Pair(Color(0xFFFF9800), "Med")
        Difficulty.ADVANCED -> Pair(Color(0xFFF44336), "Hard")
    }
    
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
