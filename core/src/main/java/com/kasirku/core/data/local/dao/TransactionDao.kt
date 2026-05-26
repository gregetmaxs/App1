package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kasirku.core.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE storeId = :storeId ORDER BY createdAt DESC")
    fun getAllByStore(storeId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE storeId = :storeId ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(storeId: String, limit: Int = 10): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE storeId = :storeId AND createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT SUM(total) FROM transactions WHERE storeId = :storeId AND createdAt BETWEEN :startDate AND :endDate")
    fun getTotalByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Long?>

    @Query("SELECT COUNT(*) FROM transactions WHERE storeId = :storeId AND createdAt BETWEEN :startDate AND :endDate")
    fun getCountByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("UPDATE transactions SET deliveryStatus = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDeliveryStatus(id: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE transactions SET deliveryProofUrl = :proofUrl, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDeliveryProof(id: String, proofUrl: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnsynced(): List<TransactionEntity>

    @Query("UPDATE transactions SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
