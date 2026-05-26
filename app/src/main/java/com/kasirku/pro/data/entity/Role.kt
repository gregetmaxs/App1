package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "roles")
data class Role(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val canAccessPos: Boolean = false,
    val canAccessItemManagement: Boolean = false,
    val canAccessPurchase: Boolean = false,
    val canAccessReport: Boolean = false,
    val canAccessCustomer: Boolean = false,
    val canAccessEmployee: Boolean = false,
    val canAccessDelivery: Boolean = false,
    val canTakeDeliveryPhoto: Boolean = false,
    val canAccessSettings: Boolean = false,
    val canManageRoles: Boolean = false,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) {
    companion object {
        fun createDefaultRoles(): List<Role> = listOf(
            Role(
                id = "role_admin",
                name = "Admin/Owner",
                canAccessPos = true,
                canAccessItemManagement = true,
                canAccessPurchase = true,
                canAccessReport = true,
                canAccessCustomer = true,
                canAccessEmployee = true,
                canAccessDelivery = true,
                canTakeDeliveryPhoto = true,
                canAccessSettings = true,
                canManageRoles = true,
                isDefault = true
            ),
            Role(
                id = "role_kasir",
                name = "Kasir",
                canAccessPos = true,
                canAccessCustomer = true,
                canAccessDelivery = true,
                isDefault = true
            ),
            Role(
                id = "role_gudang",
                name = "Gudang",
                canAccessItemManagement = true,
                canAccessPurchase = true,
                isDefault = true
            ),
            Role(
                id = "role_supir",
                name = "Supir",
                canAccessDelivery = true,
                canTakeDeliveryPhoto = true,
                isDefault = true
            ),
            Role(
                id = "role_kenek",
                name = "Kenek",
                canAccessDelivery = true,
                canTakeDeliveryPhoto = true,
                isDefault = true
            ),
            Role(
                id = "role_viewer",
                name = "Viewer",
                canAccessReport = true,
                isDefault = true
            )
        )
    }
}
