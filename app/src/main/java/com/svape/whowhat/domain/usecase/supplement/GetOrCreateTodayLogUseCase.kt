package com.svape.whowhat.domain.usecase.supplement

import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.repository.SupplementRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetOrCreateTodayLogUseCase @Inject constructor(
    private val repository: SupplementRepository
) {
    suspend operator fun invoke(): SupplementLog {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return repository.getLogByDate(today) ?: SupplementLog(
            date = today,
            creatineTaken = false,
            proteinAvailable = false,
            proteinTaken = null
        )
    }
}