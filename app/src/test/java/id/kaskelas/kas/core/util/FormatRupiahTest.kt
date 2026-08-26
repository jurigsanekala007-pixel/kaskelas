package id.kaskelas.kas.core.util

import id.kaskelas.kas.formatRupiah
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatRupiahTest {

    @Test
    fun `nol menghasilkan Rp 0`() {
        assertEquals("Rp 0", formatRupiah(0))
    }

    @Test
    fun `angka positif tanpa pemisah`() {
        assertEquals("Rp 999", formatRupiah(999))
    }

    @Test
    fun `angka tepat ribuan`() {
        assertEquals("Rp 1.000", formatRupiah(1_000))
    }

    @Test
    fun `angka dengan ribuan dan ratusan`() {
        assertEquals("Rp 1.250.000", formatRupiah(1_250_000))
    }

    @Test
    fun `angka besar`() {
        assertEquals("Rp 99.999.999", formatRupiah(99_999_999))
    }

    @Test
    fun `angka negatif`() {
        assertEquals("-Rp 5.000", formatRupiah(-5_000))
    }

    @Test
    fun `angka negatif kecil`() {
        assertEquals("-Rp 1", formatRupiah(-1))
    }

    @Test
    fun `Long MIN_VALUE tidak crash`() {
        val result = formatRupiah(Long.MIN_VALUE)
        assert(result.startsWith("-Rp"))
    }

    @Test
    fun `Long MAX_VALUE tidak crash`() {
        val result = formatRupiah(Long.MAX_VALUE)
        assert(result.startsWith("Rp"))
    }
}
