package com.kasirku.core.repository

import com.kasirku.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllByStore(storeId: String): Flow<List<Category>>
    suspend fun getById(id: String): Category?
    suspend fun create(category: Category): Result<Category>
    suspend fun update(category: Category): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
