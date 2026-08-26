package id.kaskelas.kas.domain.repository

import kotlinx.coroutines.flow.Flow

/** Kontrak lock/keamanan — impl di data layer (hash SHA-256 + salt). */
interface LockRepository {
    val isPinSet: Flow<Boolean>

    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun saveSecurityQuestion(question: String, answer: String)
    suspend fun verifySecurityAnswer(answer: String): Boolean
    suspend fun getSecurityQuestion(): String?
    /** Ganti PIN: verifikasi lama dulu, baru simpan yang baru. */
    suspend fun changePin(oldPin: String, newPin: String): Boolean
}
