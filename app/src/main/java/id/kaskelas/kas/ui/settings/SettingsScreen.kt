package id.kaskelas.kas.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import id.kaskelas.kas.ui.lock.PinDots
import id.kaskelas.kas.ui.lock.PinKeypad
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.KasSpacing
import java.time.format.DateTimeFormatter

private val backupFileName =
    "kas-kelas-backup-" + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".json"

@Composable
fun SettingsScreen(
    onNavigateToCategory: (String) -> Unit = { _ -> },
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            message = when (event) {
                is SettingsEvent.ShowMessage -> event.message
            }
        }
    }

    // SAF: pilih tujuan file backup
    val createBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let(viewModel::exportBackup) }

    // SAF: pilih file backup untuk restore
    val openBackupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(viewModel::importBackup) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KasSpacing.md),
    ) {
        Text(
            text = "Pengaturan",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(KasSpacing.lg))

        // ---------- Keamanan ----------
        SectionCard(title = "Keamanan") {
            SettingRow(
                title = "Ubah PIN",
                subtitle = "Ganti PIN 4 digit aplikasi",
                onClick = viewModel::startChangePin,
                actionLabel = "Ubah",
            )
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))

        // ---------- Kategori ----------
        SectionCard(title = "Kategori") {
            SettingRow(
                title = "Kategori Pemasukan",
                subtitle = "Kelola kategori pemasukan",
                onClick = { onNavigateToCategory("MASUK") },
                actionLabel = "Atur",
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = KasSpacing.sm))
            SettingRow(
                title = "Kategori Pengeluaran",
                subtitle = "Kelola kategori pengeluaran",
                onClick = { onNavigateToCategory("KELUAR") },
                actionLabel = "Atur",
            )
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))

        // ---------- Backup & Restore ----------
        SectionCard(title = "Backup & Restore") {
            Text(
                text = "Simpan seluruh transaksi ke file JSON, atau pulihkan dari backup sebelumnya. " +
                    "Restore akan MENGGANTI semua data saat ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(KasSpacing.md))
            Row(horizontalArrangement = Arrangement.spacedBy(KasSpacing.sm)) {
                OutlinedButton(
                    onClick = { createBackupLauncher.launch(backupFileName) },
                    enabled = !state.busy,
                    modifier = Modifier.weight(1f),
                ) { Text("Backup") }
                OutlinedButton(
                    onClick = { openBackupLauncher.launch(arrayOf("application/json")) },
                    enabled = !state.busy,
                    modifier = Modifier.weight(1f),
                ) { Text("Restore") }
            }
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))

        // ---------- Tentang ----------
        SectionCard(title = "Tentang Aplikasi") {
            SettingInfoRow(label = "Nama", value = "Kas Kelas")
            HorizontalDivider(modifier = Modifier.padding(vertical = KasSpacing.sm))
            SettingInfoRow(label = "Versi", value = viewModel.appVersion.ifEmpty { "v1.0.0" })
        }

        if (state.busy) {
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // Dialog pesan (sukses/gagal)
    message?.let { msg ->
        AlertDialog(
            onDismissRequest = { message = null },
            confirmButton = {
                TextButton(onClick = { message = null }) { Text("OK") }
            },
            title = { Text("Info") },
            text = { Text(msg) },
        )
    }

    // Overlay ubah PIN
    if (state.pinChangeStep != PinChangeStep.NONE) {
        ChangePinOverlay(state, viewModel)
    }
}

@Composable
private fun ChangePinOverlay(state: SettingsUiState, viewModel: SettingsViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::cancelChangePin,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = viewModel::cancelChangePin) { Text("Batal") }
        },
        title = { Text(text = pinTitle(state.pinChangeStep)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                state.pinError?.let {
                    Text(it, color = CoralRed, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(KasSpacing.sm))
                }
                val filled = when (state.pinChangeStep) {
                    PinChangeStep.OLD_PIN -> state.oldPin.length
                    PinChangeStep.NEW_PIN -> state.newPin.length
                    PinChangeStep.CONFIRM_PIN -> state.confirmPin.length
                    PinChangeStep.NONE -> 0
                }
                PinDots(filled = filled)
                Spacer(modifier = Modifier.height(KasSpacing.md))
                PinKeypad(
                    onDigit = viewModel::onPinDigit,
                    onDelete = viewModel::onPinDelete,
                )
            }
        },
    )
}

private fun pinTitle(step: PinChangeStep): String = when (step) {
    PinChangeStep.OLD_PIN -> "Masukkan PIN Lama"
    PinChangeStep.NEW_PIN -> "PIN Baru"
    PinChangeStep.CONFIRM_PIN -> "Konfirmasi PIN Baru"
    PinChangeStep.NONE -> ""
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            content()
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    actionLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        TextButton(onClick = onClick) { Text(actionLabel) }
    }
}

@Composable
private fun SettingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
