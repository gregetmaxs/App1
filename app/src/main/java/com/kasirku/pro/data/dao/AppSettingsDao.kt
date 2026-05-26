package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.AppSettings

@Dao
interface AppSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: AppSettings)

    @Update
    suspend fun update(settings: AppSettings)

    @Query("SELECT * FROM app_settings WHERE id = 'settings' LIMIT 1")
    fun getSettings(): LiveData<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 'settings' LIMIT 1")
    suspend fun getSettingsSync(): AppSettings?

    @Query("SELECT deliveryEnabled FROM app_settings WHERE id = 'settings' LIMIT 1")
    fun isDeliveryEnabled(): LiveData<Boolean?>
}
