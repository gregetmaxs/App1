package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "license_prices")
data class LicensePriceEntity(
    @PrimaryKey val type: String = "",  // 3_DAYS, 7_DAYS, etc.
    val label: String = "",
    val price: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
