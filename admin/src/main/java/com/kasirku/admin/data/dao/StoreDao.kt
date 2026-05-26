package com.kasirku.admin.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.admin.data.entity.Store

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(store: Store)

    @Update
    suspend fun update(store: Store)

    @Delete
    suspend fun delete(store: Store)

    @Query("SELECT * FROM stores ORDER BY createdAt DESC")
    fun getAllStores(): LiveData<List<Store>>

    @Query("SELECT * FROM stores ORDER BY createdAt DESC")
    suspend fun getAllStoresList(): List<Store>

    @Query("SELECT * FROM stores WHERE id = :id")
    suspend fun getStoreById(id: String): Store?

    @Query("SELECT * FROM stores WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveStores(): LiveData<List<Store>>

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM stores WHERE isActive = 1")
    suspend fun getActiveCount(): Int

    @Query("SELECT * FROM stores WHERE isSynced = 0")
    suspend fun getUnsyncedStores(): List<Store>

    @Query("UPDATE stores SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM stores WHERE name LIKE '%' || :query || '%' OR ownerName LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchStores(query: String): LiveData<List<Store>>
}
