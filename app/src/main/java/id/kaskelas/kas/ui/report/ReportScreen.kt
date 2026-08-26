package id.kaskelas.kas.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.formatRupiah
import id.kaskelas.kas.ui.theme.AmberGold
import id.kaskelas.kas.ui.theme.CloudGray
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasSpacing
import id.kaskelas.kas.ui.theme.MidnightNavy
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.of("id", "ID"))
private val dayFormatter = DateTimeFormatter.ofPattern("dd MMM")

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }
        return
    }

    if (state.availableMonths.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(KasSpacing.md),
        ) {
            Text(
                text = "Laporan",
                style = MaterialTheme.typography.headlineLarge,
                color = MidnightNavy,
            )
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            EmptyReport()
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(KasSpacing.md),
        verticalArrangement = Arrangement.spacedBy(KasSpacing.sm),
    ) {
        item {
            Text(
                text = "Laporan",
                style = MaterialTheme.typography.headlineLarge,
                color = MidnightNavy,
            )
            Spacer(modifier = Modifier.height(KasSpacing.lg))
        }

        item {
            MonthSelector(
                selectedMonth = state.selectedMonth,
                availableMonths = state.availableMonths,
                onPrevious = { viewModel.selectMonth(state.selectedMonth.minusMonths(1)) },
                onNext = { viewModel.selectMonth(state.selectedMonth.plusMonths(1)) },
                onSelectFromList = viewModel::selectMonth,
            )
            Spacer(modifier = Modifier.height(KasSpacing.md))
        }

        item {
            SummaryCard(
                totalMasuk = state.totalMasuk,
                totalKeluar = state.totalKeluar,
                endingBalance = state.endingBalance,
            )
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            Text(
                text = "Transaksi Bulan Ini (${state.transactions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MidnightNavy,
            )
            Spacer(modifier = Modifier.height(KasSpacing.sm))
        }

        if (state.transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CloudGray),
                ) {
                    Text(
                        text = "Tidak ada transaksi di bulan ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MidnightNavy.copy(alpha = 0.6f),
                        modifier = Modifier.padding(KasSpacing.md),
                    )
                }
            }
        } else {
            items(state.transactions, key = { it.id }) { transaction ->
                ReportTransactionRow(transaction)
            }
        }
    }
}

@Composable
private fun EmptyReport() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CloudGray),
    ) {
        Text(
            text = "Belum ada data untuk dilaporkan.\nCatat transaksi lewat tab Transaksi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MidnightNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(KasSpacing.md),
        )
    }
}

@Composable
private fun MonthSelector(
    selectedMonth: YearMonth,
    availableMonths: List<YearMonth>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelectFromList: (YearMonth) -> Unit,
) {
    var showList by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Bulan sebelumnya")
        }
        OutlinedButton(
            onClick = { showList = !showList },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = selectedMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                fontWeight = FontWeight.SemiBold,
                color = MidnightNavy,
            )
        }
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Bulan berikutnya")
        }
    }

    if (showList) {
        Spacer(modifier = Modifier.height(KasSpacing.sm))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CloudGray),
        ) {
            Column(modifier = Modifier.padding(KasSpacing.sm)) {
                availableMonths.forEach { month ->
                    MonthTextButton(month = month, selected = month == selectedMonth) {
                        onSelectFromList(month)
                        showList = false
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthTextButton(month: YearMonth, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = month.format(monthFormatter).replaceFirstChar { it.uppercase() },
            color = if (selected) AmberGold else MidnightNavy,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun SummaryCard(
    totalMasuk: Long,
    totalKeluar: Long,
    endingBalance: Long,
) {
    // Kartu saldo akhir — aksen amber sesuai PRD §11 (laporan).
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
                text = "Saldo Akhir",
                style = MaterialTheme.typography.bodyMedium,
                color = id.kaskelas.kas.ui.theme.BoneWhite.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(KasSpacing.xs))
            Text(
                text = formatRupiah(endingBalance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (endingBalance < 0) CoralRed else id.kaskelas.kas.ui.theme.BoneWhite,
            )
        }
    }
    Spacer(modifier = Modifier.height(KasSpacing.md))
    Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.md)) {
        MiniSummary(
            label = "Total Masuk",
            amount = totalMasuk,
            color = ForestGreen,
            modifier = Modifier.weight(1f),
        )
        MiniSummary(
            label = "Total Keluar",
            amount = totalKeluar,
            color = CoralRed,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MiniSummary(
    label: String,
    amount: Long,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CloudGray),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KasSpacing.md),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MidnightNavy.copy(alpha = 0.7f),
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
private fun ReportTransactionRow(transaction: id.kaskelas.kas.domain.model.Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CloudGray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KasSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = transaction.date.format(dayFormatter),
                style = MaterialTheme.typography.labelSmall,
                color = MidnightNavy.copy(alpha = 0.6f),
                modifier = Modifier.padding(end = KasSpacing.md),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MidnightNavy,
                )
                transaction.note.takeIf { it.isNotBlank() }?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MidnightNavy.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
            val prefix = if (transaction.type == TransactionType.MASUK) "+" else "−"
            val color = if (transaction.type == TransactionType.MASUK) ForestGreen else CoralRed
            Text(
                text = "$prefix ${formatRupiah(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}
