package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.CartItem
import com.kasirku.core.model.Category
import com.kasirku.core.model.Product
import com.kasirku.core.model.Transaction
import com.kasirku.core.model.TransactionItem
import com.kasirku.core.repository.CategoryRepository
import com.kasirku.core.repository.ProductRepository
import com.kasirku.core.repository.TransactionRepository
import com.kasirku.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class PosUiState(
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val cart: List<CartItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val showCheckout: Boolean = false,
    val checkoutSuccess: Boolean = false,
    val error: String? = null
) {
    val subtotal: Long get() = cart.sumOf { it.subtotal }
    val ppnAmount: Long get() = (subtotal * Constants.PPN_DEFAULT).toLong()
    val total: Long get() = subtotal + ppnAmount
    val cartCount: Int get() = cart.sumOf { it.quantity }
}

@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private var storeId = ""
    private var cashierUserId = ""
    private var cashierName = ""

    fun init(storeId: String, userId: String, userName: String) {
        this.storeId = storeId
        this.cashierUserId = userId
        this.cashierName = userName
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllByStore(storeId).collect { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllByStore(storeId).collect { products ->
                _uiState.value = _uiState.value.copy(products = products)
            }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                productRepository.search(storeId, query).collect { products ->
                    _uiState.value = _uiState.value.copy(products = products, selectedCategoryId = null)
                }
            }
        } else {
            loadProducts()
        }
    }

    fun filterByCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId, searchQuery = "")
        if (categoryId != null) {
            viewModelScope.launch {
                productRepository.getByCategory(storeId, categoryId).collect { products ->
                    _uiState.value = _uiState.value.copy(products = products)
                }
            }
        } else {
            loadProducts()
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _uiState.value.cart.toMutableList()
        val existingIndex = currentCart.indexOfFirst { it.product.id == product.id && it.variant == null }
        if (existingIndex >= 0) {
            val existing = currentCart[existingIndex]
            currentCart[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentCart.add(CartItem(product = product))
        }
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun updateCartQuantity(index: Int, quantity: Int) {
        val currentCart = _uiState.value.cart.toMutableList()
        if (quantity <= 0) {
            currentCart.removeAt(index)
        } else {
            currentCart[index] = currentCart[index].copy(quantity = quantity)
        }
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun removeFromCart(index: Int) {
        val currentCart = _uiState.value.cart.toMutableList()
        currentCart.removeAt(index)
        _uiState.value = _uiState.value.copy(cart = currentCart)
    }

    fun toggleCheckout() {
        _uiState.value = _uiState.value.copy(showCheckout = !_uiState.value.showCheckout, checkoutSuccess = false)
    }

    fun checkout(paymentMethod: String, amountPaid: Long, deliveryType: String) {
        val state = _uiState.value
        if (state.cart.isEmpty()) return

        viewModelScope.launch {
            val txId = UUID.randomUUID().toString()
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("id"))
            val txNumber = "KKU-${dateFormat.format(Date())}-${(1000..9999).random()}"

            val items = state.cart.map { cartItem ->
                TransactionItem(
                    id = UUID.randomUUID().toString(),
                    transactionId = txId,
                    productId = cartItem.product.id,
                    variantId = cartItem.variant?.id ?: "",
                    productName = cartItem.product.name,
                    variantName = cartItem.variant?.name ?: "",
                    quantity = cartItem.quantity,
                    price = cartItem.price,
                    subtotal = cartItem.subtotal
                )
            }

            val transaction = Transaction(
                id = txId,
                transactionNumber = txNumber,
                cashierUserId = cashierUserId,
                cashierName = cashierName,
                subtotal = state.subtotal,
                ppnPercent = Constants.PPN_DEFAULT,
                ppnAmount = state.ppnAmount,
                total = state.total,
                paymentMethod = paymentMethod,
                amountPaid = amountPaid,
                changeAmount = amountPaid - state.total,
                deliveryType = deliveryType,
                storeId = storeId,
                items = items
            )

            transactionRepository.createTransaction(transaction)
                .onSuccess {
                    state.cart.forEach { cartItem ->
                        productRepository.decreaseStock(cartItem.product.id, cartItem.variant?.id, cartItem.quantity)
                    }
                    _uiState.value = _uiState.value.copy(
                        cart = emptyList(),
                        showCheckout = false,
                        checkoutSuccess = true
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(checkoutSuccess = false)
    }

    fun scanBarcode(barcode: String) {
        viewModelScope.launch {
            val product = productRepository.getByBarcode(barcode, storeId)
            if (product != null) {
                addToCart(product)
            } else {
                _uiState.value = _uiState.value.copy(error = "Produk dengan barcode $barcode tidak ditemukan")
            }
        }
    }
}
