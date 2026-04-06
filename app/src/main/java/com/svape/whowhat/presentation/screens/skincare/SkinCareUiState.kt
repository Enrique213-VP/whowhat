package com.svape.whowhat.presentation.screens.skincare

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.model.SkinCareStreak

data class SkinCareUiState(
    val todayLog: SkinCareLog? = null,
    val products: List<SkinCareProduct> = emptyList(),
    val streak: SkinCareStreak = SkinCareStreak(
        streakDays = 0,
        totalDaysLogged = 0,
        compliancePercent = 0f
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddProductDialog: Boolean = false
)