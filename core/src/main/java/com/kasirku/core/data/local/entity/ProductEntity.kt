package com.kasirku.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val description: String = "",
    val barcode: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val supplierId: String = "",
    val buyPrice: Long = 0L,
    val sellPrice: Long = 0L,
    val stock: Int = 0,
    val minStock: Int = 0,
    val unit: String = "pcs",
    val hasVariants: Boolean = false,
    val storeId: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
