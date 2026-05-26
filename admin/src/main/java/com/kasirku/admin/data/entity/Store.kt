package com.kasirku.admin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "stores")
data class Store(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val licenseKey: String = "",
    val isActive: Boolean = true,
    val firebaseUid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
