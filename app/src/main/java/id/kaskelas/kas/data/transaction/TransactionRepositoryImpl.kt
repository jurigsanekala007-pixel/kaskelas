package id.kaskelas.kas.data.transaction

import id.kaskelas.kas.domain.repository.TransactionRepository
import id.kaskelas.kas.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
) : TransactionRepository {

    override fun observeAll(): Flow<List<Transaction>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFrom(fromIsoDate: String?): Flow<List<Transaction>> =
        dao.observeFrom(fromIsoDate).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Transaction? = dao.getById(id)?.toDomain()

    override suspend fun add(transaction: Transaction): Long =
        dao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) =
        dao.update(transaction.toEntity())

    override suspend fun delete(transaction: Transaction) =
        dao.delete(transaction.toEntity())

    /** Dipakai restore: hapus semua lalu isi ulang dari backup. */
    override suspend fun replaceAll(transactions: List<Transaction>) {
        dao.deleteAll()
        dao.insertAll(transactions.map { it.toEntity() })
    }

    override suspend fun getAll(): List<Transaction> =
        dao.observeAll().first().map { it.toDomain() }
}
