package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val storeId: String = "",
    val isDefault: Boolean = false,
    // Permissions as boolean flags
    val canAccessPos: Boolean = false,
    val canAccessProducts: Boolean = false,
    val canAccessStock: Boolean = false,
    val canAccessTransactions: Boolean = false,
    val canAccessReports: Boolean = false,
    val canAccessEmployees: Boolean = false,
    val canAccessCustomers: Boolean = false,
    val canAccessDelivery: Boolean = false,
    val canTakeDeliveryPhoto: Boolean = false,
    val canAccessSettings: Boolean = false,
    val canManageRoles: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
