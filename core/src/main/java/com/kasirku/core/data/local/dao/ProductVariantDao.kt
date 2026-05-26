package com.kasirku.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kasirku.core.data.local.entity.ProductVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantDao {
    @Query("SELECT * FROM product_variants WHERE productId = :productId AND isActive = 1 ORDER BY name ASC")
    fun getByProductId(productId: String): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE id = :id")
    suspend fun getById(id: String): ProductVariantEntity?

    @Query("SELECT * FROM product_variants WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ProductVariantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: ProductVariantEntity)

    @Update
    suspend fun update(variant: ProductVariantEntity)

    @Query("UPDATE product_variants SET stock = stock - :qty WHERE id = :id")
    suspend fun decreaseStock(id: String, qty: Int)

    @Query("UPDATE product_variants SET stock = stock + :qty WHERE id = :id")
    suspend fun increaseStock(id: String, qty: Int)

    @Query("SELECT * FROM product_variants WHERE isSynced = 0")
    suspend fun getUnsynced(): List<ProductVariantEntity>

    @Query("UPDATE product_variants SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM product_variants WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)
}
