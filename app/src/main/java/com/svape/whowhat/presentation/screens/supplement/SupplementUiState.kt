package com.svape.whowhat.presentation.screens.supplement

import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.model.SupplementStreak

data class SupplementUiState(
    val todayLog: SupplementLog? = null,
    val allLogs: List<SupplementLog> = emptyList(),
    val streak: SupplementStreak = SupplementStreak(
        creatineStreakDays = 0,
        totalDaysLogged = 0,
        creatineCompliancePercent = 0f
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)