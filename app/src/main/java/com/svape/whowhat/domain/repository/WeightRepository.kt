package com.svape.whowhat.domain.repository

import com.svape.whowhat.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun getAllEntries(): Flow<List<WeightEntry>>
    suspend fun insertEntry(entry: WeightEntry): Long
    suspend fun updateEntry(entry: WeightEntry)
    suspend fun deleteEntry(entryId: Long)
}