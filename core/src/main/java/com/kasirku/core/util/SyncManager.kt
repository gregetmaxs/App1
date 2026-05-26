package com.kasirku.core.util

import android.content.Context
import com.kasirku.core.repository.CategoryRepository
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.repository.ProductRepository
import com.kasirku.core.repository.RoleRepository
import com.kasirku.core.repository.StoreRepository
import com.kasirku.core.repository.SupplierRepository
import com.kasirku.core.repository.TransactionRepository
import com.kasirku.core.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val licenseRepository: LicenseRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val supplierRepository: SupplierRepository,
    private val roleRepository: RoleRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startObserving() {
        scope.launch {
            NetworkMonitor.observe(context).collectLatest { isOnline ->
                if (isOnline) syncAll()
            }
        }
    }

    suspend fun syncAll() {
        runCatching {
            licenseRepository.syncToCloud()
            storeRepository.syncToCloud()
            userRepository.syncToCloud()
            categoryRepository.syncToCloud()
            supplierRepository.syncToCloud()
            productRepository.syncToCloud()
            transactionRepository.syncToCloud()
            roleRepository.syncToCloud()
        }
    }

    suspend fun pullAll() {
        runCatching {
            licenseRepository.syncFromCloud()
            storeRepository.syncFromCloud()
            userRepository.syncFromCloud()
            categoryRepository.syncFromCloud()
            supplierRepository.syncFromCloud()
            productRepository.syncFromCloud()
            transactionRepository.syncFromCloud()
            roleRepository.syncFromCloud()
        }
    }
}
