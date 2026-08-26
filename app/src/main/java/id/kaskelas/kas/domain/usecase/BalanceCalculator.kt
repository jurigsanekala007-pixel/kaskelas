package id.kaskelas.kas.domain.usecase

import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType

/**
 * Logika uang murni (pure function) — satu-satunya usecase sesuai desain.
 * Wajib ter-cover unit test.
 */
object BalanceCalculator {

    data class Summary(
        val balance: Long,
        val totalMasuk: Long,
        val totalKeluar: Long,
    )

    /** Saldo keseluruhan dari daftar transaksi (urutan tidak penting). */
    fun balance(transactions: List<Transaction>): Long =
        transactions.fold(0L) { acc, t ->
            when (t.type) {
                TransactionType.MASUK -> acc + t.amount
                TransactionType.KELUAR -> acc - t.amount
            }
        }

    /** Ringkasan dari subset transaksi (mis. satu bulan). */
    fun summary(transactions: List<Transaction>): Summary {
        var masuk = 0L
        var keluar = 0L
        for (t in transactions) {
            when (t.type) {
                TransactionType.MASUK -> masuk += t.amount
                TransactionType.KELUAR -> keluar += t.amount
            }
        }
        return Summary(balance = masuk - keluar, totalMasuk = masuk, totalKeluar = keluar)
    }

    /**
     * Subtotal harian untuk grouping riwayat: map tanggal → saldo bersih hari itu.
     */
    fun dailyNet(transactions: List<Transaction>): Map<java.time.LocalDate, Long> =
        transactions
            .groupBy { it.date }
            .mapValues { (_, dayList) -> balance(dayList) }
}
