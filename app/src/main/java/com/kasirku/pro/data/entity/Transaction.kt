package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class TransactionType {
    SALE,
    PURCHASE
}

enum class DeliveryType {
    PICKUP,
    DELIVERY
}

enum class DeliveryStatus {
    WAITING,
    IN_PROGRESS,
    DELIVERED
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType = TransactionType.SALE,
    val customerName: String = "",
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val changeAmount: Double = 0.0,
    val userId: String = "",
    val userName: String = "",
    val notes: String = "",
    val resiNumber: String = "",
    val deliveryType: DeliveryType = DeliveryType.PICKUP,
    val deliveryAddress: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val helperId: String = "",
    val helperName: String = "",
    val deliveryStatus: DeliveryStatus = DeliveryStatus.WAITING,
    val deliveryProofImage: String = "",
    val deliveredAt: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
