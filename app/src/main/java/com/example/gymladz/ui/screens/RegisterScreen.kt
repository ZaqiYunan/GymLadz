package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.data.user.UserRepository
import com.example.gymladz.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val scope = rememberCoroutineScope()
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Title
            Text(
                text = "🏋️",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Start your fitness journey today",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    error = null
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePeach,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = OrangePeach,
                    unfocusedLabelColor = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    error = null
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePeach,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = OrangePeach,
                    unfocusedLabelColor = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    error = null
                },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePeach,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = OrangePeach,
                    unfocusedLabelColor = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Error Message
            error?.let {
                Text(
                    text = it,
                    color = Color(0xFFE53935),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    when {
                        username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                            error = "Please fill all fields"
                        }
                        username.length < 3 -> {
                            error = "Username must be at least 3 characters"
                        }
                        password.length < 6 -> {
                            error = "Password must be at least 6 characters"
                        }
                        password != confirmPassword -> {
                            error = "Passwords do not match"
                        }
                        else -> {
                            isLoading = true
                            scope.launch {
                                val success = userRepository.register(username, password)
                                isLoading = false
                                if (success) {
                                    onRegisterSuccess()
                                } else {
                                    error = "Username already exists"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePeach),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "REGISTER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Login Link
            TextButton(onClick = onBackToLogin) {
                Text(
                    "Already have an account? Login",
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
