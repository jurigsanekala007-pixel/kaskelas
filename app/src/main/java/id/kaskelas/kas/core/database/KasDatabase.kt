package id.kaskelas.kas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.kaskelas.kas.data.transaction.TransactionDao
import id.kaskelas.kas.data.transaction.TransactionEntity

/**
 * Seed kosong sesuai keputusan desain — data pertama berasal dari user.
 * Security question disimpan di DataStore, bukan Room.
 * Versi 2: hapus SecurityQuestionEntity (dead code).
 */
@Database(
    entities = [
        TransactionEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KasDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "kas_kelas.db"

        // Migrasi berikutnya: val MIGRATION_2_3 = object : Migration(2, 3) { ... }
        val ALL_MIGRATIONS = arrayOf<androidx.room.migration.Migration>()
    }
}
