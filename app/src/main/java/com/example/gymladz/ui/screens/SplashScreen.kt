package com.example.gymladz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gymladz.R
import com.example.gymladz.ui.theme.DarkTealBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 second delay
        onTimeout()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkTealBackground),
        contentAlignment = Alignment.Center
    ) {
        // You can replace this with your actual logo
        // For now, showing a placeholder
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "GymLadz Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}
