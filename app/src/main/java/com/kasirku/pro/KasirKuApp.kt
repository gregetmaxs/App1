package com.kasirku.pro

import android.app.Application
import com.kasirku.pro.data.database.AppDatabase
import com.kasirku.pro.util.NetworkMonitor

class KasirKuApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val networkMonitor: NetworkMonitor by lazy { NetworkMonitor(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: KasirKuApp
            private set
    }
}
