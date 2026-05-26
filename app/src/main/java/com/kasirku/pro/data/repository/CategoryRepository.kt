package com.kasirku.pro.data.repository

import com.kasirku.pro.data.dao.CategoryDao
import com.kasirku.pro.data.entity.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories() = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: String) = categoryDao.getCategoryById(id)
    suspend fun insert(category: Category) = categoryDao.insert(category)
    suspend fun update(category: Category) = categoryDao.update(category)
    suspend fun delete(category: Category) = categoryDao.delete(category)
    suspend fun getUnsyncedCategories() = categoryDao.getUnsyncedCategories()
    suspend fun markAsSynced(id: String) = categoryDao.markAsSynced(id)
}
