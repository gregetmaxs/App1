package com.kasirku.admin.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.admin.data.entity.License

@Dao
interface LicenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(license: License)

    @Update
    suspend fun update(license: License)

    @Delete
    suspend fun delete(license: License)

    @Query("SELECT * FROM licenses ORDER BY createdAt DESC")
    fun getAllLicenses(): LiveData<List<License>>

    @Query("SELECT * FROM licenses ORDER BY createdAt DESC")
    suspend fun getAllLicensesList(): List<License>

    @Query("SELECT * FROM licenses WHERE id = :id")
    suspend fun getLicenseById(id: String): License?

    @Query("SELECT * FROM licenses WHERE licenseKey = :key")
    suspend fun getLicenseByKey(key: String): License?

    @Query("SELECT * FROM licenses WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveLicenses(): LiveData<List<License>>

    @Query("SELECT COUNT(*) FROM licenses")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM licenses WHERE isActive = 1")
    suspend fun getActiveCount(): Int

    @Query("SELECT SUM(price) FROM licenses")
    suspend fun getTotalRevenue(): Double?

    @Query("SELECT SUM(price) FROM licenses WHERE createdAt >= :startDate AND createdAt <= :endDate")
    suspend fun getRevenueByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT COUNT(*) FROM licenses WHERE createdAt >= :startDate AND createdAt <= :endDate")
    suspend fun getCountByDateRange(startDate: Long, endDate: Long): Int

    @Query("SELECT * FROM licenses WHERE isSynced = 0")
    suspend fun getUnsyncedLicenses(): List<License>

    @Query("UPDATE licenses SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM licenses WHERE storeName LIKE '%' || :query || '%' OR ownerName LIKE '%' || :query || '%' OR licenseKey LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchLicenses(query: String): LiveData<List<License>>
}
