package com.danlanur.salesvision.ui.theme

import androidx.compose.material3.Typography
import com.danlanur.salesvision.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

val QuickSand = FontFamily(
    Font(R.font.quicksand_regular),
    Font(R.font.quicksand_regular, FontWeight.Bold),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Bold
    ),

    labelSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Bold
    ),
)