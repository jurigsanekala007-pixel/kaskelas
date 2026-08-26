package id.kaskelas.kas.domain.repository

import id.kaskelas.kas.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(): Flow<List<Transaction>>
    fun observeFrom(fromIsoDate: String?): Flow<List<Transaction>>
    suspend fun getById(id: Long): Transaction?
    suspend fun getAll(): List<Transaction>
    suspend fun add(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    /** Ganti seluruh isi tabel (dipakai restore backup). */
    suspend fun replaceAll(transactions: List<Transaction>)
}
