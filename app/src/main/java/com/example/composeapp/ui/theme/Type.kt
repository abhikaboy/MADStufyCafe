package com.example.composeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.composeapp.R


val LoraFont = FontFamily(Font(R.font.lora))
val DmSansFont = FontFamily(Font(R.font.dm_sans))

val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontFamily = LoraFont,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontFamily = LoraFont,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        lineHeight = 40.sp
    ),

    titleLarge  = TextStyle(
        fontSize = 20.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.W800,
        color = TextPrimary,
        lineHeight = 24.sp
    ),

    titleMedium  = TextStyle(
        fontSize = 14.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.W600,
        color = TextPrimary,
        lineHeight = 24.sp
    ),

    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Medium,
        color = TextPrimary,
        lineHeight = 20.sp
    ),

    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.W300,
        color = TextSecondary,
        lineHeight = 20.sp
    ),

    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Light,
        color = TextSecondary,
        lineHeight = 16.sp
    ),

    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Light,
        color = TextSecondary,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontFamily = DmSansFont,
        fontWeight = FontWeight.Medium,
        color = Color.White,
        lineHeight = 20.sp
    )
)