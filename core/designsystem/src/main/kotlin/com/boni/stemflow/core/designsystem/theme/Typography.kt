package com.boni.stemflow.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Stemflow = FontFamily.SansSerif

val StemflowTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 19.44.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.2.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.8.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Stemflow,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
    ),
)
