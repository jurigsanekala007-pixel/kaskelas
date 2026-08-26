package id.kaskelas.kas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.kaskelas.kas.data.settings.SecurityQuestionEntity
import id.kaskelas.kas.data.transaction.TransactionDao
import id.kaskelas.kas.data.transaction.TransactionEntity

/**
 * Seed kosong sesuai keputusan desain — data pertama berasal dari user.
 * Versi 1: transactions + security_questions.
 */
@Database(
    entities = [
        TransactionEntity::class,
        SecurityQuestionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KasDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "kas_kelas.db"

        // Migrasi berikutnya: val MIGRATION_1_2 = object : Migration(1, 2) { ... }
        val ALL_MIGRATIONS = arrayOf<androidx.room.migration.Migration>()
    }
}
