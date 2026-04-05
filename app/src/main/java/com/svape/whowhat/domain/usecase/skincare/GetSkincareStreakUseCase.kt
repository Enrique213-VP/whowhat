package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareStreak
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSkincareStreakUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    operator fun invoke(): Flow<SkinCareStreak> =
        repository.getAllLogs().map { logs ->
            val sorted = logs.sortedByDescending { it.date }
            var streak = 0
            for (log in sorted) {
                if (log.morningDone || log.nightDone) streak++ else break
            }
            val compliance = if (logs.isEmpty()) 0f
            else logs.count { it.morningDone || it.nightDone }.toFloat() / logs.size * 100f
            SkinCareStreak(
                streakDays = streak,
                totalDaysLogged = logs.size,
                compliancePercent = compliance
            )
        }
}