package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "licenses")
data class LicenseEntity(
    @PrimaryKey val id: String = "",
    val licenseKey: String = "",
    val storeId: String = "",
    val type: String = "",       // 3_DAYS, 7_DAYS, 14_DAYS, 30_DAYS, 1_YEAR, 2_YEARS, PERMANENT
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, REVOKED
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L,    // 0 = permanent
    val createdBy: String = "",  // owner user id
    val price: Long = 0L,        // harga dalam rupiah
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
