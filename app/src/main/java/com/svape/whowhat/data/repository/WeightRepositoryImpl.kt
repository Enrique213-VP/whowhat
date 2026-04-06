package com.svape.whowhat.data.repository

import com.svape.whowhat.data.local.dao.WeightEntryDao
import com.svape.whowhat.data.local.entity.WeightEntryEntity
import com.svape.whowhat.domain.model.WeightEntry
import com.svape.whowhat.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class WeightRepositoryImpl @Inject constructor(
    private val dao: WeightEntryDao
) : WeightRepository {

    override fun getAllEntries(): Flow<List<WeightEntry>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertEntry(entry: WeightEntry): Long =
        dao.insert(entry.toEntity())

    override suspend fun updateEntry(entry: WeightEntry) =
        dao.update(entry.toEntity())

    override suspend fun deleteEntry(entryId: Long) =
        dao.deleteById(entryId)

    private fun WeightEntryEntity.toDomain() = WeightEntry(
        id = id,
        weightKg = weightKg,
        date = LocalDate.parse(date),
        notes = notes
    )

    private fun WeightEntry.toEntity() = WeightEntryEntity(
        id = id,
        weightKg = weightKg,
        date = date.toString(),
        notes = notes
    )
}