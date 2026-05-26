package com.kasirku.pro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kasirku.pro.data.dao.*
import com.kasirku.pro.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Item::class,
        Category::class,
        Transaction::class,
        TransactionItem::class,
        Customer::class,
        Role::class,
        AppSettings::class,
        ItemVariant::class,
        PriceHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun customerDao(): CustomerDao
    abstract fun roleDao(): RoleDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun itemVariantDao(): ItemVariantDao
    abstract fun priceHistoryDao(): PriceHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kasirku_pro_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val roleCount = database.roleDao().getRoleCount()
                                if (roleCount == 0) {
                                    database.roleDao().insertAll(Role.createDefaultRoles())
                                }
                                val settings = database.appSettingsDao().getSettingsSync()
                                if (settings == null) {
                                    database.appSettingsDao().insert(AppSettings())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
