package id.kaskelas.kas.ui.transaction

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import id.kaskelas.kas.domain.model.KategoriKeluar
import id.kaskelas.kas.domain.model.KategoriMasuk
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasSpacing
import id.kaskelas.kas.ui.theme.MidnightNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onAddTransaction: () -> Unit,
    modifier: Modifier = Modifier,
    onEditTransaction: (Long) -> Unit = {},
    viewModel: TransactionListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val filtered by viewModel.filteredTransactions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TransactionListEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                TransactionListEvent.TransactionDeleted -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Transaksi",
                        fontWeight = FontWeight.Bold,
                        color = MidnightNavy,
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = ForestGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Pencarian
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                label = { Text("Cari kategori atau keterangan…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KasSpacing.md, vertical = KasSpacing.sm),
                singleLine = true,
            )

            // Filter tanggal
            val hasDateFilter = state.dateFrom != null || state.dateTo != null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KasSpacing.md, vertical = KasSpacing.xs),
                horizontalArrangement = Arrangement.spacedBy(KasSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.dateFrom?.format(dateFormatter) ?: "Dari",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dari") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }.also { source ->
                        LaunchedEffect(source) {
                            source.interactions.collect { if (it is androidx.compose.foundation.interaction.PressInteraction.Release) showDateFromPicker = true }
                        }
                    },
                )
                OutlinedTextField(
                    value = state.dateTo?.format(dateFormatter) ?: "Sampai",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sampai") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }.also { source ->
                        LaunchedEffect(source) {
                            source.interactions.collect { if (it is androidx.compose.foundation.interaction.PressInteraction.Release) showDateToPicker = true }
                        }
                    },
                )
                if (hasDateFilter) {
                    TextButton(onClick = viewModel::clearDateFilter) {
                        Text("Reset")
                    }
                }
            }

            // Filter chip kategori
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = KasSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(KasSpacing.sm),
            ) {
                androidx.compose.material3.FilterChip(
                    selected = state.filterCategory == null,
                    onClick = { viewModel.setFilterCategory(null) },
                    label = { Text("Semua") },
                )
                KategoriMasuk.entries.forEach { kategori ->
                    androidx.compose.material3.FilterChip(
                        selected = state.filterCategory == kategori.label,
                        onClick = {
                            viewModel.setFilterCategory(
                                if (state.filterCategory == kategori.label) null else kategori.label,
                            )
                        },
                        label = { Text(kategori.label) },
                    )
                }
                KategoriKeluar.entries.forEach { kategori ->
                    androidx.compose.material3.FilterChip(
                        selected = state.filterCategory == kategori.label,
                        onClick = {
                            viewModel.setFilterCategory(
                                if (state.filterCategory == kategori.label) null else kategori.label,
                            )
                        },
                        label = { Text(kategori.label) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(KasSpacing.sm))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Belum ada transaksi",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.height(KasSpacing.sm))
                        Text(
                            text = "Tekan tombol + untuk menambah",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = KasSpacing.md,
                        end = KasSpacing.md,
                        bottom = KasSpacing.md,
                    ),
                    verticalArrangement = Arrangement.spacedBy(KasSpacing.sm),
                ) {
                    items(filtered, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = viewModel::showDeleteDialog,
                            onEdit = onEditTransaction,
                        )
                    }
                }
            }
        }
    }

    // DatePickerDialog — Dari tanggal
    if (showDateFromPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dateFrom?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDateFromPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.setDateFrom(date)
                    }
                    showDateFromPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDateFromPicker = false }) { Text("Batal") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // DatePickerDialog — Sampai tanggal
    if (showDateToPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dateTo?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDateToPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.setDateTo(date)
                    }
                    showDateToPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDateToPicker = false }) { Text("Batal") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Dialog konfirmasi hapus (PRD §8.2: konfirmasi sebelum hapus data)
    state.showDeleteDialog?.let { id ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("Hapus transaksi?") },
            text = { Text("Transaksi yang dihapus tidak bisa dikembalikan.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTransaction(id) }) {
                    Text("Hapus", color = CoralRed)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteDialog) {
                    Text("Batal", color = ForestGreen)
                }
            },
        )
    }
}
