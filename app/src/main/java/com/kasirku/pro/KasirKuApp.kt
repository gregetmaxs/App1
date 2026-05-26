package com.kasirku.pro

import android.app.Application
import android.util.Log
import com.kasirku.pro.data.database.AppDatabase
import com.kasirku.pro.util.NetworkMonitor
import com.kasirku.pro.util.ThemeManager

class KasirKuApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val networkMonitor: NetworkMonitor by lazy { NetworkMonitor(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ThemeManager.applyTheme(this)
        try {
            database.openHelper.readableDatabase
        } catch (e: Exception) {
            Log.e("KasirKuApp", "Database init: ${e.message}")
        }
    }

    companion object {
        lateinit var instance: KasirKuApp
            private set
    }
}
