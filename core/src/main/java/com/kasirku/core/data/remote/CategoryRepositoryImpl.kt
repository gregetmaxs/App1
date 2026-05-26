package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.CategoryDao
import com.kasirku.core.model.Category
import com.kasirku.core.repository.CategoryRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllByStore(storeId: String): Flow<List<Category>> =
        categoryDao.getAllByStore(storeId).map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: String): Category? =
        categoryDao.getById(id)?.toModel()

    override suspend fun create(category: Category): Result<Category> = runCatching {
        categoryDao.insert(category.toEntity())
        firestore.collection(Constants.FIRESTORE_CATEGORIES).document(category.id).set(category).await()
        categoryDao.markSynced(category.id)
        category
    }

    override suspend fun update(category: Category): Result<Unit> = runCatching {
        categoryDao.update(category.toEntity())
        firestore.collection(Constants.FIRESTORE_CATEGORIES).document(category.id).set(category).await()
        categoryDao.markSynced(category.id)
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        categoryDao.deleteById(id)
        firestore.collection(Constants.FIRESTORE_CATEGORIES).document(id).delete().await()
    }

    override suspend fun syncToCloud() {
        categoryDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_CATEGORIES)
                    .document(entity.id).set(entity.toModel()).await()
                categoryDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_CATEGORIES).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Category::class.java)?.let { category ->
                    categoryDao.insert(category.toEntity(isSynced = true))
                }
            }
        }
    }
}
