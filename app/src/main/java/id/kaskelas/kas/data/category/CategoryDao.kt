package id.kaskelas.kas.data.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY isDefault DESC, id ASC")
    fun observeByType(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY isDefault DESC, id ASC")
    suspend fun getAllByType(type: String): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<CategoryEntity>)

    @Delete
    suspend fun delete(entity: CategoryEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE name = :name AND type = :type)")
    suspend fun exists(name: String, type: String): Boolean

    @Query("SELECT COUNT(*) FROM categories WHERE type = :type")
    suspend fun countByType(type: String): Int
}
