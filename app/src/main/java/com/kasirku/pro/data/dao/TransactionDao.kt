package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.entity.TransactionItem
import com.kasirku.pro.data.entity.TransactionType

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @androidx.room.Transaction
    suspend fun insertFullTransaction(transaction: Transaction, items: List<TransactionItem>) {
        insertTransaction(transaction)
        insertTransactionItems(items)
    }

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND type = :type ORDER BY date DESC")
    fun getTransactionsByDateRangeAndType(startDate: Long, endDate: Long, type: TransactionType): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    fun getTransactionItems(transactionId: String): LiveData<List<TransactionItem>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getTransactionItemsList(transactionId: String): List<TransactionItem>

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalByDateRange(type: TransactionType, startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'SALE' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSalesByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'PURCHASE' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalPurchasesByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'SALE' AND date BETWEEN :startDate AND :endDate")
    suspend fun getSalesCountByDateRange(startDate: Long, endDate: Long): Int

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnsyncedTransactions(): List<Transaction>

    @Query("SELECT * FROM transaction_items WHERE isSynced = 0")
    suspend fun getUnsyncedTransactionItems(): List<TransactionItem>

    @Query("UPDATE transactions SET isSynced = 1 WHERE id = :id")
    suspend fun markTransactionAsSynced(id: String)

    @Query("UPDATE transaction_items SET isSynced = 1 WHERE id = :id")
    suspend fun markTransactionItemAsSynced(id: String)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): Transaction?

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: String): LiveData<List<Transaction>>
}
