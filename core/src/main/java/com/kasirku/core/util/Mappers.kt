package com.kasirku.core.util

import com.kasirku.core.data.local.entity.*
import com.kasirku.core.model.*

// User
fun UserEntity.toModel() = User(id, email, fullName, photoUrl, phone, nik, address, roleId, storeId, isActive, createdAt)
fun User.toEntity(isSynced: Boolean = false) = UserEntity(id, email, fullName, photoUrl, phone, nik, address, roleId, storeId, isActive, createdAt, System.currentTimeMillis(), isSynced)

// Store
fun StoreEntity.toModel() = Store(id, name, address, phone, ownerName, ownerEmail, licenseId, isDeliveryFullMode, createdAt)
fun Store.toEntity(isSynced: Boolean = false) = StoreEntity(id, name, address, phone, ownerName, ownerEmail, licenseId, isDeliveryFullMode, createdAt, System.currentTimeMillis(), isSynced)

// License
fun LicenseEntity.toModel() = License(id, licenseKey, storeId, type, status, activatedAt, expiresAt, createdBy, price, createdAt)
fun License.toEntity(isSynced: Boolean = false) = LicenseEntity(id, licenseKey, storeId, type, status, activatedAt, expiresAt, createdBy, price, createdAt, System.currentTimeMillis(), isSynced)

// LicensePrice
fun LicensePriceEntity.toModel() = LicensePrice(type, label, price)
fun LicensePrice.toEntity(isSynced: Boolean = false) = LicensePriceEntity(type, label, price, System.currentTimeMillis(), isSynced)

// Category
fun CategoryEntity.toModel() = Category(id, name, storeId)
fun Category.toEntity(isSynced: Boolean = false) = CategoryEntity(id, name, storeId, System.currentTimeMillis(), System.currentTimeMillis(), isSynced)

// Supplier
fun SupplierEntity.toModel() = Supplier(id, name, phone, address, storeId)
fun Supplier.toEntity(isSynced: Boolean = false) = SupplierEntity(id, name, phone, address, storeId, System.currentTimeMillis(), System.currentTimeMillis(), isSynced)

// Product
fun ProductEntity.toModel(variants: List<ProductVariant> = emptyList(), categoryName: String = "", supplierName: String = "") =
    Product(id, name, description, barcode, imageUrl, categoryId, supplierId, buyPrice, sellPrice, stock, minStock, unit, hasVariants, storeId, isActive, variants, categoryName, supplierName)
fun Product.toEntity(isSynced: Boolean = false) =
    ProductEntity(id, name, description, barcode, imageUrl, categoryId, supplierId, buyPrice, sellPrice, stock, minStock, unit, hasVariants, storeId, isActive, System.currentTimeMillis(), System.currentTimeMillis(), isSynced)

// ProductVariant
fun ProductVariantEntity.toModel() = ProductVariant(id, productId, name, barcode, buyPrice, sellPrice, stock, isActive)
fun ProductVariant.toEntity(isSynced: Boolean = false) = ProductVariantEntity(id, productId, name, barcode, buyPrice, sellPrice, stock, isActive, System.currentTimeMillis(), System.currentTimeMillis(), isSynced)

// Transaction
fun TransactionEntity.toModel(items: List<TransactionItem> = emptyList()) =
    Transaction(id, transactionNumber, cashierUserId, cashierName, customerName, subtotal, ppnPercent, ppnAmount, discount, total, paymentMethod, amountPaid, changeAmount, deliveryType, deliveryAddress, driverUserId, helperUserId, deliveryStatus, deliveryProofUrl, resiNumber, notes, storeId, items, createdAt)
fun Transaction.toEntity(isSynced: Boolean = false) =
    TransactionEntity(id, transactionNumber, cashierUserId, cashierName, customerName, subtotal, ppnPercent, ppnAmount, discount, total, paymentMethod, amountPaid, changeAmount, deliveryType, deliveryAddress, driverUserId, helperUserId, deliveryStatus, deliveryProofUrl, resiNumber, notes, storeId, createdAt, System.currentTimeMillis(), isSynced)

// TransactionItem
fun TransactionItemEntity.toModel() = TransactionItem(id, transactionId, productId, variantId, productName, variantName, quantity, price, subtotal)
fun TransactionItem.toEntity(isSynced: Boolean = false) = TransactionItemEntity(id, transactionId, productId, variantId, productName, variantName, quantity, price, subtotal, isSynced)

// PriceHistory
fun PriceHistoryEntity.toModel() = PriceHistory(id, productId, variantId, oldBuyPrice, newBuyPrice, oldSellPrice, newSellPrice, changedByUserId, changedByName, createdAt)
fun PriceHistory.toEntity(isSynced: Boolean = false) = PriceHistoryEntity(id, productId, variantId, oldBuyPrice, newBuyPrice, oldSellPrice, newSellPrice, changedByUserId, changedByName, createdAt, isSynced)

// Role
fun RoleEntity.toModel() = Role(id, name, storeId, isDefault, canAccessPos, canAccessProducts, canAccessStock, canAccessTransactions, canAccessReports, canAccessEmployees, canAccessCustomers, canAccessDelivery, canTakeDeliveryPhoto, canAccessSettings, canManageRoles)
fun Role.toEntity(isSynced: Boolean = false) = RoleEntity(id, name, storeId, isDefault, canAccessPos, canAccessProducts, canAccessStock, canAccessTransactions, canAccessReports, canAccessEmployees, canAccessCustomers, canAccessDelivery, canTakeDeliveryPhoto, canAccessSettings, canManageRoles, System.currentTimeMillis(), System.currentTimeMillis(), isSynced)
