package com.svape.whowhat.presentation.screens.skincare

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.model.SkinCareStreak
import kotlinx.datetime.LocalDate

data class SkinCareUiState(
    val todayLog: SkinCareLog? = null,
    val selectedDate: LocalDate? = null,
    val selectedDateLog: SkinCareLog? = null,
    val allLogs: List<SkinCareLog> = emptyList(),
    val products: List<SkinCareProduct> = emptyList(),
    val streak: SkinCareStreak = SkinCareStreak(
        streakDays = 0,
        totalDaysLogged = 0,
        compliancePercent = 0f
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddProductDialog: Boolean = false,
    val showDatePicker: Boolean = false,
    val showEditLogDialog: Boolean = false
)