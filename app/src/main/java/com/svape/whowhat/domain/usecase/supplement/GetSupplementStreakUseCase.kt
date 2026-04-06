package com.svape.whowhat.domain.usecase.supplement

import com.svape.whowhat.domain.model.SupplementStreak
import com.svape.whowhat.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import javax.inject.Inject

class GetSupplementStreakUseCase @Inject constructor(
    private val repository: SupplementRepository
) {
    operator fun invoke(): Flow<SupplementStreak> =
        repository.getAllLogs().map { logs ->

            val creatineTaken = logs.filter { it.creatineTaken }
            val creatineSorted = creatineTaken.sortedByDescending { it.date }
            var creatineStreak = 0
            var prevCreatine = creatineSorted.firstOrNull()?.date
            for (log in creatineSorted) {
                if (prevCreatine == null) break
                if (log.date == prevCreatine) {
                    creatineStreak++
                    prevCreatine = prevCreatine.minus(1, DateTimeUnit.DAY)
                } else break
            }
            val daysWithActivity = logs.filter { it.creatineTaken || it.proteinAvailable }
            val creatineCompliance = if (daysWithActivity.isEmpty()) 0f
            else creatineTaken.size.toFloat() / daysWithActivity.size * 100f

            val proteinAvailableLogs = logs.filter { it.proteinAvailable }
            val proteinTakenLogs = logs.filter { it.proteinTaken == true }
            val proteinSorted = proteinTakenLogs.sortedByDescending { it.date }
            var proteinStreak = 0
            var prevProtein = proteinSorted.firstOrNull()?.date
            for (log in proteinSorted) {
                if (prevProtein == null) break
                if (log.date == prevProtein) {
                    proteinStreak++
                    prevProtein = prevProtein.minus(1, DateTimeUnit.DAY)
                } else break
            }
            val proteinCompliance = if (proteinAvailableLogs.isEmpty()) 0f
            else proteinTakenLogs.size.toFloat() / proteinAvailableLogs.size * 100f

            SupplementStreak(
                creatineStreakDays = creatineStreak,
                totalDaysLogged = creatineTaken.size,
                creatineCompliancePercent = creatineCompliance,
                proteinStreakDays = proteinStreak,
                proteinDaysTaken = proteinTakenLogs.size,
                proteinDaysAvailable = proteinAvailableLogs.size,
                proteinCompliancePercent = proteinCompliance
            )
        }
}