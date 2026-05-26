package com.kasirku.core.model

data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val photoUrl: String = "",
    val phone: String = "",
    val nik: String = "",
    val address: String = "",
    val roleId: String = "",
    val storeId: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
