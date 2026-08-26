package id.kaskelas.kas.domain.model

import java.time.LocalDate

/**
 * Domain model transaksi kas — bebas dari anotasi Room/persistensi.
 * Nominal selalu positif; arah masuk/keluar ditentukan [type].
 */
data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Long,
    val category: String,
    val date: LocalDate,
    val note: String,
)
