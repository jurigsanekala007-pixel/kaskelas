package id.kaskelas.kas.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabel single-row (id selalu 1) untuk pertanyaan keamanan.
 * Pertanyaan plaintext; jawaban di-hash di LockRepositoryImpl (DataStore),
 * entitas ini hanya arsip agar ikut ter-backup.
 */
@Entity(tableName = "security_questions")
data class SecurityQuestionEntity(
    @PrimaryKey val id: Int = 1,
    val question: String,
    /** hash ber-salt "salt:hash" */
    val answerHash: String,
)
