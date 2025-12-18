package com.example.gymladz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.ui.theme.*

data class ChatMessage(
    val id: Int,
    val sender: String,
    val message: String,
    val time: String,
    val isCurrentUser: Boolean = false
)

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier
) {
    val messages = remember {
        listOf(
            ChatMessage(1, "Mike", "Just crushed my morning workout! 💪", "8:45 AM"),
            ChatMessage(2, "Sarah", "Nice! What did you do?", "8:47 AM"),
            ChatMessage(3, "Mike", "5K run + strength training", "8:48 AM"),
            ChatMessage(4, "You", "That's awesome! Keep it up 🔥", "8:50 AM", true),
            ChatMessage(5, "John", "Anyone hitting the gym today?", "9:15 AM"),
            ChatMessage(6, "Emma", "I'm planning to go at 6 PM", "9:17 AM"),
            ChatMessage(7, "You", "Count me in! See you there 👍", "9:20 AM", true),
            ChatMessage(8, "Sarah", "Don't forget to hydrate everyone! 💧", "9:25 AM")
        )
    }
    
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
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
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
                        text = "Fitness Community",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "➕",
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Today's Discussion",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
            
            // Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
                
                // Bottom padding for input
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Message Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "💬",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Type a message...",
                        fontSize = 15.sp,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePeach),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "➤",
                            fontSize = 20.sp,
                            color = TextLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isCurrentUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NavyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.sender.first().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (!message.isCurrentUser) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }
            
            Card(
                shape = RoundedCornerShape(
                    topStart = if (message.isCurrentUser) 16.dp else 4.dp,
                    topEnd = if (message.isCurrentUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isCurrentUser) OrangePeach else SurfaceWhite
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text = message.message,
                    fontSize = 14.sp,
                    color = if (message.isCurrentUser) TextLight else TextPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Text(
                text = message.time,
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
