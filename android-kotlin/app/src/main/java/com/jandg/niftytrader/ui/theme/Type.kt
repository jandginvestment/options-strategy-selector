package com.jandg.niftytrader.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jandg.niftytrader.R

val SpaceMono = FontFamily(
    Font(R.font.space_mono_regular, FontWeight.Normal),
    Font(R.font.space_mono_bold,    FontWeight.Bold),
)

val Syne = FontFamily(
    Font(R.font.syne_regular,    FontWeight.Normal),
    Font(R.font.syne_semibold,   FontWeight.SemiBold),
    Font(R.font.syne_bold,       FontWeight.Bold),
    Font(R.font.syne_extrabold,  FontWeight.ExtraBold),
)

val NiftyTypography = Typography(
    // Large display numbers (Nifty price, VIX)
    displayLarge  = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Bold,    fontSize = 32.sp, color = TextPrimary),
    displayMedium = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Bold,    fontSize = 24.sp, color = TextPrimary),
    displaySmall  = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Normal,  fontSize = 18.sp, color = TextPrimary),

    // Section headings
    headlineLarge  = TextStyle(fontFamily = Syne, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextPrimary),
    headlineMedium = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Bold,      fontSize = 16.sp, color = TextPrimary),
    headlineSmall  = TextStyle(fontFamily = Syne, fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, color = TextPrimary),

    // Body text
    bodyLarge  = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextPrimary),
    bodyMedium = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = TextSecondary),
    bodySmall  = TextStyle(fontFamily = Syne, fontWeight = FontWeight.Normal, fontSize = 11.sp, color = TextMuted),

    // Labels (mono for data)
    labelLarge  = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = TextSecondary),
    labelMedium = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Normal, fontSize = 11.sp, color = TextMuted),
    labelSmall  = TextStyle(fontFamily = SpaceMono, fontWeight = FontWeight.Normal, fontSize = 10.sp, color = TextMuted),
)
