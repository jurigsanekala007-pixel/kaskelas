package id.kaskelas.kas.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.kaskelas.kas.clickableNoRipple
import id.kaskelas.kas.ui.theme.KasKelasTheme
import androidx.compose.ui.tooling.preview.Preview

/** Keypad 3x4 ala ATM: angka besar, mudah disentuh jempol. */
@Preview(name = "PIN Keypad", showBackground = true)
@Composable
fun PinKeypad(
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf(BLANK, '0', BACKSPACE),
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { key ->
                    when (key) {
                        BACKSPACE -> KeypadButton("⌫", 18.sp, onClick = onDelete)
                        BLANK -> KeypadButton("", 18.sp, onClick = {})
                        else -> KeypadButton(key.toString(), 26.sp, onClick = { onDigit(key) })
                    }
                }
            }
        }
    }
}

private const val BACKSPACE = '<'
private const val BLANK = '_'

@Composable
private fun KeypadButton(label: String, fontSize: TextUnit, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (label.isNotEmpty()) {
            Text(text = label, fontSize = fontSize, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(name = "PIN Keypad (themed)", showBackground = true)
@Composable
private fun PinKeypadPreview() {
    KasKelasTheme {
        PinKeypad(onDigit = {}, onDelete = {})
    }
}
