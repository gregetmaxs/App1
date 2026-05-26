package com.kasirku.core.repository

import com.kasirku.core.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun registerEmployee(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        nik: String,
        address: String,
        roleId: String,
        storeId: String
    ): Result<User>
}
