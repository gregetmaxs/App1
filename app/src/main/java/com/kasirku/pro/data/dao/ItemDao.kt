package com.kasirku.pro.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kasirku.pro.data.entity.Item

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: String): Item?

    @Query("SELECT * FROM items WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isActive = 1 AND categoryId = :categoryId ORDER BY name ASC")
    fun getItemsByCategory(categoryId: String): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE isActive = 1 AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchItems(query: String): LiveData<List<Item>>

    @Query("UPDATE items SET stock = stock - :quantity WHERE id = :itemId")
    suspend fun decreaseStock(itemId: String, quantity: Int)

    @Query("UPDATE items SET stock = stock + :quantity WHERE id = :itemId")
    suspend fun increaseStock(itemId: String, quantity: Int)

    @Query("SELECT * FROM items WHERE isSynced = 0")
    suspend fun getUnsyncedItems(): List<Item>

    @Query("UPDATE items SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM items WHERE isActive = 1 ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentItems(limit: Int = 6): LiveData<List<Item>>
}
