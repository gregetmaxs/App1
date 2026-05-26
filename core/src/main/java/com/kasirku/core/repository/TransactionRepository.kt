package com.kasirku.core.repository

import com.kasirku.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllByStore(storeId: String): Flow<List<Transaction>>
    fun getRecent(storeId: String, limit: Int = 10): Flow<List<Transaction>>
    fun getByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun getById(id: String): Transaction?
    fun getTotalByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Long?>
    fun getCountByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Int>
    suspend fun createTransaction(transaction: Transaction): Result<Transaction>
    suspend fun updateDeliveryStatus(transactionId: String, status: String)
    suspend fun updateDeliveryProof(transactionId: String, proofUrl: String)
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
