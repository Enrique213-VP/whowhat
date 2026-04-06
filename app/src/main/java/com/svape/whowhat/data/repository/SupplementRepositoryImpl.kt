package com.svape.whowhat.data.repository

import com.svape.whowhat.data.local.dao.SupplementLogDao
import com.svape.whowhat.data.local.entity.SupplementLogEntity
import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class SupplementRepositoryImpl @Inject constructor(
    private val dao: SupplementLogDao
) : SupplementRepository {

    override fun getAllLogs(): Flow<List<SupplementLog>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getLogByDate(date: LocalDate): SupplementLog? =
        dao.getByDate(date.toString())?.toDomain()

    override suspend fun insertOrUpdateLog(log: SupplementLog) {
        dao.insert(log.toEntity())
    }

    private fun SupplementLogEntity.toDomain() = SupplementLog(
        id = id,
        date = LocalDate.parse(date),
        creatineTaken = creatineTaken,
        proteinAvailable = proteinAvailable,
        proteinTaken = proteinTaken,
        notes = notes
    )

    private fun SupplementLog.toEntity() = SupplementLogEntity(
        id = id,
        date = date.toString(),
        creatineTaken = creatineTaken,
        proteinAvailable = proteinAvailable,
        proteinTaken = proteinTaken,
        notes = notes
    )
}