package com.kasirku.core.repository

import com.kasirku.core.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getAllStores(): Flow<List<Store>>
    suspend fun getStoreById(id: String): Store?
    suspend fun createStore(store: Store): Result<Store>
    suspend fun updateStore(store: Store): Result<Unit>
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
