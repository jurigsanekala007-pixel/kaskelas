package id.kaskelas.kas.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasSpacing
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: (Long) -> Unit,
    onEdit: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onEdit(transaction.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KasSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Tanggal
            Column(
                modifier = Modifier.width(80.dp),
            ) {
                Text(
                    text = transaction.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
            Spacer(modifier = Modifier.width(KasSpacing.sm))

            // Kategori + keterangan
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = KasSpacing.xs),
                    )
                }
            }

            Spacer(modifier = Modifier.width(KasSpacing.md))

            // Nominal + tombol hapus
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatAmount(transaction.type, transaction.amount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.type == TransactionType.MASUK)
                            ForestGreen
                        else
                            CoralRed,
                    ),
                )
                Spacer(modifier = Modifier.width(KasSpacing.sm))
                IconButton(
                    onClick = { onDelete(transaction.id) },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

private fun formatAmount(type: TransactionType, amount: Long): String {
    val prefix = if (type == TransactionType.MASUK) "+" else "−"
    return "$prefix Rp ${amount.toRupiahString()}"
}

private fun Long.toRupiahString(): String {
    val s = toString()
    val buffer = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        if (count > 0 && count % 3 == 0) buffer.append('.')
        buffer.append(s[i])
        count++
    }
    return buffer.reverse().toString()
}
