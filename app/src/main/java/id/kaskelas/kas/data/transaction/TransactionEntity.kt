package id.kaskelas.kas.data.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** "MASUK" | "KELUAR" */
    val type: String,
    /** Selalu positif; arah ditentukan [type]. */
    val amount: Long,
    val category: String,
    /** ISO-8601, mis. 2026-08-26 */
    val date: LocalDate,
    val note: String,
)
