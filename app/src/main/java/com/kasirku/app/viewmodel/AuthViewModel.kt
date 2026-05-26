package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.License
import com.kasirku.core.model.Role
import com.kasirku.core.model.Store
import com.kasirku.core.model.User
import com.kasirku.core.repository.AuthRepository
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.repository.RoleRepository
import com.kasirku.core.repository.StoreRepository
import com.kasirku.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val role: Role? = null,
    val license: License? = null,
    val error: String? = null,
    val licenseExpired: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val licenseRepository: LicenseRepository,
    private val roleRepository: RoleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentSession()
    }

    private fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null && user.storeId.isNotEmpty()) {
                    val license = licenseRepository.getLicenseByStoreId(user.storeId)
                    if (license != null && license.isExpired) {
                        authRepository.logout()
                        _uiState.value = AuthUiState(
                            licenseExpired = true,
                            error = "License telah habis masa aktif. Hubungi Owner untuk perpanjangan."
                        )
                    } else {
                        val role = try { roleRepository.getById(user.roleId) } catch (_: Exception) { null }
                        _uiState.value = AuthUiState(
                            isLoggedIn = true,
                            user = user,
                            role = role,
                            license = license
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Gagal memuat sesi: ${e.message}")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email dan password wajib diisi")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.login(email, password)
                .onSuccess { user ->
                    try {
                        handleLoginSuccess(user)
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Login berhasil tapi gagal memuat data: ${e.message}"
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = when {
                            e.message?.contains("password") == true -> "Password salah"
                            e.message?.contains("no user") == true -> "Akun tidak ditemukan"
                            e.message?.contains("network") == true -> "Tidak ada koneksi internet"
                            else -> e.message ?: "Login gagal"
                        }
                    )
                }
        }
    }

    private suspend fun handleLoginSuccess(user: User) {
        if (user.storeId.isEmpty()) {
            val storeId = UUID.randomUUID().toString()
            val store = Store(
                id = storeId,
                name = "Toko ${user.fullName.ifEmpty { user.email.substringBefore("@") }}",
                ownerName = user.fullName.ifEmpty { user.email.substringBefore("@") },
                ownerEmail = user.email
            )
            storeRepository.createStore(store)
            roleRepository.initDefaultRoles(storeId)

            // Use firstOrNull() instead of collect to avoid hanging on Room Flow
            val roleList = roleRepository.getAllByStore(storeId).firstOrNull() ?: emptyList()
            val adminRole = roleList.find { it.name == "Admin" }
            val adminRoleId = adminRole?.id ?: ""

            val updatedUser = user.copy(storeId = storeId, roleId = adminRoleId)
            userRepository.updateUser(updatedUser)

            _uiState.value = AuthUiState(
                isLoading = false,
                isLoggedIn = true,
                user = updatedUser,
                role = adminRole
            )
        } else {
            val license = try { licenseRepository.getLicenseByStoreId(user.storeId) } catch (_: Exception) { null }
            if (license != null && license.isExpired) {
                authRepository.logout()
                _uiState.value = AuthUiState(
                    isLoading = false,
                    licenseExpired = true,
                    error = "License telah habis masa aktif.\nHubungi Owner untuk perpanjangan."
                )
                return
            }
            val role = try { roleRepository.getById(user.roleId) } catch (_: Exception) { null }
            _uiState.value = AuthUiState(
                isLoading = false,
                isLoggedIn = true,
                user = user,
                role = role,
                license = license
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            try { authRepository.logout() } catch (_: Exception) { }
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
