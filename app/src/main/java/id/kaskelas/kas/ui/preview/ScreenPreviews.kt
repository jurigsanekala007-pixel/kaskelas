package id.kaskelas.kas.ui.preview

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.kaskelas.kas.domain.model.KategoriMasuk
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.formatRupiah
import id.kaskelas.kas.ui.lock.PinDots
import id.kaskelas.kas.ui.lock.PinKeypad
import id.kaskelas.kas.ui.transaction.TransactionItem
import id.kaskelas.kas.ui.theme.BoneWhite
import id.kaskelas.kas.ui.theme.CloudGray
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasKelasTheme
import id.kaskelas.kas.ui.theme.KasSpacing
import id.kaskelas.kas.ui.theme.MidnightNavy
import java.time.LocalDate

// ────────────────────────────────────────────────────
// Dummy data
// ────────────────────────────────────────────────────

private val sampleTransactions = listOf(
    Transaction(1, TransactionType.MASUK, 250_000, "Iuran", LocalDate.of(2026, 8, 26), "Iuran Agustus"),
    Transaction(2, TransactionType.KELUAR, 50_000, "Snack", LocalDate.of(2026, 8, 20), "Snack arisan"),
    Transaction(3, TransactionType.MASUK, 100_000, "Donasi", LocalDate.of(2026, 8, 15), ""),
    Transaction(4, TransactionType.KELUAR, 30_000, "Alat Tulis", LocalDate.of(2026, 8, 10), "Pensil & buku"),
)

// ════════════════════════════════════════════════════
// DASHBOARD PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Dashboard - Balance Card", showBackground = true, widthDp = 400)
@Composable
private fun balanceCardPreview() {
    KasKelasTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightNavy),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KasSpacing.lg),
            ) {
                Text("Saldo Saat Ini", style = MaterialTheme.typography.bodyMedium, color = BoneWhite)
                Spacer(modifier = Modifier.height(KasSpacing.xs))
                Text(
                    text = formatRupiah(1_250_000),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = BoneWhite,
                )
            }
        }
    }
}

@Preview(name = "Dashboard - Balance Negatif", showBackground = true, widthDp = 400)
@Composable
private fun balanceCardNegativePreview() {
    KasKelasTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightNavy),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KasSpacing.lg),
            ) {
                Text("Saldo Saat Ini", style = MaterialTheme.typography.bodyMedium, color = BoneWhite)
                Spacer(modifier = Modifier.height(KasSpacing.xs))
                Text(
                    text = formatRupiah(-50_000),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = CoralRed,
                )
            }
        }
    }
}

@Preview(name = "Dashboard - Month Summary", showBackground = true, widthDp = 400)
@Composable
private fun monthSummaryPreview() {
    KasKelasTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.md)) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CloudGray),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                    Text("Masuk — Agustus", style = MaterialTheme.typography.labelSmall, color = MidnightNavy.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(KasSpacing.xs))
                    Text(formatRupiah(350_000), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CloudGray),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                    Text("Keluar — Agustus", style = MaterialTheme.typography.labelSmall, color = MidnightNavy.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(KasSpacing.xs))
                    Text(formatRupiah(80_000), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CoralRed)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
// TRANSACTION LIST PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Transaction List - With Data", showBackground = true, widthDp = 400)
@Composable
private fun transactionListPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.md),
        ) {
            Text("Riwayat Transaksi", style = MaterialTheme.typography.headlineLarge, color = MidnightNavy)
            Spacer(modifier = Modifier.height(KasSpacing.md))
            sampleTransactions.forEach { tx ->
                TransactionItem(transaction = tx, onDelete = {})
                Spacer(modifier = Modifier.height(KasSpacing.sm))
            }
        }
    }
}

@Preview(name = "Transaction List - Empty", showBackground = true, widthDp = 400)
@Composable
private fun transactionListEmptyPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.md),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Belum ada transaksi", style = MaterialTheme.typography.titleLarge, color = MidnightNavy.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            Text("Tekan tombol + untuk menambah", style = MaterialTheme.typography.bodyMedium, color = MidnightNavy.copy(alpha = 0.4f))
        }
    }
}

