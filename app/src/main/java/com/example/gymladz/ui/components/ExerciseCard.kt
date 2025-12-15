package com.example.gymladz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.data.Difficulty
import com.example.gymladz.data.Exercise
import com.example.gymladz.ui.theme.*

@Composable
fun ExerciseCard(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Icon with gradient background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                when (exercise.category) {
                                    "Cardio" -> TealAccent
                                    "Strength" -> CoralAccent
                                    "Flexibility" -> PurpleAccent
                                    else -> OrangePeach
                                },
                                when (exercise.category) {
                                    "Cardio" -> TealAccent.copy(alpha = 0.7f)
                                    "Strength" -> CoralAccent.copy(alpha = 0.7f)
                                    "Flexibility" -> PurpleAccent.copy(alpha = 0.7f)
                                    else -> OrangeLight
                                }
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.emoji,
                    fontSize = 36.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Exercise Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = exercise.name,
                    fontSize = 18.sp,
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
                    InfoChip(
                        icon = "⏱️",
                        text = "${exercise.duration} min"
                    )
                    InfoChip(
                        icon = "🔥",
                        text = "${exercise.calories} cal"
                    )
                }
            }
            
            // Difficulty Badge
            DifficultyBadge(difficulty = exercise.difficulty)
        }
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
