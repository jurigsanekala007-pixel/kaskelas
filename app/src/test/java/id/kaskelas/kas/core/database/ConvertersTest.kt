package id.kaskelas.kas.core.database

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `roundtrip LocalDate 2026-08-26`() {
        val date = LocalDate.of(2026, 8, 26)
        val stored = converters.fromLocalDate(date)
        val restored = converters.toLocalDate(stored)
        assertEquals(date, restored)
    }

    @Test
    fun `roundtrip awal tahun`() {
        val date = LocalDate.of(2026, 1, 1)
        val stored = converters.fromLocalDate(date)
        val restored = converters.toLocalDate(stored)
        assertEquals(date, restored)
    }

    @Test
    fun `roundtrip akhir tahun`() {
        val date = LocalDate.of(2026, 12, 31)
        val stored = converters.fromLocalDate(date)
        val restored = converters.toLocalDate(stored)
        assertEquals(date, restored)
    }
}
