package com.svape.whowhat.presentation.screens.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.WeightEntry
import com.svape.whowhat.domain.usecase.weight.DeleteWeightEntryUseCase
import com.svape.whowhat.domain.usecase.weight.GetAllWeightEntriesUseCase
import com.svape.whowhat.domain.usecase.weight.SaveWeightEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val getAllWeightEntriesUseCase: GetAllWeightEntriesUseCase,
    private val saveWeightEntryUseCase: SaveWeightEntryUseCase,
    private val deleteWeightEntryUseCase: DeleteWeightEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightUiState())
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getAllWeightEntriesUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                .collect { entries ->
                    val sorted = entries.sortedBy { it.date }
                    _uiState.value = _uiState.value.copy(
                        entries = sorted,
                        latestWeight = sorted.lastOrNull()?.weightKg,
                        initialWeight = sorted.firstOrNull()?.weightKg,
                        totalLost = (sorted.firstOrNull()?.weightKg ?: 0f) -
                                (sorted.lastOrNull()?.weightKg ?: 0f),
                        isLoading = false
                    )
                }
        }
    }

    fun saveEntry(weightKg: Float, notes: String = "") {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            saveWeightEntryUseCase(
                WeightEntry(
                    weightKg = weightKg,
                    date = today,
                    notes = notes
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            dismissAddEntryDialog()
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            deleteWeightEntryUseCase(entryId)
        }
    }

    fun showAddEntryDialog() {
        _uiState.value = _uiState.value.copy(showAddEntryDialog = true)
    }

    fun dismissAddEntryDialog() {
        _uiState.value = _uiState.value.copy(showAddEntryDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}