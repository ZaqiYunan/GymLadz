package com.example.gymladz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymladz.data.Exercise
import com.example.gymladz.data.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

class ExerciseViewModel(
    private val repository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()
    
    init {
        loadExercises()
    }
    
    fun loadExercises() {
        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading
            
            val result = repository.getExercises()
            _uiState.value = result.fold(
                onSuccess = { exercises ->
                    if (exercises.isNotEmpty()) {
                        ExerciseUiState.Success(exercises)
                    } else {
                        ExerciseUiState.Error("No exercises found")
                    }
                },
                onFailure = { exception ->
                    ExerciseUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }
    
    fun getCategories(): List<String> = repository.getCategories()
}
