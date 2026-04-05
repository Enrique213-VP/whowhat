package com.svape.whowhat.domain.repository

import com.svape.whowhat.domain.model.SupplementLog
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SupplementRepository {
    fun getAllLogs(): Flow<List<SupplementLog>>
    suspend fun getLogByDate(date: LocalDate): SupplementLog?
    suspend fun insertOrUpdateLog(log: SupplementLog)
    suspend fun deleteLog(logId: Long)
}