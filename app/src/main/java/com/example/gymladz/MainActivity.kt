package com.example.gymladz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymladz.data.Exercise
import com.example.gymladz.data.workout.WorkoutSession
import com.example.gymladz.ui.components.BottomNavBar
import com.example.gymladz.ui.screens.*
import com.example.gymladz.ui.theme.GymLadzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymLadzTheme {
                AppNavigation()
            }
        }
    }
}

// Navigation state
sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
    object Profile : Screen()
    object Camera : Screen()
    object Settings : Screen()
    data class ExerciseDetail(val exercise: Exercise) : Screen()
    data class ActiveWorkout(val exercise: Exercise, val goalReps: Int) : Screen()
}

@Composable
fun AppNavigation() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userRepository = remember { com.example.gymladz.data.user.UserRepository(context) }
    val isLoggedIn by userRepository.isLoggedIn.collectAsState(initial = false)
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    
    // Handle splash screen timeout
    LaunchedEffect(isLoggedIn) {
        if (currentScreen is Screen.Splash) {
            kotlinx.coroutines.delay(2000)
            currentScreen = if (isLoggedIn) Screen.Home else Screen.Login
        }
    }
    
    when (currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onTimeout = {
                    currentScreen = if (isLoggedIn) Screen.Home else Screen.Login
                }
            )
        }
        is Screen.Login -> {
            LoginScreen(
                onNavigateToRegister = { currentScreen = Screen.Register },
                onLoginSuccess = { currentScreen = Screen.Home }
            )
        }
        is Screen.Register -> {
            RegisterScreen(
                onBackToLogin = { currentScreen = Screen.Login },
                onRegisterSuccess = { currentScreen = Screen.Login }
            )
        }
        else -> {
            MainScreen(
                currentScreen = currentScreen,
                onScreenChange = { currentScreen = it },
                onLogout = { currentScreen = Screen.Login }
            )
        }
    }
}

@Composable
fun MainScreen(
    currentScreen: Screen = Screen.Home,
    onScreenChange: (Screen) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var localScreen by remember(currentScreen) { mutableStateOf(currentScreen) }
    
    // Sync local screen with external screen
    LaunchedEffect(currentScreen) {
        localScreen = currentScreen
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Display current screen
        when (val screen = localScreen) {
            is Screen.Home -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                ) {
                    ExerciseListScreen(
                        onExerciseClick = { exercise ->
                            localScreen = Screen.ExerciseDetail(exercise)
                            onScreenChange(Screen.ExerciseDetail(exercise))
                        }
                    )
                }
            }
            is Screen.Profile -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                ) {
                    ProfileScreen()
                }
            }
            is Screen.Camera -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                ) {
                    PoseDetectionScreen()
                }
            }
            is Screen.Settings -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
                ) {
                    SettingsScreen(onLogout = onLogout)
                }
            }
            is Screen.ExerciseDetail -> {
                ExerciseDetailScreen(
                    exercise = screen.exercise,
                    onStartWorkout = { exercise, goalReps ->
                        localScreen = Screen.ActiveWorkout(exercise, goalReps)
                        onScreenChange(Screen.ActiveWorkout(exercise, goalReps))
                    },
                    onBack = {
                        localScreen = Screen.Home
                        onScreenChange(Screen.Home)
                        selectedTab = 0
                    }
                )
            }
            is Screen.ActiveWorkout -> {
                ActiveWorkoutScreen(
                    exercise = screen.exercise,
                    goalReps = screen.goalReps,
                    onWorkoutComplete = { session ->
                        // Return to home after workout
                        localScreen = Screen.Home
                        onScreenChange(Screen.Home)
                        selectedTab = 0
                    },
                    onBack = {
                        localScreen = Screen.ExerciseDetail(screen.exercise)
                        onScreenChange(Screen.ExerciseDetail(screen.exercise))
                    }
                )
            }
            else -> {} // Splash, Login, Register handled in AppNavigation
        }
        
        // Bottom Navigation Bar (hide during workout)
        if (localScreen !is Screen.ActiveWorkout) {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    val newScreen = when (tab) {
                        0 -> Screen.Home
                        1 -> Screen.Profile
                        2 -> Screen.Camera
                        3 -> Screen.Settings
                        else -> Screen.Home
                    }
                    localScreen = newScreen
                    onScreenChange(newScreen)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}