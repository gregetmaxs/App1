package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.Supplier
import com.kasirku.core.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    fun loadAll(storeId: String) {
        viewModelScope.launch {
            supplierRepository.getAllByStore(storeId).collect { _suppliers.value = it }
        }
    }

    fun create(name: String, phone: String, address: String, storeId: String) {
        viewModelScope.launch {
            supplierRepository.create(Supplier(
                id = UUID.randomUUID().toString(),
                name = name,
                phone = phone,
                address = address,
                storeId = storeId
            ))
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { supplierRepository.delete(id) }
    }
}
