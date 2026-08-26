package id.kaskelas.kas.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.kaskelas.kas.ui.theme.KasKelasTheme

/** Baris 4 titik indikator PIN yang terisi. */
@Composable
fun PinDots(filled: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) { index ->
            val color = if (index < filled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(14.dp)
                    .background(color, CircleShape),
            )
        }
    }
}

@Preview(name = "PIN Dots - 2 filled", showBackground = true)
@Composable
private fun PinDotsPreview() {
    KasKelasTheme {
        PinDots(filled = 2)
    }
}

@Preview(name = "PIN Dots - 4 filled", showBackground = true)
@Composable
private fun PinDotsFullPreview() {
    KasKelasTheme {
        PinDots(filled = 4)
    }
}
