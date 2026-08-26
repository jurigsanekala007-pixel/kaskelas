package id.kaskelas.kas.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.kaskelas.kas.core.database.KasDatabase
import id.kaskelas.kas.data.settings.LockRepositoryImpl
import id.kaskelas.kas.data.transaction.TransactionDao
import id.kaskelas.kas.data.transaction.TransactionRepositoryImpl
import id.kaskelas.kas.domain.repository.LockRepository
import id.kaskelas.kas.domain.repository.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLockRepository(impl: LockRepositoryImpl): LockRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KasDatabase =
        Room.databaseBuilder(context, KasDatabase::class.java, KasDatabase.NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addMigrations(*KasDatabase.ALL_MIGRATIONS)
            .build()

    @Provides
    fun provideTransactionDao(db: KasDatabase): TransactionDao = db.transactionDao()
}
