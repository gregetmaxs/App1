package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "price_history")
data class PriceHistory(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val variantId: String = "",
    val oldPrice: Double = 0.0,
    val newPrice: Double = 0.0,
    val oldCostPrice: Double = 0.0,
    val newCostPrice: Double = 0.0,
    val changedBy: String = "",
    val changedByName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
