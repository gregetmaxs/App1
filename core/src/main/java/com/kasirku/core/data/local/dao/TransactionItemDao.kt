package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kasirku.core.data.local.entity.TransactionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionItemDao {
    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    fun getByTransactionId(transactionId: String): Flow<List<TransactionItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TransactionItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TransactionItemEntity>)

    @Query("SELECT * FROM transaction_items WHERE isSynced = 0")
    suspend fun getUnsynced(): List<TransactionItemEntity>

    @Query("UPDATE transaction_items SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
