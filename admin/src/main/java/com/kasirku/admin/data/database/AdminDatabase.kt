package com.kasirku.admin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kasirku.admin.data.dao.LicenseDao
import com.kasirku.admin.data.dao.LicenseHistoryDao
import com.kasirku.admin.data.dao.StoreDao
import com.kasirku.admin.data.entity.License
import com.kasirku.admin.data.entity.LicenseHistory
import com.kasirku.admin.data.entity.Store

@Database(
    entities = [License::class, Store::class, LicenseHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AdminDatabase : RoomDatabase() {

    abstract fun licenseDao(): LicenseDao
    abstract fun storeDao(): StoreDao
    abstract fun licenseHistoryDao(): LicenseHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AdminDatabase? = null

        fun getDatabase(context: Context): AdminDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdminDatabase::class.java,
                    "kasirku_admin_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
