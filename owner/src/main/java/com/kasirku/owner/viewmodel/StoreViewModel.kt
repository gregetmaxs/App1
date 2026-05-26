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

data class StoreWithLicense(
    val store: Store,
    val license: License? = null
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val licenseRepository: LicenseRepository
) : ViewModel() {

    private val _stores = MutableStateFlow<List<StoreWithLicense>>(emptyList())
    val stores: StateFlow<List<StoreWithLicense>> = _stores.asStateFlow()

    init {
        loadStores()
    }

    private fun loadStores() {
        viewModelScope.launch {
            storeRepository.getAllStores().collect { storeList ->
                val withLicense = storeList.map { store ->
                    val license = licenseRepository.getLicenseByStoreId(store.id)
                    StoreWithLicense(store, license)
                }
                _stores.value = withLicense
            }
        }
    }
}
