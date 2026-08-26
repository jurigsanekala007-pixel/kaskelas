package id.kaskelas.kas.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.repository.LockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LockStep {
    LOADING,
    SETUP_PIN,               // first run: buat PIN
    CONFIRM_PIN,             // first run: konfirmasi PIN
    SETUP_SECURITY_QUESTION, // first run: atur pertanyaan keamanan
    VERIFY,                  // run berikutnya: verify PIN
    RESET_VERIFY_QUESTION,   // lupa PIN: jawab pertanyaan keamanan
    RESET_NEW_PIN,           // lupa PIN: buat PIN baru
    RESET_CONFIRM_PIN,
}

data class LockUiState(
    val step: LockStep = LockStep.LOADING,
    val pin: String = "",
    val firstPinEntry: String = "",
    val unlocked: Boolean = false,
    val message: String? = null,
    val showForgotPin: Boolean = false,
    val selectedQuestionIndex: Int = 0,
    val customQuestion: String = "",
    val securityAnswer: String = "",
    val answerError: String? = null,
    val lockedUntilMs: Long = 0,
    val failedAttempts: Int = 0,
    val securityQuestion: String? = null,
)

@HiltViewModel
class LockViewModel @Inject constructor(
    private val lockRepository: LockRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val pinSet = lockRepository.isPinSet.first()
            _state.value = if (pinSet) {
                _state.value.copy(step = LockStep.VERIFY, showForgotPin = true)
            } else {
                _state.value.copy(step = LockStep.SETUP_PIN)
            }
        }
    }

    fun onDigit(digit: Char) {
        val s = _state.value
        if (s.pin.length >= PIN_LENGTH || s.lockedUntilMs > System.currentTimeMillis()) return
        val newPin = s.pin + digit
        if (newPin.length < PIN_LENGTH) {
            _state.value = s.copy(pin = newPin, message = null)
            return
        }
        // PIN 4 digit lengkap — proses per step.
        when (s.step) {
            LockStep.SETUP_PIN ->
                _state.value = s.copy(pin = "", firstPinEntry = newPin, step = LockStep.CONFIRM_PIN)
            LockStep.CONFIRM_PIN -> confirmSetup(newPin)
            LockStep.VERIFY -> verify(newPin)
            LockStep.RESET_NEW_PIN ->
                _state.value = s.copy(pin = "", firstPinEntry = newPin, step = LockStep.RESET_CONFIRM_PIN)
            LockStep.RESET_CONFIRM_PIN -> confirmReset(newPin)
            else -> Unit
        }
    }

    fun onDelete() {
        val s = _state.value
        if (s.pin.isNotEmpty()) {
            _state.value = s.copy(pin = s.pin.dropLast(1), message = null)
        }
    }

    fun onForgotPin() {
        viewModelScope.launch {
            val question = lockRepository.getSecurityQuestion()
            _state.value = _state.value.copy(
                step = LockStep.RESET_VERIFY_QUESTION,
                securityQuestion = question,
                message = null,
            )
        }
    }

    fun onSelectQuestion(index: Int) {
        _state.value = _state.value.copy(selectedQuestionIndex = index)
    }

    fun onCustomQuestionChange(text: String) {
        _state.value = _state.value.copy(customQuestion = text)
    }

    fun onAnswerChange(text: String) {
        _state.value = _state.value.copy(securityAnswer = text, answerError = null)
    }

    /** Dipanggil dari layar "Lupa PIN" setelah user submit jawaban pertanyaan keamanan. */
    fun onSubmitSecurityAnswer() {
        viewModelScope.launch {
            val s = _state.value
            val ok = lockRepository.verifySecurityAnswer(s.securityAnswer.trim())
            _state.value = if (ok) {
                s.copy(securityAnswer = "", message = null, step = LockStep.RESET_NEW_PIN)
            } else {
                s.copy(message = "Jawaban salah, coba lagi")
            }
        }
    }

    fun onSaveSecurityQuestion() {
        val s = _state.value
        val question = if (s.selectedQuestionIndex == CUSTOM_QUESTION_INDEX) {
            s.customQuestion.trim()
        } else {
            SECURITY_QUESTIONS[s.selectedQuestionIndex]
        }
        if (question.isEmpty()) {
            _state.value = s.copy(answerError = "Pertanyaan tidak boleh kosong")
            return
        }
        if (s.securityAnswer.isBlank()) {
            _state.value = s.copy(answerError = "Jawaban tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            lockRepository.saveSecurityQuestion(question, s.securityAnswer.trim())
            _state.value = s.copy(unlocked = true)
        }
    }

    private fun confirmSetup(pin: String) {
        val s = _state.value
        if (pin != s.firstPinEntry) {
            _state.value = s.copy(
                pin = "",
                firstPinEntry = "",
                step = LockStep.SETUP_PIN,
                message = "PIN tidak sama, buat ulang",
            )
        } else {
            viewModelScope.launch {
                lockRepository.savePin(pin)
                _state.value = s.copy(pin = "", step = LockStep.SETUP_SECURITY_QUESTION)
            }
        }
    }

    private fun verify(pin: String) {
        viewModelScope.launch {
            val s = _state.value
            if (s.lockedUntilMs > System.currentTimeMillis()) return@launch
            val ok = lockRepository.verifyPin(pin)
            if (ok) {
                _state.value = s.copy(pin = "", unlocked = true, failedAttempts = 0, message = null)
            } else {
                val attempts = s.failedAttempts + 1
                // Anti brute-force ringan: tiap 3x salah → tunggu 5 detik.
                val mustWait = attempts % MAX_ATTEMPTS_BEFORE_DELAY == 0
                _state.value = s.copy(
                    pin = "",
                    failedAttempts = attempts,
                    message = "PIN salah" + if (mustWait) ", tunggu sebentar…" else "",
                    lockedUntilMs = if (mustWait) System.currentTimeMillis() + DELAY_MS else 0L,
                )
                if (mustWait) {
                    delay(DELAY_MS)
                    _state.value = _state.value.copy(message = null)
                }
            }
        }
    }

    private fun confirmReset(pin: String) {
        val s = _state.value
        if (pin != s.firstPinEntry) {
            _state.value = s.copy(
                pin = "",
                firstPinEntry = "",
                step = LockStep.RESET_NEW_PIN,
                message = "PIN tidak sama, buat ulang",
            )
        } else {
            viewModelScope.launch {
                lockRepository.savePin(pin)
                _state.value = s.copy(pin = "", unlocked = true, failedAttempts = 0)
            }
        }
    }

    companion object {
        const val PIN_LENGTH = 4
        private const val MAX_ATTEMPTS_BEFORE_DELAY = 3
        private const val DELAY_MS = 5_000L
    }
}
