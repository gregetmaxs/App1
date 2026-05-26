package com.kasirku.pro.util

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object LicenseManager {

    private const val PREFS_NAME = "kasirku_license"
    private const val KEY_LICENSE_KEY = "license_key"
    private const val KEY_IS_ACTIVE = "is_active"
    private const val KEY_EXPIRES_AT = "expires_at"
    private const val KEY_LAST_CHECK = "last_check"

    const val DURATION_7_DAYS = 7L
    const val DURATION_14_DAYS = 14L
    const val DURATION_30_DAYS = 30L
    const val DURATION_PERMANENT = -1L

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    suspend fun checkLicense(context: Context): LicenseStatus {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return LicenseStatus.NO_USER

        return try {
            val firestore = FirebaseFirestore.getInstance()
            val docs = firestore.collection("licenses")
                .whereEqualTo("firebaseUid", uid)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            if (docs.isEmpty) {
                val docsByStore = firestore.collection("licenses")
                    .whereEqualTo("ownerEmail", FirebaseAuth.getInstance().currentUser?.email)
                    .whereEqualTo("isActive", true)
                    .get()
                    .await()

                if (docsByStore.isEmpty) {
                    saveLicenseLocal(context, "", false, 0L)
                    return LicenseStatus.NO_LICENSE
                }

                val license = docsByStore.documents.first()
                return processLicenseDoc(context, license)
            }

            val license = docs.documents.first()
            processLicenseDoc(context, license)
        } catch (e: Exception) {
            checkLocalLicense(context)
        }
    }

    private fun processLicenseDoc(context: Context, license: com.google.firebase.firestore.DocumentSnapshot): LicenseStatus {
        val isActive = license.getBoolean("isActive") ?: false
        val expiresAt = license.getLong("expiresAt") ?: 0L
        val licenseKey = license.getString("licenseKey") ?: ""

        saveLicenseLocal(context, licenseKey, isActive, expiresAt)

        if (!isActive) return LicenseStatus.INACTIVE

        if (expiresAt > 0 && System.currentTimeMillis() > expiresAt) {
            return LicenseStatus.EXPIRED
        }

        return LicenseStatus.VALID
    }

    private fun checkLocalLicense(context: Context): LicenseStatus {
        val prefs = getPrefs(context)
        val isActive = prefs.getBoolean(KEY_IS_ACTIVE, false)
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)

        if (!isActive) return LicenseStatus.NO_LICENSE

        if (expiresAt > 0 && System.currentTimeMillis() > expiresAt) {
            return LicenseStatus.EXPIRED
        }

        return LicenseStatus.VALID
    }

    private fun saveLicenseLocal(context: Context, licenseKey: String, isActive: Boolean, expiresAt: Long) {
        getPrefs(context).edit().apply {
            putString(KEY_LICENSE_KEY, licenseKey)
            putBoolean(KEY_IS_ACTIVE, isActive)
            putLong(KEY_EXPIRES_AT, expiresAt)
            putLong(KEY_LAST_CHECK, System.currentTimeMillis())
            apply()
        }
    }

    fun getLicenseKey(context: Context): String {
        return getPrefs(context).getString(KEY_LICENSE_KEY, "") ?: ""
    }

    fun getExpiresAt(context: Context): Long {
        return getPrefs(context).getLong(KEY_EXPIRES_AT, 0L)
    }

    enum class LicenseStatus {
        VALID,
        EXPIRED,
        INACTIVE,
        NO_LICENSE,
        NO_USER
    }
}
