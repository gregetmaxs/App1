package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.CategoryDao
import com.kasirku.core.data.local.dao.PriceHistoryDao
import com.kasirku.core.data.local.dao.ProductDao
import com.kasirku.core.data.local.dao.ProductVariantDao
import com.kasirku.core.data.local.dao.SupplierDao
import com.kasirku.core.model.PriceHistory
import com.kasirku.core.model.Product
import com.kasirku.core.model.ProductVariant
import com.kasirku.core.repository.ProductRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao,
    private val variantDao: ProductVariantDao,
    private val categoryDao: CategoryDao,
    private val supplierDao: SupplierDao,
    private val priceHistoryDao: PriceHistoryDao
) : ProductRepository {

    override fun getAllByStore(storeId: String): Flow<List<Product>> =
        productDao.getAllByStore(storeId).map { list ->
            list.map { entity ->
                val variants = variantDao.getByProductId(entity.id).first().map { it.toModel() }
                val catName = categoryDao.getById(entity.categoryId)?.name ?: ""
                val supName = supplierDao.getById(entity.supplierId)?.name ?: ""
                entity.toModel(variants, catName, supName)
            }
        }

    override fun getByCategory(storeId: String, categoryId: String): Flow<List<Product>> =
        productDao.getByCategory(storeId, categoryId).map { list ->
            list.map { it.toModel() }
        }

    override fun search(storeId: String, query: String): Flow<List<Product>> =
        productDao.search(storeId, query).map { list ->
            list.map { it.toModel() }
        }

    override suspend fun getById(id: String): Product? {
        val entity = productDao.getById(id) ?: return null
        val variants = variantDao.getByProductId(id).first().map { it.toModel() }
        val catName = categoryDao.getById(entity.categoryId)?.name ?: ""
        val supName = supplierDao.getById(entity.supplierId)?.name ?: ""
        return entity.toModel(variants, catName, supName)
    }

    override suspend fun getByBarcode(barcode: String, storeId: String): Product? {
        val entity = productDao.getByBarcode(barcode, storeId)
        if (entity != null) return entity.toModel()
        val variant = variantDao.getByBarcode(barcode)
        if (variant != null) {
            val product = productDao.getById(variant.productId)
            return product?.toModel(listOf(variant.toModel()))
        }
        return null
    }

    override suspend fun createProduct(product: Product, variants: List<ProductVariant>): Result<Product> = runCatching {
        productDao.insert(product.toEntity())
        variants.forEach { variantDao.insert(it.toEntity()) }
        firestore.collection(Constants.FIRESTORE_PRODUCTS).document(product.id).set(product).await()
        productDao.markSynced(product.id)
        product.copy(variants = variants)
    }

    override suspend fun updateProduct(product: Product, variants: List<ProductVariant>): Result<Unit> = runCatching {
        productDao.update(product.toEntity())
        variantDao.deleteByProductId(product.id)
        variants.forEach { variantDao.insert(it.toEntity()) }
        firestore.collection(Constants.FIRESTORE_PRODUCTS).document(product.id).set(product).await()
        productDao.markSynced(product.id)
    }

    override suspend fun decreaseStock(productId: String, variantId: String?, quantity: Int) {
        if (variantId != null) variantDao.decreaseStock(variantId, quantity)
        else productDao.decreaseStock(productId, quantity)
    }

    override suspend fun increaseStock(productId: String, variantId: String?, quantity: Int) {
        if (variantId != null) variantDao.increaseStock(variantId, quantity)
        else productDao.increaseStock(productId, quantity)
    }

    override fun getPriceHistory(productId: String): Flow<List<PriceHistory>> =
        priceHistoryDao.getByProductId(productId).map { list -> list.map { it.toModel() } }

    override suspend fun addPriceHistory(priceHistory: PriceHistory) {
        priceHistoryDao.insert(priceHistory.toEntity())
    }

    override suspend fun syncToCloud() {
        productDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_PRODUCTS)
                    .document(entity.id).set(entity.toModel()).await()
                productDao.markSynced(entity.id)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_PRODUCTS).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(Product::class.java)?.let { product ->
                    productDao.insert(product.toEntity(isSynced = true))
                }
            }
        }
    }
}
