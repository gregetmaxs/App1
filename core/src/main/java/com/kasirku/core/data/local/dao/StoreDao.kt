package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY name ASC")
    fun getAll(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM stores WHERE id = :id")
    suspend fun getById(id: String): StoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: StoreEntity)

    @Update
    suspend fun update(store: StoreEntity)

    @Query("SELECT * FROM stores WHERE isSynced = 0")
    suspend fun getUnsynced(): List<StoreEntity>

    @Query("UPDATE stores SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
