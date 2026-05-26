package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.*
import com.kasirku.pro.data.repository.CategoryRepository
import com.kasirku.pro.data.repository.ItemRepository
import com.kasirku.pro.data.repository.TransactionRepository
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.util.ResiGenerator
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class PosViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val itemRepository = ItemRepository(database.itemDao())
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val transactionRepository = TransactionRepository(database.transactionDao())
    private val sessionManager = SessionManager(application)
    private val syncManager = SyncManager(database, app.networkMonitor)

    val allItems = itemRepository.getAllActiveItems()
    val categories = categoryRepository.getAllCategories()
    val recentItems = itemRepository.getRecentItems()

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

    private val _searchQuery = MutableLiveData<String>()

    val filteredItems: LiveData<List<Item>> = MediatorLiveData<List<Item>>().apply {
        addSource(allItems) { items ->
            value = filterItems(items, _selectedCategory.value, _searchQuery.value)
        }
        addSource(_selectedCategory) { category ->
            value = filterItems(allItems.value, category, _searchQuery.value)
        }
        addSource(_searchQuery) { query ->
            value = filterItems(allItems.value, _selectedCategory.value, query)
        }
    }

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    val totalBill: LiveData<String> = _cartItems.map { items ->
        val total = items.sumOf { it.subtotal }
        CurrencyFormatter.formatSimple(total)
    }

    val totalBillDouble: LiveData<Double> = _cartItems.map { items ->
        items.sumOf { it.subtotal }
    }

    private val _checkoutResult = MutableLiveData<Result<Transaction>>()
    val checkoutResult: LiveData<Result<Transaction>> = _checkoutResult

    fun selectCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
    }

    fun searchItems(query: String) {
        _searchQuery.value = query
    }

    fun addToCart(item: Item) {
        val currentCart = _cartItems.value ?: mutableListOf()
        val existingItem = currentCart.find { it.item.id == item.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(item, 1))
        }
        _cartItems.value = currentCart
    }

    fun removeFromCart(position: Int) {
        val currentCart = _cartItems.value ?: mutableListOf()
        if (position in currentCart.indices) {
            currentCart.removeAt(position)
            _cartItems.value = currentCart
        }
    }

    fun updateCartQuantity(position: Int, quantity: Int) {
        val currentCart = _cartItems.value ?: mutableListOf()
        if (position in currentCart.indices) {
            if (quantity <= 0) {
                currentCart.removeAt(position)
            } else {
                currentCart[position].quantity = quantity
            }
            _cartItems.value = currentCart
        }
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }

    fun checkout(
        customerName: String,
        paidAmount: Double,
        notes: String,
        deliveryType: DeliveryType,
        deliveryAddress: String,
        driverId: String,
        driverName: String,
        helperId: String,
        helperName: String
    ) {
        viewModelScope.launch {
            try {
                val cart = _cartItems.value ?: emptyList()
                if (cart.isEmpty()) {
                    _checkoutResult.value = Result.failure(Exception("Keranjang kosong"))
                    return@launch
                }

                val total = cart.sumOf { it.subtotal }
                val change = paidAmount - total

                if (paidAmount < total) {
                    _checkoutResult.value = Result.failure(Exception("Pembayaran kurang"))
                    return@launch
                }

                val resiNumber = if (deliveryType == DeliveryType.DELIVERY) {
                    ResiGenerator.generate()
                } else ""

                val transaction = Transaction(
                    type = TransactionType.SALE,
                    customerName = customerName,
                    totalAmount = total,
                    paidAmount = paidAmount,
                    changeAmount = change,
                    userId = sessionManager.getUserId(),
                    userName = sessionManager.getUserName(),
                    notes = notes,
                    resiNumber = resiNumber,
                    deliveryType = deliveryType,
                    deliveryAddress = deliveryAddress,
                    driverId = driverId,
                    driverName = driverName,
                    helperId = helperId,
                    helperName = helperName,
                    deliveryStatus = if (deliveryType == DeliveryType.DELIVERY) DeliveryStatus.WAITING else DeliveryStatus.DELIVERED
                )

                val transactionItems = cart.map { cartItem ->
                    TransactionItem(
                        transactionId = transaction.id,
                        itemId = cartItem.item.id,
                        itemName = cartItem.item.name,
                        quantity = cartItem.quantity,
                        price = cartItem.item.price,
                        subtotal = cartItem.subtotal
                    )
                }

                transactionRepository.insertFullTransaction(transaction, transactionItems)

                for (cartItem in cart) {
                    itemRepository.decreaseStock(cartItem.item.id, cartItem.quantity)
                }

                clearCart()
                _checkoutResult.value = Result.success(transaction)

                syncManager.syncAll()
            } catch (e: Exception) {
                _checkoutResult.value = Result.failure(e)
            }
        }
    }

    private fun filterItems(items: List<Item>?, categoryId: String?, query: String?): List<Item> {
        var result = items ?: emptyList()
        if (!categoryId.isNullOrEmpty()) {
            result = result.filter { it.categoryId == categoryId }
        }
        if (!query.isNullOrEmpty()) {
            result = result.filter { it.name.contains(query, ignoreCase = true) }
        }
        return result
    }
}
