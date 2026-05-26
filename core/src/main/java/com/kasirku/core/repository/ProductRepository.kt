package com.kasirku.core.repository

import com.kasirku.core.model.Product
import com.kasirku.core.model.ProductVariant
import com.kasirku.core.model.PriceHistory
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllByStore(storeId: String): Flow<List<Product>>
    fun getByCategory(storeId: String, categoryId: String): Flow<List<Product>>
    fun search(storeId: String, query: String): Flow<List<Product>>
    suspend fun getById(id: String): Product?
    suspend fun getByBarcode(barcode: String, storeId: String): Product?
    suspend fun createProduct(product: Product, variants: List<ProductVariant>): Result<Product>
    suspend fun updateProduct(product: Product, variants: List<ProductVariant>): Result<Unit>
    suspend fun decreaseStock(productId: String, variantId: String?, quantity: Int)
    suspend fun increaseStock(productId: String, variantId: String?, quantity: Int)
    fun getPriceHistory(productId: String): Flow<List<PriceHistory>>
    suspend fun addPriceHistory(priceHistory: PriceHistory)
    suspend fun syncToCloud()
    suspend fun syncFromCloud()
}
