package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "item_variants")
data class ItemVariant(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val costPrice: Double = 0.0,
    val stock: Int = 0,
    val barcode: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
