package com.kasirku.pro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val price: Double = 0.0,
    val costPrice: Double = 0.0,
    val stock: Int = 0,
    val categoryId: String = "",
    val imagePath: String = "",
    val supplierName: String = "",
    val barcode: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
