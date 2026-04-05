package com.svape.whowhat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supplement_logs")
data class SupplementLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val creatineTaken: Boolean,
    val proteinAvailable: Boolean,
    val proteinTaken: Boolean?,
    val notes: String = ""
)