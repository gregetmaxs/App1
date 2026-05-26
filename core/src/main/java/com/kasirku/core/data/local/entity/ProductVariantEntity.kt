package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants")
data class ProductVariantEntity(
    @PrimaryKey val id: String = "",
    val productId: String = "",
    val name: String = "",
    val barcode: String = "",
    val buyPrice: Long = 0L,
    val sellPrice: Long = 0L,
    val stock: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
