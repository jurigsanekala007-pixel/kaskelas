package id.kaskelas.kas.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.formatRupiah
import id.kaskelas.kas.ui.theme.BoneWhite
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasSpacing
import id.kaskelas.kas.ui.theme.MidnightNavy
import java.time.format.DateTimeFormatter

private val fullDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

/** Warna teks di atas kartu navy. */
private val BoneWhiteOnNavy = BoneWhite

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KasSpacing.md),
    ) {
        Text(
            text = "Beranda",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(KasSpacing.lg))

        when {
            state.isLoading -> {
                AnimatedVisibility(visible = state.isLoading, enter = fadeIn(), exit = fadeOut()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            else -> {
                // Kartu saldo utama (navy, PRD §11.2)
                BalanceCard(balance = state.balance)
                Spacer(modifier = Modifier.height(KasSpacing.md))

                // Total masuk/keluar bulan berjalan
                MonthSummaryRow(
                    monthLabel = DashboardViewModel.currentMonthLabel(),
                    totalMasuk = state.monthTotalMasuk,
                    totalKeluar = state.monthTotalKeluar,
                )
                Spacer(modifier = Modifier.height(KasSpacing.lg))

                // Transaksi terakhir
                LastTransactionCard(transaction = state.lastTransaction)
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightNavy),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KasSpacing.lg),
        ) {
            Text(
                text = "Saldo Saat Ini",
                style = MaterialTheme.typography.bodyMedium,
                color = BoneWhiteOnNavy,
            )
            Spacer(modifier = Modifier.height(KasSpacing.xs))
            Text(
                text = formatRupiah(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (balance < 0) CoralRed else MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun MonthSummaryRow(
    monthLabel: String,
    totalMasuk: Long,
    totalKeluar: Long,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.md)) {
        SummaryCard(
            title = "Masuk — $monthLabel",
            amount = totalMasuk,
            color = ForestGreen,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            title = "Keluar — $monthLabel",
            amount = totalKeluar,
            color = CoralRed,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Long,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KasSpacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(KasSpacing.xs))
            Text(
                text = formatRupiah(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
private fun LastTransactionCard(transaction: id.kaskelas.kas.domain.model.Transaction?) {
    Column {
        Text(
            text = "Transaksi Terakhir",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(KasSpacing.sm))
        if (transaction == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Text(
                    text = "Belum ada transaksi.\nTekan tab Transaksi untuk mulai mencatat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(KasSpacing.md),
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(KasSpacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        val prefix = if (transaction.type == TransactionType.MASUK) "+" else "−"
                        val color =
                            if (transaction.type == TransactionType.MASUK) ForestGreen else CoralRed
                        Text(
                            text = "$prefix ${formatRupiah(transaction.amount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color,
                        )
                    }
                    transaction.note.takeIf { it.isNotBlank() }?.let { note ->
                        Spacer(modifier = Modifier.height(KasSpacing.xs))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                    }
                    Spacer(modifier = Modifier.height(KasSpacing.xs))
                    Text(
                        text = transaction.date.format(fullDateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
            }
        }
    }
}
