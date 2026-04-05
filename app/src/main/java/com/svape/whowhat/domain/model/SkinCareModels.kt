package com.svape.whowhat.domain.model

import kotlinx.datetime.LocalDate

data class SkinCareLog(
    val id: Long = 0,
    val date: LocalDate,
    val morningDone: Boolean,
    val nightDone: Boolean
)

data class SkinCareProduct(
    val id: Long = 0,
    val name: String,
    val inStock: Boolean,
    val needsToBuy: Boolean
)

data class SkinCareStreak(
    val streakDays: Int,
    val totalDaysLogged: Int,
    val compliancePercent: Float
)