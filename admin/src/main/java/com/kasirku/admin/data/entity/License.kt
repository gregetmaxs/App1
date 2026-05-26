package com.kasirku.admin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "licenses")
data class License(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val licenseKey: String = "",
    val storeId: String = "",
    val storeName: String = "",
    val ownerName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = "",
    val isActive: Boolean = true,
    val durationDays: Long = -1L,
    val price: Double = 0.0,
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
