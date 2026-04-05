package com.svape.whowhat.domain.model

import kotlinx.datetime.LocalDate

data class SupplementLog(
    val id: Long = 0,
    val date: LocalDate,
    val creatineTaken: Boolean,
    val proteinAvailable: Boolean,
    val proteinTaken: Boolean?,
    val notes: String = ""
)

data class SupplementStreak(
    val creatineStreakDays: Int,
    val totalDaysLogged: Int,
    val creatineCompliancePercent: Float
)