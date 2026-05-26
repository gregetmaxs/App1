package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.PriceHistory

@Dao
interface PriceHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(priceHistory: PriceHistory)

    @Query("SELECT * FROM price_history WHERE itemId = :itemId ORDER BY createdAt DESC")
    fun getPriceHistoryByItem(itemId: String): LiveData<List<PriceHistory>>

    @Query("SELECT * FROM price_history WHERE variantId = :variantId ORDER BY createdAt DESC")
    fun getPriceHistoryByVariant(variantId: String): LiveData<List<PriceHistory>>

    @Query("SELECT * FROM price_history ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentPriceChanges(limit: Int = 50): LiveData<List<PriceHistory>>

    @Query("SELECT * FROM price_history WHERE isSynced = 0")
    suspend fun getUnsyncedHistory(): List<PriceHistory>

    @Query("UPDATE price_history SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
