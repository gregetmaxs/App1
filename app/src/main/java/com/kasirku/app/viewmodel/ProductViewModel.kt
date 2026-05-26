package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.Category
import com.kasirku.core.model.Product
import com.kasirku.core.model.ProductVariant
import com.kasirku.core.model.Supplier
import com.kasirku.core.repository.CategoryRepository
import com.kasirku.core.repository.ProductRepository
import com.kasirku.core.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadData(storeId: String) {
        if (storeId.isEmpty()) return
        viewModelScope.launch {
            productRepository.getAllByStore(storeId).collect {
                _uiState.value = _uiState.value.copy(products = it)
            }
        }
        viewModelScope.launch {
            categoryRepository.getAllByStore(storeId).collect {
                _uiState.value = _uiState.value.copy(categories = it)
            }
        }
        viewModelScope.launch {
            supplierRepository.getAllByStore(storeId).collect {
                _uiState.value = _uiState.value.copy(suppliers = it)
            }
        }
    }

    fun createProduct(
        name: String,
        barcode: String,
        categoryId: String,
        supplierId: String,
        buyPrice: Long,
        sellPrice: Long,
        stock: Int,
        unit: String,
        storeId: String,
        variants: List<ProductVariant>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val product = Product(
                id = UUID.randomUUID().toString(),
                name = name,
                barcode = barcode,
                categoryId = categoryId,
                supplierId = supplierId,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                stock = stock,
                unit = unit,
                hasVariants = variants.isNotEmpty(),
                storeId = storeId,
                isActive = true
            )
            productRepository.createProduct(product, variants)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun clearState() {
        _uiState.value = _uiState.value.copy(success = false, error = null)
    }
}
