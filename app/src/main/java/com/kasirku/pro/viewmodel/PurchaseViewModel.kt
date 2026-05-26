package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.*
import com.kasirku.pro.data.repository.ItemRepository
import com.kasirku.pro.data.repository.TransactionRepository
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class PurchaseViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val itemRepository = ItemRepository(database.itemDao())
    private val transactionRepository = TransactionRepository(database.transactionDao())
    private val sessionManager = SessionManager(application)
    private val syncManager = SyncManager(database, app.networkMonitor)

    val allItems = itemRepository.getAllActiveItems()
    val purchaseTransactions = transactionRepository.getTransactionsByType(TransactionType.PURCHASE)

    private val _purchaseResult = MutableLiveData<Result<Transaction>>()
    val purchaseResult: LiveData<Result<Transaction>> = _purchaseResult

    fun createPurchase(items: List<CartItem>, supplierName: String, notes: String) {
        viewModelScope.launch {
            try {
                if (items.isEmpty()) {
                    _purchaseResult.value = Result.failure(Exception("Tidak ada item"))
                    return@launch
                }

                val total = items.sumOf { it.item.costPrice * it.quantity }

                val transaction = Transaction(
                    type = TransactionType.PURCHASE,
                    customerName = supplierName,
                    totalAmount = total,
                    paidAmount = total,
                    userId = sessionManager.getUserId(),
                    userName = sessionManager.getUserName(),
                    notes = notes,
                    deliveryType = DeliveryType.PICKUP
                )

                val transactionItems = items.map { cartItem ->
                    TransactionItem(
                        transactionId = transaction.id,
                        itemId = cartItem.item.id,
                        itemName = cartItem.item.name,
                        quantity = cartItem.quantity,
                        price = cartItem.item.costPrice,
                        subtotal = cartItem.item.costPrice * cartItem.quantity
                    )
                }

                transactionRepository.insertFullTransaction(transaction, transactionItems)

                for (cartItem in items) {
                    itemRepository.increaseStock(cartItem.item.id, cartItem.quantity)
                }

                _purchaseResult.value = Result.success(transaction)
                syncManager.syncAll()
            } catch (e: Exception) {
                _purchaseResult.value = Result.failure(e)
            }
        }
    }
}
