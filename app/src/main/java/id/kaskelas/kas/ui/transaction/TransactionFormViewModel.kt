package id.kaskelas.kas.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.model.KategoriKeluar
import id.kaskelas.kas.domain.model.KategoriMasuk
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import id.kaskelas.kas.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionFormUiState(
    val type: TransactionType = TransactionType.MASUK,
    val amount: String = "",
    val category: String = KategoriMasuk.IURAN.label,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val editingId: Long? = null,
)

sealed class TransactionFormAction {
    data object Saved : TransactionFormAction()
    data class ShowError(val message: String) : TransactionFormAction()
}

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState: StateFlow<TransactionFormUiState> = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<TransactionFormAction>()
    val actions: SharedFlow<TransactionFormAction> = _actions.asSharedFlow()

    fun setType(type: TransactionType) {
        val defaultCategory = when (type) {
            TransactionType.MASUK -> KategoriMasuk.IURAN.label
            TransactionType.KELUAR -> KategoriKeluar.SNACK.label
        }
        _uiState.update {
            it.copy(
                type = type,
                category = defaultCategory,
                error = null,
            )
        }
    }

    fun setAmount(value: String) {
        _uiState.update { it.copy(amount = value, error = null) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(category = category, error = null) }
    }

    fun setDate(date: LocalDate) {
        _uiState.update { it.copy(date = date, error = null) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note, error = null) }
    }

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            val existing = repository.getById(id)
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        editingId = id,
                        type = existing.type,
                        amount = existing.amount.toString(),
                        category = existing.category,
                        date = existing.date,
                        note = existing.note,
                    )
                }
            }
        }
    }

    fun save() {
        val state = _uiState.value

        // Validasi
        if (state.amount.isBlank()) {
            _uiState.update { it.copy(error = "Nominal tidak boleh kosong") }
            return
        }
        val amountValue = state.amount.toLongOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.update { it.copy(error = "Nominal harus angka lebih dari 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val transaction = Transaction(
                id = state.editingId ?: 0L,
                type = state.type,
                amount = amountValue,
                category = state.category,
                date = state.date,
                note = state.note,
            )

            try {
                if (state.editingId == null) {
                    repository.add(transaction)
                } else {
                    repository.update(transaction)
                }
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                _actions.emit(TransactionFormAction.Saved)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Gagal menyimpan") }
                _actions.emit(TransactionFormAction.ShowError(e.message ?: "Gagal menyimpan"))
            }
        }
    }
}
