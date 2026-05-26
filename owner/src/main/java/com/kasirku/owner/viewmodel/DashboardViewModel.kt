package com.kasirku.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.License
import com.kasirku.core.model.Store
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalStores: Int = 0,
    val activeLicenses: Int = 0,
    val expiredLicenses: Int = 0,
    val totalRevenue: Long = 0L,
    val recentLicenses: List<License> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val licenseRepository: LicenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            storeRepository.getAllStores().collect { stores ->
                _uiState.value = _uiState.value.copy(totalStores = stores.size)
            }
        }
        viewModelScope.launch {
            licenseRepository.getAllLicenses().collect { licenses ->
                val active = licenses.count { it.status == "ACTIVE" && (it.expiresAt == 0L || it.expiresAt > System.currentTimeMillis()) }
                val expired = licenses.count { it.status == "EXPIRED" || (it.expiresAt > 0 && it.expiresAt <= System.currentTimeMillis()) }
                val revenue = licenses.sumOf { it.price }
                _uiState.value = _uiState.value.copy(
                    activeLicenses = active,
                    expiredLicenses = expired,
                    totalRevenue = revenue,
                    recentLicenses = licenses.take(10)
                )
            }
        }
    }
}
