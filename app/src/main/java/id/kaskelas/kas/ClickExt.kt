package id.kaskelas.kas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

/** Klik tanpa ripple effect — dipakai tombol keypad. */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = null,
    onClick = onClick,
)

/** Format Long ke Rp dengan pemisah ribuan titik (Rp 1.250.000). */
internal fun formatRupiah(amount: Long): String {
    if (amount == Long.MIN_VALUE) return "-Rp 9.223.372.036.854.775.808"
    val negative = amount < 0
    val s = kotlin.math.abs(amount).toString()
    val sb = StringBuilder()
    s.reversed().forEachIndexed { index, ch ->
        if (index > 0 && index % 3 == 0) sb.append('.')
        sb.append(ch)
    }
    return (if (negative) "-" else "") + "Rp " + sb.reverse().toString()
}
