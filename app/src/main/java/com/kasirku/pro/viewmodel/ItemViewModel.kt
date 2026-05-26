package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.Category
import com.kasirku.pro.data.entity.Item
import com.kasirku.pro.data.repository.CategoryRepository
import com.kasirku.pro.data.repository.ItemRepository
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val itemRepository = ItemRepository(database.itemDao())
    private val categoryRepository = CategoryRepository(database.categoryDao())
    private val syncManager = SyncManager(database, app.networkMonitor)

    val allItems = itemRepository.getAllActiveItems()
    val allCategories = categoryRepository.getAllCategories()

    private val _saveResult = MutableLiveData<Result<Item>>()
    val saveResult: LiveData<Result<Item>> = _saveResult

    private val _categoryResult = MutableLiveData<Result<Category>>()
    val categoryResult: LiveData<Result<Category>> = _categoryResult

    fun saveItem(item: Item) {
        viewModelScope.launch {
            try {
                val existing = itemRepository.getItemById(item.id)
                if (existing != null) {
                    itemRepository.update(item.copy(updatedAt = System.currentTimeMillis()))
                } else {
                    itemRepository.insert(item)
                }
                _saveResult.value = Result.success(item)
                syncManager.syncAll()
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.update(item.copy(isActive = false, updatedAt = System.currentTimeMillis()))
            syncManager.syncAll()
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.insert(category)
                _categoryResult.value = Result.success(category)
                syncManager.syncAll()
            } catch (e: Exception) {
                _categoryResult.value = Result.failure(e)
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
            syncManager.syncAll()
        }
    }

    suspend fun getItemById(id: String) = itemRepository.getItemById(id)
}
