package id.kaskelas.kas.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HasherTest {

    private val hasher = Hasher()

    @Test
    fun `hash menghasilkan format salt colon hash`() {
        val stored = hasher.hash("1234")
        assertTrue("Harus mengandung ':' delimiter", stored.contains(':'))
        val parts = stored.split(':', limit = 2)
        assertEquals(2, parts.size)
        assertTrue("Salt harus hex string 32 karakter (16 bytes)", parts[0].length == 32)
        assertTrue("Hash harus hex string 64 karakter (SHA-256)", parts[1].length == 64)
    }

    @Test
    fun `verify benar untuk plain text yang cocok`() {
        val stored = hasher.hash("abcdef")
        assertTrue(hasher.verify("abcdef", stored))
    }

    @Test
    fun `verify gagal untuk plain text yang salah`() {
        val stored = hasher.hash("1234")
        assertFalse(hasher.verify("9999", stored))
    }

    @Test
    fun `verify gagal untuk stored string kosong`() {
        assertFalse(hasher.verify("1234", ""))
    }

    @Test
    fun `verify gagal untuk stored string tanpa delimiter`() {
        assertFalse(hasher.verify("1234", "nodelimiterhere"))
    }

    @Test
    fun `hash dua kali menghasilkan salt berbeda`() {
        val h1 = hasher.hash("1234")
        val h2 = hasher.hash("1234")
        assertNotEquals("Salt harus berbeda setiap hash", h1, h2)
    }
}
