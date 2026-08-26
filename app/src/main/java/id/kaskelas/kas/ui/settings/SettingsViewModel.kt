package id.kaskelas.kas.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.kaskelas.kas.core.util.BackupFormatException
import id.kaskelas.kas.core.util.BackupSerializer
import id.kaskelas.kas.domain.repository.LockRepository
import id.kaskelas.kas.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

data class SettingsUiState(
    /** Step ubah PIN: null = tidak sedang mengubah. */
    val pinChangeStep: PinChangeStep = PinChangeStep.NONE,
    val oldPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val pinError: String? = null,
    val busy: Boolean = false,
)

enum class PinChangeStep { NONE, OLD_PIN, NEW_PIN, CONFIRM_PIN }

sealed class SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val lockRepository: LockRepository,
    private val transactionRepository: TransactionRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    val appVersion: String = try {
        val pm = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        "v${pm.versionName}"
    } catch (e: Exception) {
        ""
    }

    // ---------- Ubah PIN ----------

    fun startChangePin() {
        _uiState.update {
            SettingsUiState(pinChangeStep = PinChangeStep.OLD_PIN)
        }
    }

    fun cancelChangePin() {
        _uiState.update { SettingsUiState() }
    }

    fun onPinDigit(digit: Char) {
        val s = _uiState.value
        if (s.busy) return
        when (s.pinChangeStep) {
            PinChangeStep.NONE -> Unit
            PinChangeStep.OLD_PIN -> {
                val pin = s.oldPin + digit
                _uiState.update {
                    if (pin.length == PIN_LENGTH) it.copy(oldPin = pin, pinChangeStep = PinChangeStep.NEW_PIN, pinError = null)
                    else it.copy(oldPin = pin)
                }
            }
            PinChangeStep.NEW_PIN -> {
                val pin = s.newPin + digit
                _uiState.update {
                    if (pin.length == PIN_LENGTH) it.copy(newPin = pin, pinChangeStep = PinChangeStep.CONFIRM_PIN, pinError = null)
                    else it.copy(newPin = pin)
                }
            }
            PinChangeStep.CONFIRM_PIN -> {
                val pin = s.confirmPin + digit
                if (pin.length < PIN_LENGTH) {
                    _uiState.update { it.copy(confirmPin = pin) }
                } else {
                    _uiState.update { it.copy(confirmPin = pin) }
                    submitPinChange()
                }
            }
        }
    }

    fun onPinDelete() {
        val s = _uiState.value
        when (s.pinChangeStep) {
            PinChangeStep.NEW_PIN ->
                if (s.newPin.isNotEmpty()) _uiState.update { it.copy(newPin = s.newPin.dropLast(1)) }
            PinChangeStep.CONFIRM_PIN ->
                if (s.confirmPin.isNotEmpty()) _uiState.update { it.copy(confirmPin = s.confirmPin.dropLast(1)) }
            PinChangeStep.OLD_PIN ->
                if (s.oldPin.isNotEmpty()) _uiState.update { it.copy(oldPin = s.oldPin.dropLast(1)) }
            PinChangeStep.NONE -> Unit
        }
    }

    private fun submitPinChange() {
        val s = _uiState.value
        if (s.confirmPin != s.newPin) {
            _uiState.update {
                it.copy(
                    newPin = "",
                    confirmPin = "",
                    pinChangeStep = PinChangeStep.NEW_PIN,
                    pinError = "PIN tidak sama, ulangi",
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true) }
            val ok = lockRepository.changePin(s.oldPin, s.newPin)
            if (ok) {
                _uiState.update { SettingsUiState() }
                _events.emit(SettingsEvent.ShowMessage("PIN berhasil diubah"))
            } else {
                _uiState.update {
                    it.copy(
                        busy = false,
                        oldPin = "",
                        pinChangeStep = PinChangeStep.OLD_PIN,
                        pinError = "PIN lama salah",
                    )
                }
            }
        }
    }

    // ---------- Backup / Restore ----------

    /** Dipanggil setelah user memilih tujuan file (SAF ACTION_CREATE_DOCUMENT). */
    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true) }
            try {
                val json = withContext(Dispatchers.IO) {
                    val transactions = transactionRepository.getAll()
                    BackupSerializer.toJson(transactions)
                }
                withContext(Dispatchers.IO) {
                    appContext.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: throw IllegalStateException("Tidak bisa membuka file tujuan")
                }
                _events.emit(SettingsEvent.ShowMessage("Backup tersimpan"))
            } catch (e: Exception) {
                _events.emit(SettingsEvent.ShowMessage("Gagal backup: ${e.message}"))
            } finally {
                _uiState.update { it.copy(busy = false) }
            }
        }
    }

    /** Dipanggil setelah user memilih file backup (SAF ACTION_OPEN_DOCUMENT). */
    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true) }
            try {
                val json = withContext(Dispatchers.IO) {
                    appContext.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.readBytes().toString(Charsets.UTF_8)
                    } ?: throw IllegalStateException("Tidak bisa membaca file")
                }
                val transactions = withContext(Dispatchers.Default) {
                    BackupSerializer.fromJson(json)
                }
                withContext(Dispatchers.IO) {
                    transactionRepository.replaceAll(transactions)
                }
                _events.emit(
                    SettingsEvent.ShowMessage("Restore berhasil — ${transactions.size} transaksi dipulihkan"),
                )
            } catch (e: BackupFormatException) {
                _events.emit(SettingsEvent.ShowMessage(e.message ?: "File backup tidak valid"))
            } catch (e: Exception) {
                _events.emit(SettingsEvent.ShowMessage("Gagal restore: ${e.message}"))
            } finally {
                _uiState.update { it.copy(busy = false) }
            }
        }
    }

    companion object {
        const val PIN_LENGTH = 4
    }
}
