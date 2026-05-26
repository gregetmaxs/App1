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
    val employees: List<User> = emptyList(),
    val roles: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    fun loadData(storeId: String) {
        viewModelScope.launch {
            userRepository.getAllByStore(storeId).collect {
                _uiState.value = _uiState.value.copy(employees = it)
            }
        }
        viewModelScope.launch {
            roleRepository.getAllByStore(storeId).collect {
                _uiState.value = _uiState.value.copy(roles = it)
            }
        }
    }

    fun registerEmployee(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        nik: String,
        address: String,
        roleId: String,
        storeId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.registerEmployee(email, password, fullName, phone, nik, address, roleId, storeId)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, success = true) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }

    fun clearState() { _uiState.value = _uiState.value.copy(success = false, error = null) }
}
