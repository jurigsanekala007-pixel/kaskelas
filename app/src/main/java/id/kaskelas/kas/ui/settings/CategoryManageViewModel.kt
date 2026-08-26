package id.kaskelas.kas.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.repository.CategoryItem
import id.kaskelas.kas.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryManageUiState(
    val type: String = "MASUK",
    val categories: List<CategoryItem> = emptyList(),
    val showAddDialog: Boolean = false,
    val newCategoryName: String = "",
    val error: String? = null,
)

sealed class CategoryManageEvent {
    data class ShowSnackbar(val message: String) : CategoryManageEvent()
}

@HiltViewModel
class CategoryManageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CategoryRepository,
) : ViewModel() {

    private val type: String = savedStateHandle["type"] ?: "MASUK"

    private val _uiState = MutableStateFlow(CategoryManageUiState(type = type))
    val uiState: StateFlow<CategoryManageUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoryManageEvent>()
    val events: SharedFlow<CategoryManageEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.observeByType(type).collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, newCategoryName = "", error = null) }
    }

    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, newCategoryName = "", error = null) }
    }

    fun setNewCategoryName(name: String) {
        _uiState.update { it.copy(newCategoryName = name, error = null) }
    }

    fun addCategory() {
        val name = _uiState.value.newCategoryName.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(error = "Nama kategori tidak boleh kosong") }
            return
        }
        viewModelScope.launch {
            if (repository.exists(name, type)) {
                _uiState.update { it.copy(error = "Kategori \"$name\" sudah ada") }
                return@launch
            }
            repository.add(name, type)
            _uiState.update { it.copy(showAddDialog = false, newCategoryName = "", error = null) }
            _events.emit(CategoryManageEvent.ShowSnackbar("Kategori \"$name\" ditambahkan"))
        }
    }

    fun deleteCategory(category: CategoryItem) {
        if (category.isDefault) return
        viewModelScope.launch {
            repository.delete(category)
            _events.emit(CategoryManageEvent.ShowSnackbar("Kategori \"${category.name}\" dihapus"))
        }
    }
}
