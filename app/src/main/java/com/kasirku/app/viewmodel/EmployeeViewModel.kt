package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.Role
import com.kasirku.core.model.User
import com.kasirku.core.repository.AuthRepository
import com.kasirku.core.repository.RoleRepository
import com.kasirku.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String = ""
)

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _employees = MutableStateFlow<List<User>>(emptyList())
    val employees: StateFlow<List<User>> = _employees.asStateFlow()

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles.asStateFlow()

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    fun loadData(storeId: String) {
        viewModelScope.launch {
            userRepository.getAllByStore(storeId).collect { _employees.value = it }
        }
        viewModelScope.launch {
            roleRepository.getAllByStore(storeId).collect { _roles.value = it }
        }
    }

    fun createEmployee(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        nik: String,
        address: String,
        roleId: String,
        storeId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")
            authRepository.registerEmployee(email, password, fullName, phone, nik, address, roleId, storeId)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, success = true) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Gagal") }
        }
    }

    fun clearState() { _uiState.value = EmployeeUiState() }
}
