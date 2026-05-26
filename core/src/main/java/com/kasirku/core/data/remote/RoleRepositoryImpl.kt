package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.RoleDao
import com.kasirku.core.model.Role
import com.kasirku.core.repository.RoleRepository
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RoleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val roleDao: RoleDao
) : RoleRepository {

    private val collection = "roles"

    override fun getAllByStore(storeId: String): Flow<List<Role>> =
        roleDao.getAllByStore(storeId).map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: String): Role? =
        roleDao.getById(id)?.toModel()

    override suspend fun create(role: Role): Result<Role> = runCatching {
        roleDao.insert(role.toEntity())
        firestore.collection(collection).document(role.id).set(role).await()
        roleDao.markSynced(role.id)
        role
    }

    override suspend fun update(role: Role): Result<Unit> = runCatching {
        roleDao.update(role.toEntity())
        firestore.collection(collection).document(role.id).set(role).await()
        roleDao.markSynced(role.id)
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        roleDao.deleteById(id)
        firestore.collection(collection).document(id).delete().await()
    }

    override suspend fun initDefaultRoles(storeId: String) {
        val defaults = listOf(
            Role(UUID.randomUUID().toString(), "Admin", storeId, true,
                canAccessPos = true, canAccessProducts = true, canAccessStock = true,
                canAccessTransactions = true, canAccessReports = true, canAccessEmployees = true,
                canAccessCustomers = true, canAccessDelivery = true, canTakeDeliveryPhoto = true,
                canAccessSettings = true, canManageRoles = true),
            Role(UUID.randomUUID().toString(), "Kasir", storeId, true,
                canAccessPos = true, canAccessTransactions = true, canAccessDelivery = true),
            Role(UUID.randomUUID().toString(), "Gudang", storeId, true,
                canAccessProducts = true, canAccessStock = true),
            Role(UUID.randomUUID().toString(), "Supir", storeId, true,
                canAccessDelivery = true, canTakeDeliveryPhoto = true),
            Role(UUID.randomUUID().toString(), "Kenek", storeId, true,
                canAccessDelivery = true, canTakeDeliveryPhoto = true),
            Role(UUID.randomUUID().toString(), "Viewer", storeId, true,
                canAccessReports = true, canAccessTransactions = true)
        )
        defaults.forEach { roleDao.insert(it.toEntity()) }
    }

    override suspend fun syncToCloud() {
        roleDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(collection)
                    .document(entity.id).set(entity.toModel()).await()
                roleDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(collection).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Role::class.java)?.let { role ->
                    roleDao.insert(role.toEntity(isSynced = true))
                }
            }
        }
    }
}
