package com.example.eazyfind.ui.themes

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.eazyfind.R

/* ---------------- POPPINS FONT FAMILY ---------------- */

private val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

/* ---------------- APP TYPOGRAPHY ---------------- */

val AppTypography = Typography(

    // Restaurant Name
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 22.sp
    ),

    // SECTION HEADERS
    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp
    ),

    // SUB-SECTION HEADERS
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 8.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 12.sp
    ),

    // Body text
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    ),

    // Offer text
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp
    )
)
