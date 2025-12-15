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

@Composable
fun ExerciseListScreen(
    modifier: Modifier = Modifier,
    viewModel: ExerciseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = remember { viewModel.getCategories() }

    
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Top Bar
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
                        text = "☰",
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Greeting
                Text(
                    text = "GOOD MORNING!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = "Rachel",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
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
                            ExerciseCard(exercise = exercise)
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
                color = SurfaceWhite,
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
            text = if (query.isEmpty()) "What do you want to do?" else query,
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
        color = if (isSelected) NavyBlue else SurfaceWhite,
        onClick = onClick
    ) {
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) TextLight else TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}
