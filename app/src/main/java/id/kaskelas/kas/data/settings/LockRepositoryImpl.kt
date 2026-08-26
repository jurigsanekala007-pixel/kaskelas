package id.kaskelas.kas.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import id.kaskelas.kas.core.util.Hasher
import id.kaskelas.kas.domain.repository.LockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.lockDataStore: DataStore<Preferences> by preferencesDataStore(name = "lock")

/**
 * Penyimpanan lock: PIN & jawaban keamanan disimpan sebagai hash ber-salt,
 * pertanyaan sebagai plaintext (tidak rahasia).
 */
@Singleton
class LockRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hasher: Hasher,
) : LockRepository {

    private object Keys {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val SECURITY_QUESTION = stringPreferencesKey("security_question")
        val SECURITY_ANSWER_HASH = stringPreferencesKey("security_answer_hash")
    }

    override val isPinSet: Flow<Boolean> = context.lockDataStore.data.map { prefs ->
        !prefs[Keys.PIN_HASH].isNullOrBlank()
    }

    override suspend fun savePin(pin: String) {
        context.lockDataStore.edit { prefs ->
            prefs[Keys.PIN_HASH] = hasher.hash(pin)
        }
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val stored = context.lockDataStore.data.first()[Keys.PIN_HASH] ?: return false
        return hasher.verify(pin, stored)
    }

    override suspend fun saveSecurityQuestion(question: String, answer: String) {
        context.lockDataStore.edit { prefs ->
            prefs[Keys.SECURITY_QUESTION] = question
            prefs[Keys.SECURITY_ANSWER_HASH] = hasher.hash(answer.lowercase().trim())
        }
    }

    override suspend fun verifySecurityAnswer(answer: String): Boolean {
        val stored = context.lockDataStore.data.first()[Keys.SECURITY_ANSWER_HASH] ?: return false
        return hasher.verify(answer.lowercase().trim(), stored)
    }

    override suspend fun getSecurityQuestion(): String? {
        return context.lockDataStore.data.first()[Keys.SECURITY_QUESTION]
    }

    override suspend fun changePin(oldPin: String, newPin: String): Boolean {
        if (!verifyPin(oldPin)) return false
        savePin(newPin)
        return true
    }
}
