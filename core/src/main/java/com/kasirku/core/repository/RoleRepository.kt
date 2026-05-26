package com.kasirku.core.repository

import com.kasirku.core.model.Role
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    fun getAllByStore(storeId: String): Flow<List<Role>>
    suspend fun getById(id: String): Role?
    suspend fun create(role: Role): Result<Role>
    suspend fun update(role: Role): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun initDefaultRoles(storeId: String)
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
