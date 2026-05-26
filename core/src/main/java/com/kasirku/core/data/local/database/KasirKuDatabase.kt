package com.kasirku.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kasirku.core.data.local.dao.CategoryDao
import com.kasirku.core.data.local.dao.LicenseDao
import com.kasirku.core.data.local.dao.LicensePriceDao
import com.kasirku.core.data.local.dao.PriceHistoryDao
import com.kasirku.core.data.local.dao.ProductDao
import com.kasirku.core.data.local.dao.ProductVariantDao
import com.kasirku.core.data.local.dao.RoleDao
import com.kasirku.core.data.local.dao.StoreDao
import com.kasirku.core.data.local.dao.SupplierDao
import com.kasirku.core.data.local.dao.TransactionDao
import com.kasirku.core.data.local.dao.TransactionItemDao
import com.kasirku.core.data.local.dao.UserDao
import com.kasirku.core.data.local.entity.CategoryEntity
import com.kasirku.core.data.local.entity.LicenseEntity
import com.kasirku.core.data.local.entity.LicensePriceEntity
import com.kasirku.core.data.local.entity.PriceHistoryEntity
import com.kasirku.core.data.local.entity.ProductEntity
import com.kasirku.core.data.local.entity.ProductVariantEntity
import com.kasirku.core.data.local.entity.RoleEntity
import com.kasirku.core.data.local.entity.StoreEntity
import com.kasirku.core.data.local.entity.SupplierEntity
import com.kasirku.core.data.local.entity.TransactionEntity
import com.kasirku.core.data.local.entity.TransactionItemEntity
import com.kasirku.core.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        StoreEntity::class,
        LicenseEntity::class,
        LicensePriceEntity::class,
        CategoryEntity::class,
        SupplierEntity::class,
        ProductEntity::class,
        ProductVariantEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        PriceHistoryEntity::class,
        RoleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KasirKuDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun storeDao(): StoreDao
    abstract fun licenseDao(): LicenseDao
    abstract fun licensePriceDao(): LicensePriceDao
    abstract fun categoryDao(): CategoryDao
    abstract fun supplierDao(): SupplierDao
    abstract fun productDao(): ProductDao
    abstract fun productVariantDao(): ProductVariantDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionItemDao(): TransactionItemDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun roleDao(): RoleDao
}
