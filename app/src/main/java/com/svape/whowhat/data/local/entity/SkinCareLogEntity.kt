package com.svape.whowhat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_logs")
data class SkinCareLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val morningDone: Boolean,
    val nightDone: Boolean
)