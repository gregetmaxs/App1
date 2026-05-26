package com.kasirku.core.model

data class Role(
    val id: String = "",
    val name: String = "",
    val storeId: String = "",
    val isDefault: Boolean = false,
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
    val canManageRoles: Boolean = false
)
