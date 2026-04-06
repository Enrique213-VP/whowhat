package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareStreak
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import javax.inject.Inject

class GetSkincareStreakUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    operator fun invoke(): Flow<SkinCareStreak> =
        repository.getAllLogs().map { logs ->
            val completed = logs.filter { it.morningDone || it.nightDone }
            val sorted = completed.sortedByDescending { it.date }
            var streak = 0
            var prev = sorted.firstOrNull()?.date
            for (log in sorted) {
                if (prev == null) break
                if (log.date == prev) {
                    streak++
                    prev = prev.minus(1, DateTimeUnit.DAY)
                } else break
            }
            val compliance = if (logs.isEmpty()) 0f
            else completed.size.toFloat() / logs.size * 100f
            SkinCareStreak(
                streakDays = streak,
                totalDaysLogged = completed.size,
                compliancePercent = compliance
            )
        }
}