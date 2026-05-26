package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers WHERE storeId = :storeId ORDER BY name ASC")
    fun getAllByStore(storeId: String): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: String): SupplierEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: SupplierEntity)

    @Update
    suspend fun update(supplier: SupplierEntity)

    @Query("DELETE FROM suppliers WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM suppliers WHERE isSynced = 0")
    suspend fun getUnsynced(): List<SupplierEntity>

    @Query("UPDATE suppliers SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
