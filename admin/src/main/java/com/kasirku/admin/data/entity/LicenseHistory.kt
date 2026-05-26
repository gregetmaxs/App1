package com.kasirku.admin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "license_history")
data class LicenseHistory(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val licenseId: String = "",
    val action: String = "",
    val details: String = "",
    val performedBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
