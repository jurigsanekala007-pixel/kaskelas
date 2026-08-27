package id.kaskelas.kas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// App selalu terang sesuai keputusan desain.
private val LightScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = BoneWhite,
    secondary = DeepBlue,
    onSecondary = BoneWhite,
    tertiary = AmberGold,
    background = BoneWhite,
    onBackground = MidnightNavy,
    surface = CloudGray,
    onSurface = MidnightNavy,
    error = CoralRed,
    onError = BoneWhite,
)

object KasSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}

private val KasTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    bodySmall = TextStyle(fontSize = 12.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

@Composable
fun KasKelasTheme(content: @Composable () -> Unit) {
    // Tema terang permanen; isSystemInDarkTheme sengaja diabaikan.
    MaterialTheme(
        colorScheme = LightScheme,
        typography = KasTypography,
        content = content,
    )
}
