package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE storeId = :storeId AND isActive = 1 ORDER BY name ASC")
    fun getAllByStore(storeId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE storeId = :storeId AND categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
    fun getByCategory(storeId: String, categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE storeId = :storeId AND isActive = 1 AND (name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%') ORDER BY name ASC")
    fun search(storeId: String, query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode AND storeId = :storeId LIMIT 1")
    suspend fun getByBarcode(barcode: String, storeId: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET stock = stock - :qty WHERE id = :id")
    suspend fun decreaseStock(id: String, qty: Int)

    @Query("UPDATE products SET stock = stock + :qty WHERE id = :id")
    suspend fun increaseStock(id: String, qty: Int)

    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsynced(): List<ProductEntity>

    @Query("UPDATE products SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
