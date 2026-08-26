package id.kaskelas.kas.data

import id.kaskelas.kas.data.transaction.TransactionEntity
import id.kaskelas.kas.data.transaction.toDomain
import id.kaskelas.kas.data.transaction.toEntity
import id.kaskelas.kas.domain.model.Transaction
import id.kaskelas.kas.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TransactionMapperTest {

    private val entity = TransactionEntity(
        id = 42,
        type = "MASUK",
        amount = 100_000,
        category = "Iuran",
        date = LocalDate.of(2026, 8, 26),
        note = "Iuran bulanan",
    )

    private val domain = Transaction(
        id = 42,
        type = TransactionType.MASUK,
        amount = 100_000,
        category = "Iuran",
        date = LocalDate.of(2026, 8, 26),
        note = "Iuran bulanan",
    )

    @Test
    fun `toDomain mengkonversi entity ke domain dengan benar`() {
        val result = entity.toDomain()
        assertEquals(domain.id, result.id)
        assertEquals(domain.type, result.type)
        assertEquals(domain.amount, result.amount)
        assertEquals(domain.category, result.category)
        assertEquals(domain.date, result.date)
        assertEquals(domain.note, result.note)
    }

    @Test
    fun `toEntity mengkonversi domain ke entity dengan benar`() {
        val result = domain.toEntity()
        assertEquals(entity.id, result.id)
        assertEquals(entity.type, result.type)
        assertEquals(entity.amount, result.amount)
        assertEquals(entity.category, result.category)
        assertEquals(entity.date, result.date)
        assertEquals(entity.note, result.note)
    }

    @Test
    fun `roundtrip entity ke domain ke entity menghasilkan data sama`() {
        val roundtripped = entity.toDomain().toEntity()
        assertEquals(entity, roundtripped)
    }

    @Test
    fun `roundtrip domain ke entity ke domain menghasilkan data sama`() {
        val roundtripped = domain.toEntity().toDomain()
        assertEquals(domain, roundtripped)
    }

    @Test
    fun `toDomain menangani tipe KELUAR`() {
        val keluarEntity = entity.copy(type = "KELUAR")
        val result = keluarEntity.toDomain()
        assertEquals(TransactionType.KELUAR, result.type)
    }
}
