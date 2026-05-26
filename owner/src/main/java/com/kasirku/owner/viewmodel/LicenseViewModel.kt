package com.kasirku.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.License
import com.kasirku.core.model.LicensePrice
import com.kasirku.core.model.Store
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LicenseUiState(
    val licenses: List<License> = emptyList(),
    val prices: List<LicensePrice> = emptyList(),
    val stores: List<Store> = emptyList(),
    val isGenerating: Boolean = false,
    val generatedLicense: License? = null,
    val error: String? = null
)

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val licenseRepository: LicenseRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LicenseUiState())
    val uiState: StateFlow<LicenseUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            licenseRepository.getAllLicenses().collect {
                _uiState.value = _uiState.value.copy(licenses = it)
            }
        }
        viewModelScope.launch {
            licenseRepository.getAllPrices().collect {
                _uiState.value = _uiState.value.copy(prices = it)
            }
        }
        viewModelScope.launch {
            storeRepository.getAllStores().collect {
                _uiState.value = _uiState.value.copy(stores = it)
            }
        }
    }

    fun generateLicense(storeId: String, type: String, createdBy: String) {
        val price = _uiState.value.prices.find { it.type == type }?.price ?: 0L
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            licenseRepository.generateLicense(storeId, type, createdBy, price)
                .onSuccess { license ->
                    _uiState.value = _uiState.value.copy(isGenerating = false, generatedLicense = license)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isGenerating = false, error = e.message)
                }
        }
    }

    fun updatePrice(price: LicensePrice) {
        viewModelScope.launch {
            licenseRepository.updatePrice(price)
        }
    }

    fun clearGenerated() {
        _uiState.value = _uiState.value.copy(generatedLicense = null, error = null)
    }
}
