package id.kaskelas.kas.domain.repository

import kotlinx.coroutines.flow.Flow

interface CategoryItem {
    val id: Long
    val name: String
    val type: String
    val isDefault: Boolean
}

interface CategoryRepository {
    fun observeByType(type: String): Flow<List<CategoryItem>>
    suspend fun getAllByType(type: String): List<CategoryItem>
    suspend fun add(name: String, type: String): Long
    suspend fun delete(category: CategoryItem)
    suspend fun exists(name: String, type: String): Boolean
    suspend fun countByType(type: String): Int
}
