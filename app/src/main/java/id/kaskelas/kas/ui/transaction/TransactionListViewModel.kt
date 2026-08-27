package id.kaskelas.kas.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.model.KategoriKeluar
import id.kaskelas.kas.domain.model.KategoriMasuk
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.repository.CategoryRepository
import id.kaskelas.kas.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val masukCategories: List<String> = emptyList(),
    val keluarCategories: List<String> = emptyList(),
    val filterCategory: String? = null,
    val searchQuery: String = "",
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val showDeleteDialog: Long? = null,
)

sealed class TransactionListEvent {
    data object TransactionDeleted : TransactionListEvent()
    data class ShowSnackbar(val message: String) : TransactionListEvent()
}

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransactionListEvent>()
    val events: SharedFlow<TransactionListEvent> = _events.asSharedFlow()

    val filteredTransactions: StateFlow<List<Transaction>> = _uiState
        .map { state ->
            state.transactions
                .filter { t ->
                    (state.filterCategory == null || t.category == state.filterCategory) &&
                    (state.searchQuery.isEmpty() ||
                        t.category.contains(state.searchQuery, ignoreCase = true) ||
                        t.note.contains(state.searchQuery, ignoreCase = true)) &&
                    (state.dateFrom == null || !t.date.isBefore(state.dateFrom)) &&
                    (state.dateTo == null || !t.date.isAfter(state.dateTo))
                }
                .sortedByDescending { it.date }
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            repository.observeAll().collect { all ->
                _uiState.update { it.copy(transactions = all) }
            }
        }
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val masuk = categoryRepository.getAllByType("MASUK").map { it.name }
                .ifEmpty { KategoriMasuk.entries.map { k -> k.label } }
            val keluar = categoryRepository.getAllByType("KELUAR").map { it.name }
                .ifEmpty { KategoriKeluar.entries.map { k -> k.label } }
            _uiState.update { it.copy(masukCategories = masuk, keluarCategories = keluar) }
        }
    }

    fun setFilterCategory(category: String?) {
        _uiState.update { it.copy(filterCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setDateFrom(date: LocalDate?) {
        _uiState.update { it.copy(dateFrom = date) }
    }

    fun setDateTo(date: LocalDate?) {
        _uiState.update { it.copy(dateTo = date) }
    }

    fun clearDateFilter() {
        _uiState.update { it.copy(dateFrom = null, dateTo = null) }
    }

    fun showDeleteDialog(transactionId: Long) {
        _uiState.update { it.copy(showDeleteDialog = transactionId) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = null) }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                val transaction = _uiState.value.transactions.find { it.id == transactionId }
                if (transaction != null) {
                    repository.delete(transaction)
                    _uiState.update { it.copy(showDeleteDialog = null) }
                    _events.emit(TransactionListEvent.TransactionDeleted)
                }
            } catch (e: Exception) {
                _events.emit(TransactionListEvent.ShowSnackbar("Gagal menghapus: ${e.message}"))
            }
        }
    }
}
