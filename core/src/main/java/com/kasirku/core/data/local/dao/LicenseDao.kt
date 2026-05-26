package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.LicenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LicenseDao {
    @Query("SELECT * FROM licenses ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LicenseEntity>>

    @Query("SELECT * FROM licenses WHERE storeId = :storeId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getByStoreId(storeId: String): LicenseEntity?

    @Query("SELECT * FROM licenses WHERE storeId = :storeId ORDER BY createdAt DESC LIMIT 1")
    fun observeByStoreId(storeId: String): Flow<LicenseEntity?>

    @Query("SELECT * FROM licenses WHERE id = :id")
    suspend fun getById(id: String): LicenseEntity?

    @Query("SELECT * FROM licenses WHERE licenseKey = :key LIMIT 1")
    suspend fun getByKey(key: String): LicenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(license: LicenseEntity)

    @Update
    suspend fun update(license: LicenseEntity)

    @Query("SELECT * FROM licenses WHERE isSynced = 0")
    suspend fun getUnsynced(): List<LicenseEntity>

    @Query("UPDATE licenses SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT COUNT(*) FROM licenses WHERE status = :status")
    fun countByStatus(status: String): Flow<Int>
}
