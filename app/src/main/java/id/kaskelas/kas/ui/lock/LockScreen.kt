package id.kaskelas.kas.ui.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import id.kaskelas.kas.ui.theme.KasSpacing

/**
 * Gerbang pertama app: setup PIN + pertanyaan keamanan (first run),
 * verify PIN (run berikutnya), atau reset PIN via pertanyaan keamanan.
 */
@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    viewModel: LockViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.unlocked) {
        if (state.unlocked) onUnlocked()
    }

    when (state.step) {
        LockStep.LOADING -> FullScreenLoading()
        LockStep.SETUP_SECURITY_QUESTION -> SecurityQuestionSetup(state, viewModel)
        LockStep.RESET_VERIFY_QUESTION -> ForgotPinLayout(state, viewModel)
        else -> PinEntryLayout(state, viewModel)
    }
}

@Composable
private fun FullScreenLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

private fun titleFor(step: LockStep) = when (step) {
    LockStep.SETUP_PIN -> "Buat PIN"
    LockStep.CONFIRM_PIN -> "Konfirmasi PIN"
    LockStep.VERIFY -> "Masukkan PIN"
    LockStep.RESET_NEW_PIN -> "PIN Baru"
    LockStep.RESET_CONFIRM_PIN -> "Konfirmasi PIN Baru"
    else -> ""
}

private fun subtitleFor(step: LockStep) = when (step) {
    LockStep.SETUP_PIN -> "Buat PIN 4 digit untuk membuka aplikasi"
    LockStep.CONFIRM_PIN -> "Ketik ulang PIN yang tadi dibuat"
    LockStep.VERIFY -> "Ketik PIN untuk membuka aplikasi"
    LockStep.RESET_NEW_PIN -> "Buat PIN 4 digit baru"
    LockStep.RESET_CONFIRM_PIN -> "Ketik ulang PIN baru"
    else -> ""
}

@Composable
private fun PinEntryLayout(state: LockUiState, viewModel: LockViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KasSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.6f))
        Text(
            text = "Kas Kelas",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(KasSpacing.xl))
        Text(titleFor(state.step), style = MaterialTheme.typography.titleLarge)
        Text(subtitleFor(state.step), style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(KasSpacing.lg))
        PinDots(filled = state.pin.length)
        state.message?.let {
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        PinKeypad(
            onDigit = viewModel::onDigit,
            onDelete = viewModel::onDelete,
            modifier = Modifier.padding(bottom = KasSpacing.sm),
        )
        if (state.showForgotPin && state.step == LockStep.VERIFY) {
            OutlinedButton(onClick = viewModel::onForgotPin) { Text("Lupa PIN?") }
            Spacer(modifier = Modifier.height(KasSpacing.md))
        }
    }
}

@Composable
private fun SecurityQuestionSetup(state: LockUiState, viewModel: LockViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KasSpacing.lg),
    ) {
        Text("Atur Pertanyaan Keamanan", style = MaterialTheme.typography.titleLarge)
        Text(
            "Pertanyaan ini dipakai kalau kamu lupa PIN.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(KasSpacing.md))
        SECURITY_QUESTIONS.forEachIndexed { index, question ->
            FilterChip(
                selected = state.selectedQuestionIndex == index,
                onClick = { viewModel.onSelectQuestion(index) },
                label = { Text(question) },
                modifier = Modifier.padding(vertical = KasSpacing.xs),
            )
        }
        if (state.selectedQuestionIndex == CUSTOM_QUESTION_INDEX) {
            Spacer(modifier = Modifier.height(KasSpacing.sm))
            OutlinedTextField(
                value = state.customQuestion,
                onValueChange = viewModel::onCustomQuestionChange,
                label = { Text("Tulis pertanyaanmu") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(KasSpacing.md))
        OutlinedTextField(
            value = state.securityAnswer,
            onValueChange = viewModel::onAnswerChange,
            label = { Text("Jawaban") },
            isError = state.answerError != null,
            supportingText = { state.answerError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(KasSpacing.lg))
        Button(onClick = viewModel::onSaveSecurityQuestion, modifier = Modifier.fillMaxWidth()) {
            Text("Selesai")
        }
    }
}

@Composable
private fun ForgotPinLayout(state: LockUiState, viewModel: LockViewModel) {
    // Pertanyaan dimuat saat masuk step ini; tampilkan setelah tersedia.
    val question = state.securityQuestion
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KasSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            text = "Reset PIN",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(KasSpacing.md))
        if (question == null) {
            CircularProgressIndicator()
        } else {
            Text(question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(KasSpacing.md))
            OutlinedTextField(
                value = state.securityAnswer,
                onValueChange = viewModel::onAnswerChange,
                label = { Text("Jawabanmu") },
                isError = state.message != null,
                supportingText = { state.message?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(KasSpacing.lg))
            Button(
                onClick = viewModel::onSubmitSecurityAnswer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Periksa Jawaban")
            }
        }
    }
}
