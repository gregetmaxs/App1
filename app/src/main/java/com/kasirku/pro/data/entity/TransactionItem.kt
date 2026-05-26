package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transaction_items")
data class TransactionItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val transactionId: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val subtotal: Double = 0.0,
    val isSynced: Boolean = false
)
