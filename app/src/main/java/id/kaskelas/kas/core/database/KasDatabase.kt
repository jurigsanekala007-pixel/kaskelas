package id.kaskelas.kas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.kaskelas.kas.data.transaction.TransactionDao
import id.kaskelas.kas.data.transaction.TransactionEntity

/**
 * Seed kosong sesuai keputusan desain — data pertama berasal dari user.
 * Security question disimpan di DataStore, bukan Room.
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

        /** Migrasi v1 → v2: drop tabel security_questions (dead code). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS security_questions")
            }
        }

        // Migrasi berikutnya: val MIGRATION_2_3 = object : Migration(2, 3) { ... }
        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
    }
}
