package id.kaskelas.kas.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.kaskelas.kas.data.category.CategoryDao
import id.kaskelas.kas.data.category.CategoryEntity
import id.kaskelas.kas.data.transaction.TransactionDao
import id.kaskelas.kas.data.transaction.TransactionEntity

/**
 * Seed kosong untuk transaksi — data pertama berasal dari user.
 * Kategori di-seed via migration (default) dan bisa dikelola user.
 */
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KasDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val NAME = "kas_kelas.db"

        /** Migrasi v1 → v2: drop tabel security_questions (dead code). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS security_questions")
            }
        }

        /** Migrasi v2 → v3: buat tabel categories + seed default kategori. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        isDefault INTEGER NOT NULL DEFAULT 0
                    )
                    """,
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_categories_name_type ON categories (name, type)",
                )

                // Seed default kategori pemasukan
                val defaults = listOf(
                    "('Iuran', 'MASUK', 1)",
                    "('Acara Kelas', 'MASUK', 1)",
                    "('Donasi', 'MASUK', 1)",
                    "('Lainnya', 'MASUK', 1)",
                    // Default kategori pengeluaran
                    "('Snack', 'KELUAR', 1)",
                    "('Perlengkapan', 'KELUAR', 1)",
                    "('Konsumsi', 'KELUAR', 1)",
                    "('Lainnya', 'KELUAR', 1)",
                )
                defaults.forEach { values ->
                    db.execSQL("INSERT OR IGNORE INTO categories (name, type, isDefault) VALUES $values")
                }
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
    }
}
