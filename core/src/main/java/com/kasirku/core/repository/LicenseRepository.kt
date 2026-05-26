package com.kasirku.core.repository

import com.kasirku.core.model.License
import com.kasirku.core.model.LicensePrice
import kotlinx.coroutines.flow.Flow

interface LicenseRepository {
    fun getAllLicenses(): Flow<List<License>>
    suspend fun getLicenseByStoreId(storeId: String): License?
    fun observeLicenseByStoreId(storeId: String): Flow<License?>
    suspend fun generateLicense(storeId: String, type: String, createdBy: String, price: Long): Result<License>
    suspend fun validateLicense(storeId: String): Boolean
    suspend fun revokeLicense(licenseId: String): Result<Unit>

    fun getAllPrices(): Flow<List<LicensePrice>>
    suspend fun updatePrice(price: LicensePrice): Result<Unit>
    suspend fun initDefaultPrices()

    fun countActiveFlow(): Flow<Int>
    fun countExpiredFlow(): Flow<Int>

    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
