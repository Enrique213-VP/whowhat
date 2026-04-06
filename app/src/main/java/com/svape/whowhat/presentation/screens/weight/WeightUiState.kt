package com.svape.whowhat.presentation.screens.weight

import com.svape.whowhat.domain.model.WeightEntry

data class WeightUiState(
    val entries: List<WeightEntry> = emptyList(),
    val latestWeight: Float? = null,
    val initialWeight: Float? = null,
    val totalLost: Float = 0f,
    val targetWeight: Float = 87f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddEntryDialog: Boolean = false
)