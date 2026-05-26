package com.kasirku.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.UserDao
import com.kasirku.core.model.User
import com.kasirku.core.repository.UserRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    override fun getAllByStore(storeId: String): Flow<List<User>> =
        userDao.getAllByStore(storeId).map { list -> list.map { it.toModel() } }

    override suspend fun getUserById(id: String): User? =
        userDao.getById(id)?.toModel()

    override suspend fun getUserByEmail(email: String): User? =
        userDao.getByEmail(email)?.toModel()

    override suspend fun createUser(user: User, password: String): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(user.email, password).await()
        val uid = result.user?.uid ?: throw Exception("Gagal membuat akun")
        val newUser = user.copy(id = uid)
        userDao.insert(newUser.toEntity())
        firestore.collection(Constants.FIRESTORE_USERS)
            .document(uid).set(newUser).await()
        userDao.markSynced(uid)
        newUser
    }

    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        userDao.update(user.toEntity())
        firestore.collection(Constants.FIRESTORE_USERS)
            .document(user.id).set(user).await()
        userDao.markSynced(user.id)
    }

    override suspend fun syncToCloud() {
        userDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_USERS)
                    .document(entity.id).set(entity.toModel()).await()
                userDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_USERS).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(User::class.java)?.let { user ->
                    userDao.insert(user.toEntity(isSynced = true))
                }
            }
        }
    }
}
