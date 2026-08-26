package id.kaskelas.kas.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val filterCategory: String? = null,
    val searchQuery: String = "",
    val showDeleteDialog: Long? = null, // id transaksi yang mau dihapus
)

sealed class TransactionListEvent {
    data object TransactionDeleted : TransactionListEvent()
    data class ShowSnackbar(val message: String) : TransactionListEvent()
}

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransactionListEvent>()
    val events: SharedFlow<TransactionListEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { all ->
                _uiState.update { it.copy(transactions = all) }
            }
        }
    }

    fun setFilterCategory(category: String?) {
        _uiState.update { it.copy(filterCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
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

    fun getFilteredTransactions(): List<Transaction> {
        val state = _uiState.value
        return state.transactions
            .filter { t ->
                (state.filterCategory == null || t.category == state.filterCategory) &&
                (state.searchQuery.isEmpty() ||
                    t.category.contains(state.searchQuery, ignoreCase = true) ||
                    t.note.contains(state.searchQuery, ignoreCase = true))
            }
            .sortedByDescending { it.date }
    }
}
