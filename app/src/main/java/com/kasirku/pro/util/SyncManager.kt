package com.kasirku.pro.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.pro.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncManager(
    private val database: AppDatabase,
    private val networkMonitor: NetworkMonitor
) {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun syncAll() {
        if (!networkMonitor.isCurrentlyConnected()) return

        withContext(Dispatchers.IO) {
            try {
                syncUsers()
                syncCategories()
                syncItems()
                syncTransactions()
                syncTransactionItems()
                syncCustomers()
                Log.d(TAG, "Sync completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed: ${e.message}")
            }
        }
    }

    private suspend fun syncUsers() {
        val unsyncedUsers = database.userDao().getUnsyncedUsers()
        for (user in unsyncedUsers) {
            try {
                firestore.collection("users")
                    .document(user.id)
                    .set(mapOf(
                        "email" to user.email,
                        "name" to user.name,
                        "ktpNumber" to user.ktpNumber,
                        "phone" to user.phone,
                        "address" to user.address,
                        "roleName" to user.roleName,
                        "roleId" to user.roleId,
                        "isActive" to user.isActive,
                        "createdAt" to user.createdAt,
                        "updatedAt" to user.updatedAt
                    ))
                    .await()
                database.userDao().markAsSynced(user.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync user ${user.id}: ${e.message}")
            }
        }
    }

    private suspend fun syncCategories() {
        val unsyncedCategories = database.categoryDao().getUnsyncedCategories()
        for (category in unsyncedCategories) {
            try {
                firestore.collection("categories")
                    .document(category.id)
                    .set(mapOf(
                        "name" to category.name,
                        "createdAt" to category.createdAt
                    ))
                    .await()
                database.categoryDao().markAsSynced(category.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync category ${category.id}: ${e.message}")
            }
        }
    }

    private suspend fun syncItems() {
        val unsyncedItems = database.itemDao().getUnsyncedItems()
        for (item in unsyncedItems) {
            try {
                firestore.collection("items")
                    .document(item.id)
                    .set(mapOf(
                        "name" to item.name,
                        "price" to item.price,
                        "costPrice" to item.costPrice,
                        "stock" to item.stock,
                        "categoryId" to item.categoryId,
                        "imagePath" to item.imagePath,
                        "barcode" to item.barcode,
                        "description" to item.description,
                        "isActive" to item.isActive,
                        "createdAt" to item.createdAt,
                        "updatedAt" to item.updatedAt
                    ))
                    .await()
                database.itemDao().markAsSynced(item.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync item ${item.id}: ${e.message}")
            }
        }
    }

    private suspend fun syncTransactions() {
        val unsyncedTransactions = database.transactionDao().getUnsyncedTransactions()
        for (trx in unsyncedTransactions) {
            try {
                firestore.collection("transactions")
                    .document(trx.id)
                    .set(mapOf(
                        "type" to trx.type.name,
                        "customerName" to trx.customerName,
                        "totalAmount" to trx.totalAmount,
                        "paidAmount" to trx.paidAmount,
                        "changeAmount" to trx.changeAmount,
                        "userId" to trx.userId,
                        "userName" to trx.userName,
                        "notes" to trx.notes,
                        "deliveryType" to trx.deliveryType.name,
                        "deliveryAddress" to trx.deliveryAddress,
                        "driverId" to trx.driverId,
                        "driverName" to trx.driverName,
                        "helperId" to trx.helperId,
                        "helperName" to trx.helperName,
                        "deliveryStatus" to trx.deliveryStatus.name,
                        "date" to trx.date,
                        "createdAt" to trx.createdAt
                    ))
                    .await()
                database.transactionDao().markTransactionAsSynced(trx.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync transaction ${trx.id}: ${e.message}")
            }
        }
    }

    private suspend fun syncTransactionItems() {
        val unsyncedItems = database.transactionDao().getUnsyncedTransactionItems()
        for (item in unsyncedItems) {
            try {
                firestore.collection("transaction_items")
                    .document(item.id)
                    .set(mapOf(
                        "transactionId" to item.transactionId,
                        "itemId" to item.itemId,
                        "itemName" to item.itemName,
                        "quantity" to item.quantity,
                        "price" to item.price,
                        "subtotal" to item.subtotal
                    ))
                    .await()
                database.transactionDao().markTransactionItemAsSynced(item.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync transaction item ${item.id}: ${e.message}")
            }
        }
    }

    private suspend fun syncCustomers() {
        val unsyncedCustomers = database.customerDao().getUnsyncedCustomers()
        for (customer in unsyncedCustomers) {
            try {
                firestore.collection("customers")
                    .document(customer.id)
                    .set(mapOf(
                        "name" to customer.name,
                        "phone" to customer.phone,
                        "address" to customer.address,
                        "createdAt" to customer.createdAt,
                        "updatedAt" to customer.updatedAt
                    ))
                    .await()
                database.customerDao().markAsSynced(customer.id)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync customer ${customer.id}: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "SyncManager"
    }
}
