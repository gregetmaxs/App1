package com.kasirku.core.repository

import com.kasirku.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllByStore(storeId: String): Flow<List<User>>
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User, password: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
