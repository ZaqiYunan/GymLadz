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
import com.example.gymladz.ui.components.BottomNavBar
import com.example.gymladz.ui.screens.CommunityScreen
import com.example.gymladz.ui.screens.ExerciseListScreen
import com.example.gymladz.ui.screens.ProfileScreen
import com.example.gymladz.ui.screens.SettingsScreen
import com.example.gymladz.ui.theme.GymLadzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymLadzTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Display different screens based on selected tab
        when (selectedTab) {
            0 -> ExerciseListScreen()
            1 -> ProfileScreen()
            2 -> SettingsScreen()
            3 -> CommunityScreen()
        }
        
        // Bottom Navigation Bar
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}