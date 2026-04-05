package com.svape.whowhat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_products")
data class SkinCareProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val inStock: Boolean,
    val needsToBuy: Boolean
)