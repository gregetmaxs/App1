package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.TransactionDao
import com.kasirku.core.data.local.dao.TransactionItemDao
import com.kasirku.core.model.Transaction
import com.kasirku.core.model.TransactionItem
import com.kasirku.core.repository.TransactionRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val transactionDao: TransactionDao,
    private val transactionItemDao: TransactionItemDao
) : TransactionRepository {

    override fun getAllByStore(storeId: String): Flow<List<Transaction>> =
        transactionDao.getAllByStore(storeId).map { list ->
            list.map { entity ->
                val items = transactionItemDao.getByTransactionId(entity.id).first().map { it.toModel() }
                entity.toModel(items)
            }
        }

    override fun getRecent(storeId: String, limit: Int): Flow<List<Transaction>> =
        transactionDao.getRecent(storeId, limit).map { list ->
            list.map { it.toModel() }
        }

    override fun getByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(storeId, startDate, endDate).map { list ->
            list.map { it.toModel() }
        }

    override suspend fun getById(id: String): Transaction? {
        val entity = transactionDao.getById(id) ?: return null
        val items = transactionItemDao.getByTransactionId(id).first().map { it.toModel() }
        return entity.toModel(items)
    }

    override fun getTotalByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Long?> =
        transactionDao.getTotalByDateRange(storeId, startDate, endDate)

    override fun getCountByDateRange(storeId: String, startDate: Long, endDate: Long): Flow<Int> =
        transactionDao.getCountByDateRange(storeId, startDate, endDate)

    override suspend fun createTransaction(transaction: Transaction): Result<Transaction> = runCatching {
        transactionDao.insert(transaction.toEntity())
        transaction.items.forEach { item ->
            transactionItemDao.insert(item.toEntity())
        }
        firestore.collection(Constants.FIRESTORE_TRANSACTIONS)
            .document(transaction.id).set(transaction).await()
        transactionDao.markSynced(transaction.id)
        transaction
    }

    override suspend fun updateDeliveryStatus(transactionId: String, status: String) {
        transactionDao.updateDeliveryStatus(transactionId, status)
        runCatching {
            firestore.collection(Constants.FIRESTORE_TRANSACTIONS)
                .document(transactionId).update("deliveryStatus", status).await()
        }
    }

    override suspend fun updateDeliveryProof(transactionId: String, proofUrl: String) {
        transactionDao.updateDeliveryProof(transactionId, proofUrl)
        runCatching {
            firestore.collection(Constants.FIRESTORE_TRANSACTIONS)
                .document(transactionId).update("deliveryProofUrl", proofUrl).await()
        }
    }

    override suspend fun syncToCloud() {
        transactionDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_TRANSACTIONS)
                    .document(entity.id).set(entity.toModel()).await()
                transactionDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_TRANSACTIONS).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Transaction::class.java)?.let { tx ->
                    transactionDao.insert(tx.toEntity(isSynced = true))
                }
            }
        }
    }
}
