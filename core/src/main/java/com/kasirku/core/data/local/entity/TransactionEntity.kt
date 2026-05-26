package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = "",
    val transactionNumber: String = "",
    val cashierUserId: String = "",
    val cashierName: String = "",
    val customerName: String = "",
    val subtotal: Long = 0L,
    val ppnPercent: Double = 0.11,
    val ppnAmount: Long = 0L,
    val discount: Long = 0L,
    val total: Long = 0L,
    val paymentMethod: String = "CASH",  // CASH, TRANSFER, QRIS
    val amountPaid: Long = 0L,
    val changeAmount: Long = 0L,
    val deliveryType: String = "SELF_PICKUP", // SELF_PICKUP, STORE_DELIVERY
    val deliveryAddress: String = "",
    val driverUserId: String = "",
    val helperUserId: String = "",
    val deliveryStatus: String = "",  // PENDING, IN_TRANSIT, DELIVERED
    val deliveryProofUrl: String = "",
    val resiNumber: String = "",
    val notes: String = "",
    val storeId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
