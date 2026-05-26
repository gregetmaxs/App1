package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.DeliveryStatus
import com.kasirku.pro.data.entity.DeliveryType
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.repository.TransactionRepository
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class DeliveryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val transactionRepository = TransactionRepository(database.transactionDao())
    private val sessionManager = SessionManager(application)
    private val syncManager = SyncManager(database, app.networkMonitor)

    val allDeliveries: LiveData<List<Transaction>> = transactionRepository.getAllTransactions().map { transactions ->
        transactions.filter { it.deliveryType == DeliveryType.DELIVERY }
    }

    val myDeliveries: LiveData<List<Transaction>> = allDeliveries.map { deliveries ->
        val userId = sessionManager.getUserId()
        deliveries.filter { it.driverId == userId || it.helperId == userId }
    }

    private val _updateResult = MutableLiveData<Result<Transaction>>()
    val updateResult: LiveData<Result<Transaction>> = _updateResult

    fun updateDeliveryStatus(transactionId: String, status: DeliveryStatus, proofImage: String = "") {
        viewModelScope.launch {
            try {
                val transaction = transactionRepository.getTransactionById(transactionId)
                if (transaction != null) {
                    val updated = transaction.copy(
                        deliveryStatus = status,
                        deliveryProofImage = if (proofImage.isNotEmpty()) proofImage else transaction.deliveryProofImage,
                        deliveredAt = if (status == DeliveryStatus.DELIVERED) System.currentTimeMillis() else transaction.deliveredAt,
                        isSynced = false
                    )
                    database.transactionDao().insertTransaction(updated)
                    _updateResult.value = Result.success(updated)
                    syncManager.syncAll()
                }
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }

    fun getTransactionItems(transactionId: String) = transactionRepository.getTransactionItems(transactionId)
}
