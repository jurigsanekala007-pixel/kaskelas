package id.kaskelas.kas.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.repository.TransactionRepository
import id.kaskelas.kas.domain.usecase.BalanceCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val balance: Long = 0L,
    val monthTotalMasuk: Long = 0L,
    val monthTotalKeluar: Long = 0L,
    val lastTransaction: Transaction? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: TransactionRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = repository.observeAll()
        .map { all ->
            if (all.isEmpty()) {
                DashboardUiState(isLoading = false)
            } else {
                val thisMonth = YearMonth.now()
                val monthTxns = all.filter {
                    YearMonth.from(it.date) == thisMonth
                }
                val summary = BalanceCalculator.summary(monthTxns)
                DashboardUiState(
                    isLoading = false,
                    balance = BalanceCalculator.balance(all),
                    monthTotalMasuk = summary.totalMasuk,
                    monthTotalKeluar = summary.totalKeluar,
                    // observeAll sudah urut tanggal terbaru dulu
                    lastTransaction = all.first(),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(isLoading = true),
        )

    companion object {
        // Bulan berjalan untuk label; dipakai UI.
        fun currentMonthLabel(): String =
            LocalDate.now().month.getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.of("id", "ID"),
            ).replaceFirstChar { it.uppercase() }
    }
}
