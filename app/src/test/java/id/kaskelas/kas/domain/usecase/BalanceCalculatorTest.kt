package id.kaskelas.kas.domain.usecase

import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BalanceCalculatorTest {

    private val d1 = LocalDate.of(2026, 8, 1)
    private val d2 = LocalDate.of(2026, 8, 5)

    private fun t(type: TransactionType, amount: Long, date: LocalDate = d1) = Transaction(
        type = type, amount = amount, category = "x", date = date, note = "",
    )

    @Test
    fun `balance kosong adalah nol`() {
        assertEquals(0L, BalanceCalculator.balance(emptyList()))
    }

    @Test
    fun `masuk menambah keluar mengurangi`() {
        val list = listOf(
            t(TransactionType.MASUK, 100_000),
            t(TransactionType.KELUAR, 30_000),
            t(TransactionType.MASUK, 50_000, d2),
        )
        assertEquals(120_000L, BalanceCalculator.balance(list))
    }

    @Test
    fun `saldo minus jika pengeluaran lebih besar`() {
        val list = listOf(t(TransactionType.KELUAR, 10_000))
        assertEquals(-10_000L, BalanceCalculator.balance(list))
    }

    @Test
    fun `summary memisahkan total masuk dan keluar`() {
        val list = listOf(t(TransactionType.MASUK, 200_000), t(TransactionType.KELUAR, 75_000))
        val s = BalanceCalculator.summary(list)
        assertEquals(200_000L, s.totalMasuk)
        assertEquals(75_000L, s.totalKeluar)
        assertEquals(125_000L, s.balance)
    }

    @Test
    fun `dailyNet mengelompokkan per tanggal`() {
        val list = listOf(
            t(TransactionType.MASUK, 100_000, d1), t(TransactionType.KELUAR, 20_000, d1),
            t(TransactionType.MASUK, 50_000, d2),
        )
        val net = BalanceCalculator.dailyNet(list)
        assertEquals(80_000L, net[d1])
        assertEquals(50_000L, net[d2])
    }
}
