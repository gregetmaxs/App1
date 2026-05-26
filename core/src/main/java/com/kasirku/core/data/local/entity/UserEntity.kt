package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val photoUrl: String = "",
    val phone: String = "",
    val nik: String = "",
    val address: String = "",
    val roleId: String = "",
    val storeId: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
