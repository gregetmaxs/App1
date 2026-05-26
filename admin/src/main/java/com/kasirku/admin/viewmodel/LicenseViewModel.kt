package com.kasirku.admin.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.admin.AdminApp
import com.kasirku.admin.data.entity.License
import com.kasirku.admin.data.entity.LicenseHistory
import com.kasirku.admin.data.entity.Store
import com.kasirku.admin.util.LicenseGenerator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LicenseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as AdminApp).database
    private val licenseDao = database.licenseDao()
    private val storeDao = database.storeDao()
    private val historyDao = database.licenseHistoryDao()
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val allLicenses = licenseDao.getAllLicenses()
    val allStores = storeDao.getAllStores()

    private val _generateResult = MutableLiveData<Result<License>>()
    val generateResult: LiveData<Result<License>> = _generateResult

    private val _toggleResult = MutableLiveData<Result<License>>()
    val toggleResult: LiveData<Result<License>> = _toggleResult

    fun generateLicense(
        storeName: String,
        ownerName: String,
        ownerEmail: String,
        ownerPhone: String,
        password: String,
        price: Double,
        address: String,
        durationDays: Long = -1L
    ) {
        viewModelScope.launch {
            try {
                val licenseKey = LicenseGenerator.generate()
                val now = System.currentTimeMillis()
                val expiresAt = if (durationDays > 0) {
                    now + (durationDays * 24 * 60 * 60 * 1000)
                } else {
                    0L
                }

                val authResult = firebaseAuth.createUserWithEmailAndPassword(ownerEmail, password).await()
                val uid = authResult.user?.uid ?: throw Exception("Gagal membuat akun Firebase")

                val store = Store(
                    name = storeName,
                    ownerName = ownerName,
                    email = ownerEmail,
                    phone = ownerPhone,
                    address = address,
                    licenseKey = licenseKey,
                    firebaseUid = uid
                )
                storeDao.insert(store)

                val license = License(
                    licenseKey = licenseKey,
                    storeId = store.id,
                    storeName = storeName,
                    ownerName = ownerName,
                    ownerEmail = ownerEmail,
                    ownerPhone = ownerPhone,
                    durationDays = durationDays,
                    price = price,
                    expiresAt = expiresAt
                )
                licenseDao.insert(license)

                firestore.collection("licenses").document(license.id).set(
                    mapOf(
                        "licenseKey" to licenseKey,
                        "storeId" to store.id,
                        "storeName" to storeName,
                        "ownerEmail" to ownerEmail,
                        "firebaseUid" to uid,
                        "isActive" to true,
                        "durationDays" to durationDays,
                        "expiresAt" to expiresAt,
                        "createdAt" to license.createdAt
                    )
                ).await()

                historyDao.insert(
                    LicenseHistory(
                        licenseId = license.id,
                        action = "CREATED",
                        details = "License $licenseKey dibuat untuk $storeName"
                    )
                )

                _generateResult.postValue(Result.success(license))
            } catch (e: Exception) {
                _generateResult.postValue(Result.failure(e))
            }
        }
    }

    fun toggleLicense(license: License) {
        viewModelScope.launch {
            try {
                val updated = license.copy(
                    isActive = !license.isActive,
                    updatedAt = System.currentTimeMillis(),
                    isSynced = false
                )
                licenseDao.update(updated)

                firestore.collection("licenses").document(license.id)
                    .update("isActive", updated.isActive).await()

                val action = if (updated.isActive) "ACTIVATED" else "DEACTIVATED"
                historyDao.insert(
                    LicenseHistory(
                        licenseId = license.id,
                        action = action,
                        details = "License ${license.licenseKey} $action"
                    )
                )

                _toggleResult.postValue(Result.success(updated))
            } catch (e: Exception) {
                _toggleResult.postValue(Result.failure(e))
            }
        }
    }

    fun updateStore(store: Store) {
        viewModelScope.launch {
            storeDao.update(store.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun searchLicenses(query: String) = licenseDao.searchLicenses(query)
    fun searchStores(query: String) = storeDao.searchStores(query)

    suspend fun getLicenseById(id: String) = licenseDao.getLicenseById(id)
    suspend fun getStoreById(id: String) = storeDao.getStoreById(id)
}
