package com.kasirku.core.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kasirku.core.data.local.database.KasirKuDatabase
import com.kasirku.core.data.remote.AuthRepositoryImpl
import com.kasirku.core.data.remote.CategoryRepositoryImpl
import com.kasirku.core.data.remote.LicenseRepositoryImpl
import com.kasirku.core.data.remote.ProductRepositoryImpl
import com.kasirku.core.data.remote.RoleRepositoryImpl
import com.kasirku.core.data.remote.StoreRepositoryImpl
import com.kasirku.core.data.remote.SupplierRepositoryImpl
import com.kasirku.core.data.remote.TransactionRepositoryImpl
import com.kasirku.core.data.remote.UserRepositoryImpl
import com.kasirku.core.repository.AuthRepository
import com.kasirku.core.repository.CategoryRepository
import com.kasirku.core.repository.LicenseRepository
import com.kasirku.core.repository.ProductRepository
import com.kasirku.core.repository.RoleRepository
import com.kasirku.core.repository.StoreRepository
import com.kasirku.core.repository.SupplierRepository
import com.kasirku.core.repository.TransactionRepository
import com.kasirku.core.repository.UserRepository
import com.kasirku.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KasirKuDatabase =
        Room.databaseBuilder(context, KasirKuDatabase::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    // DAOs
    @Provides fun provideUserDao(db: KasirKuDatabase) = db.userDao()
    @Provides fun provideStoreDao(db: KasirKuDatabase) = db.storeDao()
    @Provides fun provideLicenseDao(db: KasirKuDatabase) = db.licenseDao()
    @Provides fun provideLicensePriceDao(db: KasirKuDatabase) = db.licensePriceDao()
    @Provides fun provideCategoryDao(db: KasirKuDatabase) = db.categoryDao()
    @Provides fun provideSupplierDao(db: KasirKuDatabase) = db.supplierDao()
    @Provides fun provideProductDao(db: KasirKuDatabase) = db.productDao()
    @Provides fun provideProductVariantDao(db: KasirKuDatabase) = db.productVariantDao()
    @Provides fun provideTransactionDao(db: KasirKuDatabase) = db.transactionDao()
    @Provides fun provideTransactionItemDao(db: KasirKuDatabase) = db.transactionItemDao()
    @Provides fun providePriceHistoryDao(db: KasirKuDatabase) = db.priceHistoryDao()
    @Provides fun provideRoleDao(db: KasirKuDatabase) = db.roleDao()

    // Repositories
    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, userDao: com.kasirku.core.data.local.dao.UserDao): AuthRepository =
        AuthRepositoryImpl(auth, firestore, userDao)

    @Provides
    @Singleton
    fun provideLicenseRepository(firestore: FirebaseFirestore, licenseDao: com.kasirku.core.data.local.dao.LicenseDao, licensePriceDao: com.kasirku.core.data.local.dao.LicensePriceDao): LicenseRepository =
        LicenseRepositoryImpl(firestore, licenseDao, licensePriceDao)

    @Provides
    @Singleton
    fun provideStoreRepository(firestore: FirebaseFirestore, storeDao: com.kasirku.core.data.local.dao.StoreDao): StoreRepository =
        StoreRepositoryImpl(firestore, storeDao)

    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, userDao: com.kasirku.core.data.local.dao.UserDao): UserRepository =
        UserRepositoryImpl(auth, firestore, userDao)

    @Provides
    @Singleton
    fun provideProductRepository(
        firestore: FirebaseFirestore,
        productDao: com.kasirku.core.data.local.dao.ProductDao,
        variantDao: com.kasirku.core.data.local.dao.ProductVariantDao,
        categoryDao: com.kasirku.core.data.local.dao.CategoryDao,
        supplierDao: com.kasirku.core.data.local.dao.SupplierDao,
        priceHistoryDao: com.kasirku.core.data.local.dao.PriceHistoryDao
    ): ProductRepository =
        ProductRepositoryImpl(firestore, productDao, variantDao, categoryDao, supplierDao, priceHistoryDao)

    @Provides
    @Singleton
    fun provideTransactionRepository(firestore: FirebaseFirestore, transactionDao: com.kasirku.core.data.local.dao.TransactionDao, transactionItemDao: com.kasirku.core.data.local.dao.TransactionItemDao): TransactionRepository =
        TransactionRepositoryImpl(firestore, transactionDao, transactionItemDao)

    @Provides
    @Singleton
    fun provideCategoryRepository(firestore: FirebaseFirestore, categoryDao: com.kasirku.core.data.local.dao.CategoryDao): CategoryRepository =
        CategoryRepositoryImpl(firestore, categoryDao)

    @Provides
    @Singleton
    fun provideSupplierRepository(firestore: FirebaseFirestore, supplierDao: com.kasirku.core.data.local.dao.SupplierDao): SupplierRepository =
        SupplierRepositoryImpl(firestore, supplierDao)

    @Provides
    @Singleton
    fun provideRoleRepository(firestore: FirebaseFirestore, roleDao: com.kasirku.core.data.local.dao.RoleDao): RoleRepository =
        RoleRepositoryImpl(firestore, roleDao)
}
