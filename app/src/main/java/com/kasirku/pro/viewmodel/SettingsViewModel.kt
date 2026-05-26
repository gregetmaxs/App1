package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.AppSettings
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val settingsDao = database.appSettingsDao()
    private val syncManager = SyncManager(database, app.networkMonitor)

    val settings = settingsDao.getSettings()
    val isDeliveryEnabled = settingsDao.isDeliveryEnabled()

    private val _saveResult = MutableLiveData<Result<AppSettings>>()
    val saveResult: LiveData<Result<AppSettings>> = _saveResult

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            try {
                settingsDao.insert(settings.copy(updatedAt = System.currentTimeMillis()))
                _saveResult.value = Result.success(settings)
                syncManager.syncAll()
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun toggleDelivery(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsDao.getSettingsSync() ?: AppSettings()
            settingsDao.insert(current.copy(deliveryEnabled = enabled, updatedAt = System.currentTimeMillis()))
            syncManager.syncAll()
        }
    }

    fun updateStoreInfo(name: String, address: String, phone: String) {
        viewModelScope.launch {
            val current = settingsDao.getSettingsSync() ?: AppSettings()
            settingsDao.insert(
                current.copy(
                    storeName = name,
                    storeAddress = address,
                    storePhone = phone,
                    updatedAt = System.currentTimeMillis(),
                    isSynced = false
                )
            )
            syncManager.syncAll()
        }
    }
}
