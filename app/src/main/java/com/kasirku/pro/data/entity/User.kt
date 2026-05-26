package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val ktpNumber: String = "",
    val phone: String = "",
    val address: String = "",
    val roleId: String = "role_kasir",
    val roleName: String = "Kasir",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
