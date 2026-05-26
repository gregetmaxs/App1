package com.kasirku.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.UserDao
import com.kasirku.core.model.User
import com.kasirku.core.repository.AuthRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Login gagal")

        var user = userDao.getByEmail(email)?.toModel()
        if (user == null) {
            val doc = firestore.collection(Constants.FIRESTORE_USERS)
                .document(firebaseUser.uid).get().await()
            user = if (doc.exists()) {
                doc.toObject(User::class.java) ?: throw Exception("Data user tidak ditemukan")
            } else {
                User(
                    id = firebaseUser.uid,
                    email = email,
                    fullName = firebaseUser.displayName ?: email.substringBefore("@"),
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
            }
            userDao.insert(user.toEntity())
        }
        user
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return userDao.getByEmail(firebaseUser.email ?: "")?.toModel()
    }

    override fun isLoggedIn(): Boolean = auth.currentUser != null

    override suspend fun registerEmployee(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        nik: String,
        address: String,
        roleId: String,
        storeId: String
    ): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Gagal membuat akun")

        val user = User(
            id = firebaseUser.uid,
            email = email,
            fullName = fullName,
            phone = phone,
            nik = nik,
            address = address,
            roleId = roleId,
            storeId = storeId,
            isActive = true
        )

        userDao.insert(user.toEntity())
        firestore.collection(Constants.FIRESTORE_USERS)
            .document(firebaseUser.uid)
            .set(user).await()

        // Re-sign in as current admin
        auth.currentUser?.let { } // Firebase will keep current session after createUser
        user
    }
}
