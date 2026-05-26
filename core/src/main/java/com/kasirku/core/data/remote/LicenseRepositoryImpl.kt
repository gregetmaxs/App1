package com.kasirku.core.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.dao.LicenseDao
import com.kasirku.core.data.local.dao.LicensePriceDao
import com.kasirku.core.model.License
import com.kasirku.core.model.LicensePrice
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.util.Constants
import com.kasirku.core.util.toEntity
import com.kasirku.core.util.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class LicenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val licenseDao: LicenseDao,
    private val licensePriceDao: LicensePriceDao
) : LicenseRepository {

    override fun getAllLicenses(): Flow<List<License>> =
        licenseDao.getAll().map { list -> list.map { it.toModel() } }

    override suspend fun getLicenseByStoreId(storeId: String): License? =
        licenseDao.getByStoreId(storeId)?.toModel()

    override fun observeLicenseByStoreId(storeId: String): Flow<License?> =
        licenseDao.observeByStoreId(storeId).map { it?.toModel() }

    override suspend fun generateLicense(storeId: String, type: String, createdBy: String, price: Long): Result<License> = runCatching {
        val key = generateLicenseKey()
        val durationDays = Constants.LICENSE_DURATIONS[type] ?: throw Exception("Tipe license tidak valid")
        val now = System.currentTimeMillis()
        val expiresAt = if (durationDays == -1L) 0L else now + (durationDays * 24 * 60 * 60 * 1000)

        val license = License(
            id = UUID.randomUUID().toString(),
            licenseKey = key,
            storeId = storeId,
            type = type,
            status = "ACTIVE",
            activatedAt = now,
            expiresAt = expiresAt,
            createdBy = createdBy,
            price = price,
            createdAt = now
        )
        licenseDao.insert(license.toEntity())

        firestore.collection(Constants.FIRESTORE_LICENSES)
            .document(license.id)
            .set(license).await()
        licenseDao.markSynced(license.id)

        license
    }

    override suspend fun validateLicense(storeId: String): Boolean {
        val license = licenseDao.getByStoreId(storeId)?.toModel() ?: return false
        if (license.isPermanent) return true
        if (license.isExpired) {
            licenseDao.update(license.copy(status = "EXPIRED").toEntity())
            return false
        }
        return true
    }

    override suspend fun revokeLicense(licenseId: String): Result<Unit> = runCatching {
        val license = licenseDao.getById(licenseId)?.toModel() ?: throw Exception("License tidak ditemukan")
        val revoked = license.copy(status = "REVOKED")
        licenseDao.update(revoked.toEntity())
        firestore.collection(Constants.FIRESTORE_LICENSES).document(licenseId)
            .update("status", "REVOKED").await()
    }

    override fun getAllPrices(): Flow<List<LicensePrice>> =
        licensePriceDao.getAll().map { list -> list.map { it.toModel() } }

    override suspend fun updatePrice(price: LicensePrice): Result<Unit> = runCatching {
        licensePriceDao.insert(price.toEntity())
        firestore.collection(Constants.FIRESTORE_LICENSE_PRICES)
            .document(price.type).set(price).await()
        licensePriceDao.markSynced(price.type)
    }

    override suspend fun initDefaultPrices() {
        val defaults = listOf(
            LicensePrice("3_DAYS", "3 Hari", 10000),
            LicensePrice("7_DAYS", "7 Hari", 25000),
            LicensePrice("14_DAYS", "14 Hari", 45000),
            LicensePrice("30_DAYS", "30 Hari", 75000),
            LicensePrice("1_YEAR", "1 Tahun", 500000),
            LicensePrice("2_YEARS", "2 Tahun", 900000),
            LicensePrice("PERMANENT", "Permanent", 1500000)
        )
        licensePriceDao.insertAll(defaults.map { it.toEntity() })
    }

    override fun countActiveFlow(): Flow<Int> = licenseDao.countByStatus("ACTIVE")
    override fun countExpiredFlow(): Flow<Int> = licenseDao.countByStatus("EXPIRED")

    override suspend fun syncToCloud() {
        licenseDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_LICENSES)
                    .document(entity.id).set(entity.toModel()).await()
                licenseDao.markSynced(entity.id)
            }
        }
        licensePriceDao.getUnsynced().forEach { entity ->
            runCatching {
                firestore.collection(Constants.FIRESTORE_LICENSE_PRICES)
                    .document(entity.type).set(entity.toModel()).await()
                licensePriceDao.markSynced(entity.type)
            }
        }
    }

    override suspend fun syncFromCloud() {
        runCatching {
            val snapshot = firestore.collection(Constants.FIRESTORE_LICENSES).get().await()
            snapshot.documents.forEach { doc ->
                doc.toObject(License::class.java)?.let { license ->
                    licenseDao.insert(license.toEntity(isSynced = true))
                }
            }
            val priceSnapshot = firestore.collection(Constants.FIRESTORE_LICENSE_PRICES).get().await()
            priceSnapshot.documents.forEach { doc ->
                doc.toObject(LicensePrice::class.java)?.let { price ->
                    licensePriceDao.insert(price.toEntity(isSynced = true))
                }
            }
        }
    }

    private fun generateLicenseKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        fun block() = (1..4).map { chars.random() }.joinToString("")
        return "KKU-${block()}-${block()}-${block()}"
    }
}
