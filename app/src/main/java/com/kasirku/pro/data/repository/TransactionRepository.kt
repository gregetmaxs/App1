package com.kasirku.pro.data.repository

import com.kasirku.pro.data.dao.TransactionDao
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.entity.TransactionItem
import com.kasirku.pro.data.entity.TransactionType

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions() = transactionDao.getAllTransactions()
    fun getTransactionsByType(type: TransactionType) = transactionDao.getTransactionsByType(type)
    fun getTransactionsByDateRange(start: Long, end: Long) = transactionDao.getTransactionsByDateRange(start, end)
    fun getTransactionsByDateRangeAndType(start: Long, end: Long, type: TransactionType) =
        transactionDao.getTransactionsByDateRangeAndType(start, end, type)
    fun getTransactionItems(transactionId: String) = transactionDao.getTransactionItems(transactionId)
    fun getTotalByDateRange(type: TransactionType, start: Long, end: Long) =
        transactionDao.getTotalByDateRange(type, start, end)
    fun getTransactionsByUser(userId: String) = transactionDao.getTransactionsByUser(userId)

    suspend fun insertFullTransaction(transaction: Transaction, items: List<TransactionItem>) =
        transactionDao.insertFullTransaction(transaction, items)
    suspend fun getTransactionById(id: String) = transactionDao.getTransactionById(id)
    suspend fun getTransactionItemsList(transactionId: String) = transactionDao.getTransactionItemsList(transactionId)
    suspend fun getTotalSalesByDateRange(start: Long, end: Long) = transactionDao.getTotalSalesByDateRange(start, end)
    suspend fun getTotalPurchasesByDateRange(start: Long, end: Long) = transactionDao.getTotalPurchasesByDateRange(start, end)
    suspend fun getSalesCountByDateRange(start: Long, end: Long) = transactionDao.getSalesCountByDateRange(start, end)
    suspend fun getUnsyncedTransactions() = transactionDao.getUnsyncedTransactions()
    suspend fun getUnsyncedTransactionItems() = transactionDao.getUnsyncedTransactionItems()
    suspend fun markTransactionAsSynced(id: String) = transactionDao.markTransactionAsSynced(id)
    suspend fun markTransactionItemAsSynced(id: String) = transactionDao.markTransactionItemAsSynced(id)
}
