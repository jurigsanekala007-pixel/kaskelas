package id.kaskelas.kas.data.category

import id.kaskelas.kas.domain.repository.CategoryItem
import id.kaskelas.kas.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private data class CategoryItemImpl(
    override val id: Long,
    override val name: String,
    override val type: String,
    override val isDefault: Boolean,
) : CategoryItem

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
) : CategoryRepository {

    override fun observeByType(type: String): Flow<List<CategoryItem>> =
        dao.observeByType(type).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllByType(type: String): List<CategoryItem> =
        dao.getAllByType(type).map { it.toDomain() }

    override suspend fun add(name: String, type: String): Long =
        dao.insert(CategoryEntity(name = name, type = type, isDefault = false))

    override suspend fun delete(category: CategoryItem) {
        dao.delete(
            CategoryEntity(
                id = category.id,
                name = category.name,
                type = category.type,
                isDefault = category.isDefault,
            ),
        )
    }

    override suspend fun exists(name: String, type: String): Boolean =
        dao.exists(name, type)

    override suspend fun countByType(type: String): Int =
        dao.countByType(type)

    private fun CategoryEntity.toDomain(): CategoryItem = CategoryItemImpl(
        id = id,
        name = name,
        type = type,
        isDefault = isDefault,
    )
}
