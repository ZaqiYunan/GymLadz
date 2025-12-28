package com.example.gymladz.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Teal/Cyan Theme - GymLadz Modern Dark Mode

// Background Colors
val DarkTealBackground = Color(0xFF0D2726)      // Main background
val DarkTealSurface = Color(0xFF1A3635)         // Cards, elevated surfaces
val DarkTealElevated = Color(0xFF234140)        // Hover states, elevated cards

// Accent Colors
val BrightCyan = Color(0xFF00E5CC)              // Primary actions, highlights
val Turquoise = Color(0xFF1DE9B6)               // Secondary accent
val DarkCyan = Color(0xFF004D47)                // Borders, dividers
val CyanGlow = Color(0x3300E5CC)                // Glowing effects (20% opacity)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)               // Primary text
val TextGray = Color(0xFF8B9E9D)                // Secondary text
val TextMuted = Color(0xFF5A6B6A)               // Tertiary text

// Semantic Colors
val SuccessGreen = Color(0xFF4CAF50)            // Success states
val WarningOrange = Color(0xFFFF9800)           // Warnings
val ErrorRed = Color(0xFFE53935)                // Errors

// Legacy compatibility (for gradual migration from orange/peach theme)
val OrangePeach = BrightCyan
val OrangeLight = Turquoise
val OrangeDark = DarkCyan
val BackgroundLight = DarkTealBackground
val SurfaceWhite = DarkTealSurface
val SurfaceLight = DarkTealElevated
val TextPrimary = TextWhite
val TextSecondary = TextGray
val TextLight = TextWhite

// Legacy compatibility (for gradual migration from green/black theme)
val GreenPrimary = BrightCyan
val GreenLight = Turquoise
val GreenDark = DarkCyan
val GreenAccent = BrightCyan
val LimeAccent = Turquoise
val MintAccent = BrightCyan
val BlackPrimary = DarkTealBackground
val BlackSecondary = DarkTealSurface
val BlackLight = DarkTealElevated
val BackgroundDark = DarkTealBackground
val SurfaceDark = DarkTealSurface