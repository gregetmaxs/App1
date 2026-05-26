package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kasirku.core.data.local.entity.LicensePriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LicensePriceDao {
    @Query("SELECT * FROM license_prices ORDER BY `type` ASC")
    fun getAll(): Flow<List<LicensePriceEntity>>

    @Query("SELECT * FROM license_prices WHERE type = :type")
    suspend fun getByType(type: String): LicensePriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: LicensePriceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<LicensePriceEntity>)

    @Query("SELECT * FROM license_prices WHERE isSynced = 0")
    suspend fun getUnsynced(): List<LicensePriceEntity>

    @Query("UPDATE license_prices SET isSynced = 1 WHERE type = :type")
    suspend fun markSynced(type: String)
}
