/*
NIM: 23523047
Nama: Muhammad Zaqi Yunan Rachmanda
*/
package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymladz.ui.components.ErrorView
import com.example.gymladz.ui.components.ExerciseCard
import com.example.gymladz.ui.components.LoadingIndicator
import com.example.gymladz.ui.theme.*
import com.example.gymladz.ui.viewmodel.ExerciseUiState
import com.example.gymladz.ui.viewmodel.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.gymladz.data.Exercise

@Composable
fun ExerciseListScreen(
    onExerciseClick: (Exercise) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: ExerciseViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = remember { viewModel.getCategories() }

    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkTealBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Bar with Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = getCurrentDate(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "GymLadz",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    
                    // Notification icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = SurfaceDark,
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔔",
                            fontSize = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Dynamic Greeting
                Text(
                    text = getGreeting(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = "Ready to crush your fitness goals?",
                    fontSize = 14.sp,
                    color = TextPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Category Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = category == selectedCategory,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }
            
            // Content based on UI State
            when (uiState) {
                is ExerciseUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                
                is ExerciseUiState.Error -> {
                    ErrorView(
                        message = (uiState as ExerciseUiState.Error).message,
                        onRetry = { viewModel.loadExercises() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                
                is ExerciseUiState.Success -> {
                    val exercises = (uiState as ExerciseUiState.Success).exercises
                    val filteredExercises = exercises.filter { exercise ->
                        (selectedCategory == "All" || exercise.category == selectedCategory) &&
                        (searchQuery.isEmpty() || exercise.name.contains(searchQuery, ignoreCase = true))
                    }
                    
                    // Exercise List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Today's Exercises",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(filteredExercises) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onClick = { onExerciseClick(exercise) }
                            )
                        }
                        
                        // Bottom padding for nav bar
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DarkTealSurface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔍",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (query.isEmpty()) "Search exercises..." else query,
            fontSize = 15.sp,
            color = if (query.isEmpty()) TextSecondary else TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) BrightCyan else DarkTealSurface,
        onClick = onClick
    ) {
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) DarkTealBackground else TextWhite,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

// Helper Functions
private fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning!"
        in 12..16 -> "Good Afternoon!"
        in 17..20 -> "Good Evening!"
        else -> "Good Night!"
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    return dateFormat.format(Date())
}
