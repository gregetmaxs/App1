package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val ownerName: String = "",
    val ownerEmail: String = "",
    val licenseId: String = "",
    val isDeliveryFullMode: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
