package com.svape.whowhat.presentation.screens.weight

import com.svape.whowhat.domain.model.WeightEntry
import kotlinx.datetime.LocalDate

data class WeightUiState(
    val entries: List<WeightEntry> = emptyList(),
    val weeklyGroups: List<WeeklyWeightGroup> = emptyList(),
    val latestWeight: Float? = null,
    val initialWeight: Float? = null,
    val totalLost: Float = 0f,
    val targetWeight: Float = 87f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddEntryDialog: Boolean = false,
    val showEditTargetDialog: Boolean = false
)

data class WeeklyWeightGroup(
    val weekLabel: String,
    val startDate: LocalDate,
    val entries: List<WeightEntry>,
    val avgWeight: Float,
    val change: Float?
)