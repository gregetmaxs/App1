package com.kasirku.core.model

data class Store(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val ownerName: String = "",
    val ownerEmail: String = "",
    val licenseId: String = "",
    val isDeliveryFullMode: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
