package com.example.gymladz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymladz.ui.theme.BlackSecondary
import com.example.gymladz.ui.theme.GreenPrimary
import com.example.gymladz.ui.theme.TextPrimary

@Composable
fun BottomNavBar(
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Bottom Navigation Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(35.dp)
                )
                .background(
                    color = BlackSecondary,
                    shape = RoundedCornerShape(35.dp)
                )
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = "🏠",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            NavItem(
                icon = "👤",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            NavItem(
                icon = "📷",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            NavItem(
                icon = "⚙️",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            color = if (isSelected) GreenPrimary else TextPrimary.copy(alpha = 0.5f)
        )
    }
}
