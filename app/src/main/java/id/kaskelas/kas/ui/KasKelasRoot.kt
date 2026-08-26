package id.kaskelas.kas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import id.kaskelas.kas.domain.repository.LockRepository
import id.kaskelas.kas.ui.navigation.KasNavGraph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LockGateViewModel @Inject constructor(
    lockRepository: LockRepository,
) : ViewModel() {
    /** true = PIN sudah pernah dibuat, langsung ke verify */
    val isPinSet = lockRepository.isPinSet
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}

/**
 * Root composable: menunggu status PIN terbaca, lalu buka nav graph.
 * Nav graph startDestination = LOCK; kalau belum ada PIN, LockScreen masuk mode setup.
 */
@Composable
fun KasKelasRoot(viewModel: LockGateViewModel = hiltViewModel()) {
    val isPinSet by viewModel.isPinSet.collectAsState()
    // Tampilkan app hanya setelah status lock diketahui (hindari flicker).
    if (isPinSet != null) {
        KasNavGraph(rememberNavController())
    }
}
