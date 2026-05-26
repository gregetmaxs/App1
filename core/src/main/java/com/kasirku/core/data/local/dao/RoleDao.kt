package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.RoleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Query("SELECT * FROM roles WHERE storeId = :storeId ORDER BY name ASC")
    fun getAllByStore(storeId: String): Flow<List<RoleEntity>>

    @Query("SELECT * FROM roles WHERE id = :id")
    suspend fun getById(id: String): RoleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: RoleEntity)

    @Update
    suspend fun update(role: RoleEntity)

    @Query("DELETE FROM roles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM roles WHERE isSynced = 0")
    suspend fun getUnsynced(): List<RoleEntity>

    @Query("UPDATE roles SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
