package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.ItemVariant

@Dao
interface ItemVariantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: ItemVariant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(variants: List<ItemVariant>)

    @Update
    suspend fun update(variant: ItemVariant)

    @Delete
    suspend fun delete(variant: ItemVariant)

    @Query("SELECT * FROM item_variants WHERE itemId = :itemId AND isActive = 1 ORDER BY name ASC")
    fun getVariantsByItemId(itemId: String): LiveData<List<ItemVariant>>

    @Query("SELECT * FROM item_variants WHERE itemId = :itemId AND isActive = 1 ORDER BY name ASC")
    suspend fun getVariantsByItemIdList(itemId: String): List<ItemVariant>

    @Query("SELECT * FROM item_variants WHERE id = :id")
    suspend fun getVariantById(id: String): ItemVariant?

    @Query("SELECT * FROM item_variants WHERE barcode = :barcode AND isActive = 1 LIMIT 1")
    suspend fun getVariantByBarcode(barcode: String): ItemVariant?

    @Query("UPDATE item_variants SET stock = stock - :quantity WHERE id = :variantId")
    suspend fun decreaseStock(variantId: String, quantity: Int)

    @Query("UPDATE item_variants SET stock = stock + :quantity WHERE id = :variantId")
    suspend fun increaseStock(variantId: String, quantity: Int)

    @Query("SELECT * FROM item_variants WHERE isSynced = 0")
    suspend fun getUnsyncedVariants(): List<ItemVariant>

    @Query("UPDATE item_variants SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
