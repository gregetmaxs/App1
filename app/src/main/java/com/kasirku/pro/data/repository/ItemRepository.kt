package com.kasirku.pro.data.repository

import com.kasirku.pro.data.dao.ItemDao
import com.kasirku.pro.data.entity.Item

class ItemRepository(private val itemDao: ItemDao) {

    fun getAllActiveItems() = itemDao.getAllActiveItems()
    fun getItemsByCategory(categoryId: String) = itemDao.getItemsByCategory(categoryId)
    fun searchItems(query: String) = itemDao.searchItems(query)
    fun getRecentItems(limit: Int = 6) = itemDao.getRecentItems(limit)

    suspend fun getItemById(id: String) = itemDao.getItemById(id)
    suspend fun insert(item: Item) = itemDao.insert(item)
    suspend fun update(item: Item) = itemDao.update(item)
    suspend fun delete(item: Item) = itemDao.delete(item)
    suspend fun decreaseStock(itemId: String, quantity: Int) = itemDao.decreaseStock(itemId, quantity)
    suspend fun increaseStock(itemId: String, quantity: Int) = itemDao.increaseStock(itemId, quantity)
    suspend fun getUnsyncedItems() = itemDao.getUnsyncedItems()
    suspend fun markAsSynced(id: String) = itemDao.markAsSynced(id)
}
