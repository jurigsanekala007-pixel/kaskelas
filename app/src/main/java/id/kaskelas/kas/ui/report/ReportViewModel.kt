package id.kaskelas.kas.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.repository.TransactionRepository
import id.kaskelas.kas.domain.usecase.BalanceCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

data class ReportUiState(
    val isLoading: Boolean = true,
    val selectedMonth: YearMonth = YearMonth.now(),
    /** Saldo akhir = saldo semua transaksi s.d. akhir bulan terpilih. */
    val endingBalance: Long = 0L,
    val totalMasuk: Long = 0L,
    val totalKeluar: Long = 0L,
    val transactions: List<Transaction> = emptyList(),
    val availableMonths: List<YearMonth> = emptyList(),
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    repository: TransactionRepository,
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    val uiState: StateFlow<ReportUiState> =
        combine(repository.observeAll(), _selectedMonth) { all, month ->
            val inMonth = all.filter { YearMonth.from(it.date) == month }
            val summary = BalanceCalculator.summary(inMonth)
            val endOfMonthExclusive = month.plusMonths(1).atDay(1)
            // Saldo akhir bulan = akumulasi semua transaksi sebelum awal bulan berikutnya.
            val upToEndOfMonth = all.filter { it.date < endOfMonthExclusive }

            ReportUiState(
                isLoading = false,
                selectedMonth = month,
                endingBalance = BalanceCalculator.balance(upToEndOfMonth),
                totalMasuk = summary.totalMasuk,
                totalKeluar = summary.totalKeluar,
                transactions = inMonth, // observeAll sudah urut tanggal desc
                availableMonths = all.map { YearMonth.from(it.date) }.distinct().sortedDescending(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportUiState(isLoading = true),
        )

    fun selectMonth(month: YearMonth) {
        _selectedMonth.value = month
    }
}
