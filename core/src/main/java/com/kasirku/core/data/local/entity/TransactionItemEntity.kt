package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_items")
data class TransactionItemEntity(
    @PrimaryKey val id: String = "",
    val transactionId: String = "",
    val productId: String = "",
    val variantId: String = "",
    val productName: String = "",
    val variantName: String = "",
    val quantity: Int = 0,
    val price: Long = 0L,
    val subtotal: Long = 0L,
    val isSynced: Boolean = false
)
