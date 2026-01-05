package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import com.example.gymladz.data.user.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val scope = rememberCoroutineScope()
    val currentUsername by userRepository.currentUsername.collectAsState(initial = null)
    
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkTealBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
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
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // User Info
            currentUsername?.let { username ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "👤", fontSize = 40.sp)
                        Column {
                            Text(
                                text = "Logged in as",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = username,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Settings Sections
            SettingsSection(title = "Preferences") {
                SettingItem(
                    icon = "🔔",
                    title = "Notifications",
                    subtitle = "Get workout reminders",
                    trailing = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GreenPrimary,
                                checkedTrackColor = GreenPrimary.copy(alpha = 0.5f)
                            )
                        )
                    }
                )
                
                SettingItem(
                    icon = "🌙",
                    title = "Dark Mode",
                    subtitle = "Switch to dark theme",
                    trailing = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GreenPrimary,
                                checkedTrackColor = GreenPrimary.copy(alpha = 0.5f)
                            )
                        )
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Account") {
                SettingItem(
                    icon = "👤",
                    title = "Edit Profile",
                    subtitle = "Update your information"
                )
                
                SettingItem(
                    icon = "🔒",
                    title = "Privacy",
                    subtitle = "Manage your privacy settings"
                )
                
                SettingItem(
                    icon = "🔐",
                    title = "Security",
                    subtitle = "Password and authentication"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(title = "Support") {
                SettingItem(
                    icon = "❓",
                    title = "Help Center",
                    subtitle = "Get help and support"
                )
                
                SettingItem(
                    icon = "📧",
                    title = "Contact Us",
                    subtitle = "Send us feedback"
                )
                
                SettingItem(
                    icon = "ℹ️",
                    title = "About",
                    subtitle = "Version 1.0.0"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logout Button
            Button(
                onClick = {
                    scope.launch {
                        userRepository.logout()
                        onLogout()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text(
                    text = "LOGOUT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceDark
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: String,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else {
            Text(
                text = "›",
                fontSize = 24.sp,
                color = TextSecondary
            )
        }
    }
}
