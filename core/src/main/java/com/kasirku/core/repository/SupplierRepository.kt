package com.kasirku.core.repository

import com.kasirku.core.model.Supplier
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {
    fun getAllByStore(storeId: String): Flow<List<Supplier>>
    suspend fun getById(id: String): Supplier?
    suspend fun create(supplier: Supplier): Result<Supplier>
    suspend fun update(supplier: Supplier): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
