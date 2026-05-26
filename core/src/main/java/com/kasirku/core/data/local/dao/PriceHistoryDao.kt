package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kasirku.core.data.local.entity.PriceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY createdAt DESC")
    fun getByProductId(productId: String): Flow<List<PriceHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(priceHistory: PriceHistoryEntity)

    @Query("SELECT * FROM price_history WHERE isSynced = 0")
    suspend fun getUnsynced(): List<PriceHistoryEntity>

    @Query("UPDATE price_history SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
