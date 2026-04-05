package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetOrCreateTodaySkincareLogUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    suspend operator fun invoke(): SkinCareLog {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return repository.getLogByDate(today) ?: SkinCareLog(
            date = today,
            morningDone = false,
            nightDone = false
        )
    }
}