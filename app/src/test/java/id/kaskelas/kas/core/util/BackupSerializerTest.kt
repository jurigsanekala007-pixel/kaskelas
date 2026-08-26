package id.kaskelas.kas.core.util

import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class BackupSerializerTest {

    private fun tx(
        id: Long = 1L,
        type: TransactionType = TransactionType.MASUK,
        amount: Long = 50_000,
        category: String = "Iuran",
        date: LocalDate = LocalDate.of(2026, 8, 26),
        note: String = "Iuran bulanan",
    ) = Transaction(id = id, type = type, amount = amount, category = category, date = date, note = note)

    @Test
    fun `toJson menghasilkan JSON dengan structure benar`() {
        val json = BackupSerializer.toJson(listOf(tx()))
        assertTrue(json.contains("\"schemaVersion\": 1"))
        assertTrue(json.contains("\"exportedAt\":"))
        assertTrue(json.contains("\"transactions\":"))
        assertTrue(json.contains("\"type\": \"MASUK\""))
        assertTrue(json.contains("\"amount\": 50000"))
        assertTrue(json.contains("\"category\": \"Iuran\""))
        assertTrue(json.contains("\"date\": \"2026-08-26\""))
    }

    @Test
    fun `roundtrip toJson lalu fromJson menghasilkan data sama`() {
        val original = listOf(
            tx(id = 1, type = TransactionType.MASUK, amount = 100_000, date = LocalDate.of(2026, 8, 1)),
            tx(id = 2, type = TransactionType.KELUAR, amount = 25_000, category = "Snack", date = LocalDate.of(2026, 8, 15)),
        )
        val json = BackupSerializer.toJson(original)
        val restored = BackupSerializer.fromJson(json)
        assertEquals(2, restored.size)
        assertEquals(original[0].id, restored[0].id)
        assertEquals(original[0].type, restored[0].type)
        assertEquals(original[0].amount, restored[0].amount)
        assertEquals(original[0].date, restored[0].date)
        assertEquals(original[1].category, restored[1].category)
    }

    @Test(expected = BackupFormatException::class)
    fun `fromJson menolak string bukan JSON`() {
        BackupSerializer.fromJson("bukan json")
    }

    @Test(expected = BackupFormatException::class)
    fun `fromJson menolak schemaVersion lebih baru`() {
        val json = """
            {
                "schemaVersion": 99,
                "exportedAt": "2026-08-26",
                "transactions": []
            }
        """.trimIndent()
        BackupSerializer.fromJson(json)
    }

    @Test(expected = BackupFormatException::class)
    fun `fromJson menolak type transaksi tidak valid`() {
        val json = """
            {
                "schemaVersion": 1,
                "exportedAt": "2026-08-26",
                "transactions": [{"id":1,"type":"JENIS_SALAH","amount":10000,"date":"2026-08-26"}]
            }
        """.trimIndent()
        BackupSerializer.fromJson(json)
    }

    @Test(expected = BackupFormatException::class)
    fun `fromJson menolak tanggal tidak valid`() {
        val json = """
            {
                "schemaVersion": 1,
                "exportedAt": "2026-08-26",
                "transactions": [{"id":1,"type":"MASUK","amount":10000,"date":"bukan-tanggal"}]
            }
        """.trimIndent()
        BackupSerializer.fromJson(json)
    }

    @Test
    fun `fromJson menangani field optional category dan note kosong`() {
        val json = """
            {
                "schemaVersion": 1,
                "exportedAt": "2026-08-26",
                "transactions": [{"id":1,"type":"MASUK","amount":10000,"date":"2026-08-26"}]
            }
        """.trimIndent()
        val result = BackupSerializer.fromJson(json)
        assertEquals(1, result.size)
        assertEquals("Lainnya", result[0].category)
        assertEquals("", result[0].note)
    }

    @Test
    fun `fromJson menolak struktur tidak dikenal`() {
        val json = """
            {
                "data": [1, 2, 3]
            }
        """.trimIndent()
        assertThrows(BackupFormatException::class.java) {
            BackupSerializer.fromJson(json)
        }
    }
}
