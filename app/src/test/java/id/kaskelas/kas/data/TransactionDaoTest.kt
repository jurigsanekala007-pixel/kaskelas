package id.kaskelas.kas.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import id.kaskelas.kas.core.database.KasDatabase
import id.kaskelas.kas.data.transaction.TransactionEntity
import id.kaskelas.kas.data.transaction.TransactionDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class TransactionDaoTest {

    private lateinit var db: KasDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            KasDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.transactionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun entity(
        type: String = "MASUK",
        amount: Long = 10_000,
        date: LocalDate = LocalDate.of(2026, 8, 1),
    ) = TransactionEntity(type = type, amount = amount, category = "Iuran", date = date, note = "n")

    @Test
    fun `insert dan baca kembali dengan LocalDate utuh`() = runTest {
        val id = dao.insert(entity(date = LocalDate.of(2026, 8, 26)))
        val loaded = dao.getById(id)!!
        assertEquals(LocalDate.of(2026, 8, 26), loaded.date)
        assertEquals(10_000L, loaded.amount)
    }

    @Test
    fun `observeAll urut tanggal terbaru dulu`() = runTest {
        dao.insert(entity(date = LocalDate.of(2026, 8, 1)))
        dao.insert(entity(date = LocalDate.of(2026, 8, 20)))
        val all = dao.observeAll().first()
        assertEquals(2, all.size)
        assertEquals(LocalDate.of(2026, 8, 20), all.first().date)
    }

    @Test
    fun `update mengubah nilai`() = runTest {
        val id = dao.insert(entity())
        val e = dao.getById(id)!!.copy(amount = 99_000)
        dao.update(e)
        assertEquals(99_000L, dao.getById(id)!!.amount)
    }

    @Test
    fun `delete menghapus`() = runTest {
        val id = dao.insert(entity())
        val e = dao.getById(id)!!
        dao.delete(e)
        assertEquals(0, dao.count())
    }

    @Test
    fun `replaceAll mengganti seluruh data secara atomic`() = runTest {
        dao.insert(entity(date = LocalDate.of(2026, 8, 1)))
        dao.insert(entity(date = LocalDate.of(2026, 8, 2)))

        val newData = listOf(
            entity(date = LocalDate.of(2026, 9, 1)),
            entity(date = LocalDate.of(2026, 9, 15)),
            entity(date = LocalDate.of(2026, 9, 30)),
        )
        dao.replaceAll(newData)

        val all = dao.observeAll().first()
        assertEquals(3, all.size)
        assertEquals(LocalDate.of(2026, 9, 30), all.first().date)
    }

    @Test
    fun `replaceAll dengan list kosong menghapus semua`() = runTest {
        dao.insert(entity())
        dao.insert(entity())
        dao.replaceAll(emptyList())
        assertEquals(0, dao.count())
    }

    @Test
    fun `observeFrom hanya mengembalikan data dari tanggal tertentu`() = runTest {
        dao.insert(entity(date = LocalDate.of(2026, 7, 15)))
        dao.insert(entity(date = LocalDate.of(2026, 8, 1)))
        dao.insert(entity(date = LocalDate.of(2026, 8, 15)))
        dao.insert(entity(date = LocalDate.of(2026, 9, 1)))

        val result = dao.observeFrom("2026-08-01").first()
        assertEquals(3, result.size)
        // Semua tanggal >= 2026-08-01
        assertTrue(result.all { !it.date.isBefore(LocalDate.of(2026, 8, 1)) })
    }

    @Test
    fun `observeFrom dengan null mengembalikan semua`() = runTest {
        dao.insert(entity(date = LocalDate.of(2026, 7, 1)))
        dao.insert(entity(date = LocalDate.of(2026, 8, 1)))

        val result = dao.observeFrom(null).first()
        assertEquals(2, result.size)
    }
}
