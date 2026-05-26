package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.Category
import com.kasirku.core.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun loadAll(storeId: String) {
        viewModelScope.launch {
            categoryRepository.getAllByStore(storeId).collect { _categories.value = it }
        }
    }

    fun create(name: String, storeId: String) {
        viewModelScope.launch {
            categoryRepository.create(Category(id = UUID.randomUUID().toString(), name = name, storeId = storeId))
            _saved.value = true
        }
    }

    fun delete(id: String) {
        viewModelScope.launch { categoryRepository.delete(id) }
    }

    fun clearSaved() { _saved.value = false }
}