// ════════════════════════════════════════════════════
// TRANSACTION FORM PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Transaction Form - Tambah", showBackground = true, widthDp = 400)
@Composable
private fun transactionFormPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.md).verticalScroll(rememberScrollState()),
        ) {
            Text("Tambah Transaksi", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MidnightNavy)
            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Type chips
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(KasSpacing.md)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Pemasukan") })
                FilterChip(selected = false, onClick = {}, label = { Text("Pengeluaran") })
            }
            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Amount
            Text("Nominal Pemasukan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MidnightNavy)
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            Text("Rp 250.000", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ForestGreen)
            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Category chips
            Text("Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MidnightNavy)
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.sm)) {
                KategoriMasuk.entries.take(3).forEach { kategori ->
                    FilterChip(
                        selected = kategori == KategoriMasuk.IURAN,
                        onClick = {},
                        label = { Text(kategori.label) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Date
            Text("Tanggal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MidnightNavy)
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("26 Agustus 2026", fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ════════════════════════════════════════════════════
// REPORT PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Report - Summary Card", showBackground = true, widthDp = 400)
@Composable
private fun reportSummaryCardPreview() {
    KasKelasTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightNavy),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.lg)) {
                Text("Saldo Akhir", style = MaterialTheme.typography.bodyMedium, color = BoneWhite.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(KasSpacing.xs))
                Text(formatRupiah(1_170_000), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = BoneWhite)
            }
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.md)) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CloudGray)) {
                Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                    Text("Total Masuk", style = MaterialTheme.typography.labelSmall, color = MidnightNavy.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(KasSpacing.xs))
                    Text(formatRupiah(350_000), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CloudGray)) {
                Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                    Text("Total Keluar", style = MaterialTheme.typography.labelSmall, color = MidnightNavy.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(KasSpacing.xs))
                    Text(formatRupiah(180_000), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CoralRed)
                }
            }
        }
    }
}

@Preview(name = "Report - Transaction Row", showBackground = true, widthDp = 400)
@Composable
private fun reportTransactionRowPreview() {
    KasKelasTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CloudGray),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(KasSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("26 Agu", style = MaterialTheme.typography.labelSmall, color = MidnightNavy.copy(alpha = 0.6f), modifier = Modifier.padding(end = KasSpacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Iuran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MidnightNavy)
                    Text("Iuran bulan Agustus", style = MaterialTheme.typography.bodyMedium, color = MidnightNavy.copy(alpha = 0.6f), maxLines = 1)
                }
                Text("+ ${formatRupiah(250_000)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ForestGreen)
            }
        }
    }
}

// ════════════════════════════════════════════════════
// SETTINGS PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Settings - Section Card", showBackground = true, widthDp = 400)
@Composable
private fun settingsSectionCardPreview() {
    KasKelasTheme {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                Text("Keamanan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MidnightNavy)
                Spacer(modifier = Modifier.height(KasSpacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ubah PIN", style = MaterialTheme.typography.titleMedium, color = MidnightNavy)
                        Text("Ganti PIN 4 digit aplikasi", style = MaterialTheme.typography.bodyMedium, color = MidnightNavy.copy(alpha = 0.6f))
                    }
                    TextButton(onClick = {}) { Text("Ubah") }
                }
            }
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(KasSpacing.md)) {
                Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MidnightNavy)
                Spacer(modifier = Modifier.height(KasSpacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nama", style = MaterialTheme.typography.bodyMedium, color = MidnightNavy.copy(alpha = 0.7f))
                    Text("Kas Kelas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MidnightNavy)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = KasSpacing.sm))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Versi", style = MaterialTheme.typography.bodyMedium, color = MidnightNavy.copy(alpha = 0.7f))
                    Text("v1.0.0", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MidnightNavy)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════
// LOCK SCREEN PREVIEWS
// ════════════════════════════════════════════════════

@Preview(name = "Lock - PIN Entry", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
private fun lockPinEntryPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.6f))
            Text("Kas Kelas", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(KasSpacing.xl))
            Text("Masukkan PIN", style = MaterialTheme.typography.titleLarge)
            Text("Ketik PIN untuk membuka aplikasi", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            PinDots(filled = 2)
            Spacer(modifier = Modifier.weight(1f))
            PinKeypad(onDigit = {}, onDelete = {})
        }
    }
}

@Preview(name = "Lock - PIN Setup", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
private fun lockPinSetupPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.6f))
            Text("Kas Kelas", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(KasSpacing.xl))
            Text("Buat PIN", style = MaterialTheme.typography.titleLarge)
            Text("Buat PIN 4 digit untuk membuka aplikasi", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            PinDots(filled = 0)
            Spacer(modifier = Modifier.weight(1f))
            PinKeypad(onDigit = {}, onDelete = {})
        }
    }
}

@Preview(name = "Lock - Security Question", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
private fun lockSecurityQuestionPreview() {
    KasKelasTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(KasSpacing.lg),
        ) {
            Text("Atur Pertanyaan Keamanan", style = MaterialTheme.typography.titleLarge)
            Text("Pertanyaan ini dipakai kalau kamu lupa PIN.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(KasSpacing.md))
            repeat(3) {
                FilterChip(
                    selected = it == 0,
                    onClick = {},
                    label = { Text("Siapa nama hewan peliharaanmu?") },
                    modifier = Modifier.padding(vertical = KasSpacing.xs),
                )
            }
            Spacer(modifier = Modifier.height(KasSpacing.md))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Jawaban") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Selesai")
            }
        }
    }
}
