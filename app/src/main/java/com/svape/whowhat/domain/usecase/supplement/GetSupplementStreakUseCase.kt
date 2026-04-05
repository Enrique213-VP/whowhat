package com.svape.whowhat.domain.usecase.supplement

import com.svape.whowhat.domain.model.SupplementStreak
import com.svape.whowhat.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSupplementStreakUseCase @Inject constructor(
    private val repository: SupplementRepository
) {
    operator fun invoke(): Flow<SupplementStreak> =
        repository.getAllLogs().map { logs ->
            val sorted = logs.sortedByDescending { it.date }
            var streak = 0
            for (log in sorted) {
                if (log.creatineTaken) streak++ else break
            }
            val compliance = if (logs.isEmpty()) 0f
            else logs.count { it.creatineTaken }.toFloat() / logs.size * 100f
            SupplementStreak(
                creatineStreakDays = streak,
                totalDaysLogged = logs.size,
                creatineCompliancePercent = compliance
            )
        }
}