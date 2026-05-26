package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.SupplierDao
import com.kasirku.core.model.Supplier
import com.kasirku.core.repository.SupplierRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SupplierRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supplierDao: SupplierDao
) : SupplierRepository {

    override fun getAllByStore(storeId: String): Flow<List<Supplier>> =
        supplierDao.getAllByStore(storeId).map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: String): Supplier? =
        supplierDao.getById(id)?.toModel()

    override suspend fun create(supplier: Supplier): Result<Supplier> = runCatching {
        supplierDao.insert(supplier.toEntity())
        firestore.collection(Constants.FIRESTORE_SUPPLIERS).document(supplier.id).set(supplier).await()
        supplierDao.markSynced(supplier.id)
        supplier
    }

    override suspend fun update(supplier: Supplier): Result<Unit> = runCatching {
        supplierDao.update(supplier.toEntity())
        firestore.collection(Constants.FIRESTORE_SUPPLIERS).document(supplier.id).set(supplier).await()
        supplierDao.markSynced(supplier.id)
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        supplierDao.deleteById(id)
        firestore.collection(Constants.FIRESTORE_SUPPLIERS).document(id).delete().await()
    }

    override suspend fun syncToCloud() {
        supplierDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_SUPPLIERS)
                    .document(entity.id).set(entity.toModel()).await()
                supplierDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_SUPPLIERS).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Supplier::class.java)?.let { supplier ->
                    supplierDao.insert(supplier.toEntity(isSynced = true))
                }
            }
        }
    }
}
