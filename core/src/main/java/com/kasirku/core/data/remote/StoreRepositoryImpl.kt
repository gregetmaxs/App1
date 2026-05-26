package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.StoreDao
import com.kasirku.core.model.Store
import com.kasirku.core.repository.StoreRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storeDao: StoreDao
) : StoreRepository {

    override fun getAllStores(): Flow<List<Store>> =
        storeDao.getAll().map { list -> list.map { it.toModel() } }

    override suspend fun getStoreById(id: String): Store? =
        storeDao.getById(id)?.toModel()

    override suspend fun createStore(store: Store): Result<Store> = runCatching {
        storeDao.insert(store.toEntity())
        firestore.collection(Constants.FIRESTORE_STORES)
            .document(store.id).set(store).await()
        storeDao.markSynced(store.id)
        store
    }

    override suspend fun updateStore(store: Store): Result<Unit> = runCatching {
        storeDao.update(store.toEntity())
        firestore.collection(Constants.FIRESTORE_STORES)
            .document(store.id).set(store).await()
        storeDao.markSynced(store.id)
    }

    override suspend fun syncToCloud() {
        storeDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_STORES)
                    .document(entity.id).set(entity.toModel()).await()
                storeDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_STORES).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Store::class.java)?.let { store ->
                    storeDao.insert(store.toEntity(isSynced = true))
                }
            }
        }
    }
}
