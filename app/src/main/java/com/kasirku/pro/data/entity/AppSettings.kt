package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: String = "settings",
    val storeName: String = "KasirKu Pro",
    val storeAddress: String = "",
    val storePhone: String = "",
    val deliveryEnabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
