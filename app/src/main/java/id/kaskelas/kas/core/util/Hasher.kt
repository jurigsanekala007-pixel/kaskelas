package id.kaskelas.kas.core.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hash util untuk PIN & jawaban keamanan. Bukan kriptografi tingkat tinggi —
 * cukup untuk app offline lokal: plaintext tidak pernah disimpan.
 * Salt acak disimpan di samping hash (format "salt:hash").
 */
@Singleton
class Hasher @Inject constructor() {

    fun hash(plain: String): String {
        val salt = randomSalt()
        return "$salt:${sha256(salt + plain)}"
    }

    fun verify(plain: String, stored: String): Boolean {
        if (stored.isBlank() || !stored.contains(':')) return false
        val (salt, expected) = stored.split(':', limit = 2)
        return constantTimeEquals(sha256(salt + plain), expected)
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun randomSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].code xor b[i].code)
        return result == 0
    }

    private companion object {
        const val SALT_BYTES = 16
    }
}
