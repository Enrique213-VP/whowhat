package com.svape.whowhat.domain.model

data class SupplementStreak(
    val creatineStreakDays: Int,
    val totalDaysLogged: Int,
    val creatineCompliancePercent: Float,
    val proteinStreakDays: Int = 0,
    val proteinDaysTaken: Int = 0,
    val proteinDaysAvailable: Int = 0,
    val proteinCompliancePercent: Float = 0f
)