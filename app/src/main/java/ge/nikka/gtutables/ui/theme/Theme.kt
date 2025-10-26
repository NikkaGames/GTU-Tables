package ge.nikka.gtutables.ui.theme

import ge.nikka.gtutables.*
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun GTUTablesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF121212),  // Dark primary color (e.g., dark gray)
            secondary = Color(0xFF1E1E1E),  // Dark secondary color
            background = Color.Black,  // Black background
            surface = Color.Black,  // Black surface (card, button background)
            onPrimary = Color.White,  // White text/icons on primary
            onSecondary = Color.White,  // White text/icons on secondary
            onBackground = Color.White,  // White text/icons on background
            onSurface = Color.White  // White text/icons on surface
        ),
        typography = MaterialTheme.typography.copy(
            bodyLarge = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    Font(R.font.googlereg, FontWeight.Normal)
                )
            )
        ),
        content = content
    )
}