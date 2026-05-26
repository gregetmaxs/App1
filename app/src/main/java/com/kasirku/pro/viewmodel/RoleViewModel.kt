package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.Role
import com.kasirku.pro.util.SyncManager
import kotlinx.coroutines.launch

class RoleViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KasirKuApp
    private val database = app.database
    private val roleDao = database.roleDao()
    private val syncManager = SyncManager(database, app.networkMonitor)

    val allRoles = roleDao.getAllRoles()

    private val _saveResult = MutableLiveData<Result<Role>>()
    val saveResult: LiveData<Result<Role>> = _saveResult

    fun saveRole(role: Role) {
        viewModelScope.launch {
            try {
                roleDao.insert(role)
                _saveResult.value = Result.success(role)
                syncManager.syncAll()
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun updateRole(role: Role) {
        viewModelScope.launch {
            try {
                roleDao.update(role.copy(updatedAt = System.currentTimeMillis()))
                _saveResult.value = Result.success(role)
                syncManager.syncAll()
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun deleteRole(role: Role) {
        viewModelScope.launch {
            if (!role.isDefault) {
                roleDao.delete(role)
                syncManager.syncAll()
            }
        }
    }

    suspend fun getRoleById(id: String) = roleDao.getRoleById(id)
    suspend fun getAllRolesList() = roleDao.getAllRolesList()
}
