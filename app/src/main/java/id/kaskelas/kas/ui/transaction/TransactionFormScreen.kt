package id.kaskelas.kas.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import id.kaskelas.kas.domain.model.KategoriKeluar
import id.kaskelas.kas.domain.model.KategoriMasuk
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.ui.theme.CoralRed
import id.kaskelas.kas.ui.theme.DeepBlue
import id.kaskelas.kas.ui.theme.ForestGreen
import id.kaskelas.kas.ui.theme.KasSpacing
import id.kaskelas.kas.ui.theme.MidnightNavy
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    onBack: () -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    transactionId: Long? = null,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load existing if editing
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId)
        }
    }

    // React to save success
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Transaksi berhasil disimpan")
                onTransactionSaved()
            }
        }
    }

    // Show error messages
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (transactionId != null)
                            "Edit Transaksi"
                        else
                            "Tambah Transaksi",
                        fontWeight = FontWeight.Bold,
                        color = MidnightNavy,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(KasSpacing.md)
                .verticalScroll(rememberScrollState()),
        ) {
            // Type picker (Pemasukan / Pengeluaran)
            TypePicker(
                selectedType = state.type,
                onTypeSelected = viewModel::setType,
            )

            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Nominal
            AmountSection(
                amount = state.amount,
                onAmountChange = viewModel::setAmount,
                type = state.type,
            )

            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Kategori
            CategorySection(
                type = state.type,
                selectedCategory = state.category,
                onCategorySelected = viewModel::setCategory,
            )

            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Tanggal
            DateSection(
                date = state.date,
                onDateSelected = viewModel::setDate,
            )

            Spacer(modifier = Modifier.height(KasSpacing.lg))

            // Keterangan
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = { Text("Keterangan (opsional)") },
                placeholder = { Text("Contoh: Iuran bulan September") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(KasSpacing.xl))

            // Tombol simpan
            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(
                        text = if (transactionId != null)
                            "Update Transaksi"
                        else
                            "Simpan Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TypePicker(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(KasSpacing.md),
    ) {
        TypeChip(
            label = "Pemasukan",
            icon = Icons.Filled.Add,
            isSelected = selectedType == TransactionType.MASUK,
            color = ForestGreen,
            onClick = { onTypeSelected(TransactionType.MASUK) },
        )
        TypeChip(
            label = "Pengeluaran",
            icon = Icons.Filled.Remove,
            isSelected = selectedType == TransactionType.KELUAR,
            color = CoralRed,
            onClick = { onTypeSelected(TransactionType.KELUAR) },
        )
    }
}

@Composable
private fun TypeChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    val containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimary
    else
        DeepBlue

    androidx.compose.material3.FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
            )
        },
        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            selectedContainerColor = color,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

@Composable
private fun AmountSection(
    amount: String,
    onAmountChange: (String) -> Unit,
    type: TransactionType,
) {
    val color = when (type) {
        TransactionType.MASUK -> ForestGreen
        TransactionType.KELUAR -> CoralRed
    }

    Column {
        Text(
            text = if (type == TransactionType.MASUK) "Nominal Pemasukan" else "Nominal Pengeluaran",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MidnightNavy,
        )
        Spacer(modifier = Modifier.height(KasSpacing.sm))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    if (amount.isNotEmpty()) {
                        onAmountChange(amount.dropLast(1))
                    }
                },
                enabled = amount.isNotEmpty(),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Hapus",
                    tint = color,
                )
            }
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Rp") },
                placeholder = { Text("0") },
                readOnly = false,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                ),
                modifier = Modifier.weight(1f),
                isError = !amount.isBlank() && amount.toLongOrNull() == null,
            )
            IconButton(
                onClick = { onAmountChange("") },
                enabled = amount.isNotEmpty(),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Hapus Semua",
                    tint = color,
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    type: TransactionType,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    val categories: List<String> = when (type) {
        TransactionType.MASUK -> KategoriMasuk.entries.map { it.label }
        TransactionType.KELUAR -> KategoriKeluar.entries.map { it.label }
    }

    Column {
        Text(
            text = "Kategori",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MidnightNavy,
        )
        Spacer(modifier = Modifier.height(KasSpacing.sm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(KasSpacing.sm),
        ) {
            categories.forEach { kategori ->
                FilterChip(
                    selected = selectedCategory == kategori,
                    onClick = { onCategorySelected(kategori) },
                    label = { Text(kategori) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSection(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Tanggal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MidnightNavy,
        )
        Spacer(modifier = Modifier.height(KasSpacing.sm))
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                fontWeight = FontWeight.Medium,
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochDay() * 24 * 60 * 60 * 1000,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            onDateSelected(selectedDate)
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
