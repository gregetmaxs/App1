package com.kasirku.admin

import android.app.Application
import com.kasirku.admin.data.database.AdminDatabase

class AdminApp : Application() {
    val database: AdminDatabase by lazy { AdminDatabase.getDatabase(this) }
}
