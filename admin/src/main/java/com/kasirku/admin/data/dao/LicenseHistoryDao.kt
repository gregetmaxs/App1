package com.kasirku.admin.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.admin.data.entity.LicenseHistory

@Dao
interface LicenseHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: LicenseHistory)

    @Query("SELECT * FROM license_history WHERE licenseId = :licenseId ORDER BY createdAt DESC")
    fun getHistoryByLicense(licenseId: String): LiveData<List<LicenseHistory>>

    @Query("SELECT * FROM license_history ORDER BY createdAt DESC LIMIT 20")
    fun getRecentHistory(): LiveData<List<LicenseHistory>>
}
