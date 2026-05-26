package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.Role
import com.kasirku.pro.data.entity.User
import com.kasirku.pro.data.repository.UserRepository
import com.kasirku.pro.util.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as KasirKuApp).database
    private val userRepository = UserRepository(database.userDao())
    private val roleDao = database.roleDao()
    private val sessionManager = SessionManager(application)
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    val allUsers = userRepository.getAllUsers()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: return@addOnSuccessListener
                        viewModelScope.launch {
                            val user = userRepository.getUserById(uid)
                            if (user != null && user.isActive) {
                                val role = roleDao.getRoleById(user.roleId)
                                if (role != null) {
                                    sessionManager.saveSession(
                                        user.id, user.email, user.name,
                                        user.roleId, user.roleName, role
                                    )
                                    _loginResult.postValue(Result.success(user))
                                } else {
                                    _loginResult.postValue(Result.failure(Exception("Role tidak ditemukan")))
                                }
                            } else {
                                _loginResult.postValue(Result.failure(Exception("Akun tidak aktif atau tidak ditemukan")))
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        _loginResult.postValue(Result.failure(Exception("Login gagal: ${e.message}")))
                    }
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        ktpNumber: String,
        phone: String,
        address: String,
        role: Role
    ) {
        registerUser(email, password, name, ktpNumber, phone, address, role.id, role.name)
    }

    fun registerUser(
        email: String,
        password: String,
        name: String,
        ktpNumber: String,
        phone: String,
        address: String,
        roleId: String,
        roleName: String
    ) {
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: return@addOnSuccessListener
                        viewModelScope.launch {
                            val user = User(
                                id = uid,
                                email = email,
                                name = name,
                                ktpNumber = ktpNumber,
                                phone = phone,
                                address = address,
                                roleId = roleId,
                                roleName = roleName
                            )
                            userRepository.insert(user)
                            _registerResult.postValue(Result.success(user))
                        }
                    }
                    .addOnFailureListener { e ->
                        _registerResult.postValue(Result.failure(Exception("Registrasi gagal: ${e.message}")))
                    }
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.update(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.update(user.copy(isActive = false, updatedAt = System.currentTimeMillis()))
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }

    fun isLoggedIn() = sessionManager.isLoggedIn()
}
