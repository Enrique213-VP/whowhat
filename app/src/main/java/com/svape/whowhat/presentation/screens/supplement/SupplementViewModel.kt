package com.svape.whowhat.presentation.screens.supplement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.usecase.supplement.GetAllSupplementLogsUseCase
import com.svape.whowhat.domain.usecase.supplement.GetOrCreateTodayLogUseCase
import com.svape.whowhat.domain.usecase.supplement.GetSupplementStreakUseCase
import com.svape.whowhat.domain.usecase.supplement.LogSupplementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplementViewModel @Inject constructor(
    private val getOrCreateTodayLogUseCase: GetOrCreateTodayLogUseCase,
    private val logSupplementUseCase: LogSupplementUseCase,
    private val getAllSupplementLogsUseCase: GetAllSupplementLogsUseCase,
    private val getSupplementStreakUseCase: GetSupplementStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupplementUiState())
    val uiState: StateFlow<SupplementUiState> = _uiState.asStateFlow()

    init {
        loadTodayLog()
        loadLogsAndStreak()
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { getOrCreateTodayLogUseCase() }
                .onSuccess { log ->
                    _uiState.value = _uiState.value.copy(
                        todayLog = log,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
        }
    }

    private fun loadLogsAndStreak() {
        viewModelScope.launch {
            combine(
                getAllSupplementLogsUseCase(),
                getSupplementStreakUseCase()
            ) { logs, streak ->
                _uiState.value.copy(
                    allLogs = logs,
                    streak = streak
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleCreatine(taken: Boolean) {
        val log = _uiState.value.todayLog ?: return
        viewModelScope.launch {
            val updated = log.copy(creatineTaken = taken)
            logSupplementUseCase(updated)
            _uiState.value = _uiState.value.copy(todayLog = updated)
        }
    }

    fun toggleProteinAvailable(available: Boolean) {
        val log = _uiState.value.todayLog ?: return
        viewModelScope.launch {
            val updated = log.copy(
                proteinAvailable = available,
                proteinTaken = if (!available) null else log.proteinTaken
            )
            logSupplementUseCase(updated)
            _uiState.value = _uiState.value.copy(todayLog = updated)
        }
    }

    fun toggleProtein(taken: Boolean) {
        val log = _uiState.value.todayLog ?: return
        if (!log.proteinAvailable) return
        viewModelScope.launch {
            val updated = log.copy(proteinTaken = taken)
            logSupplementUseCase(updated)
            _uiState.value = _uiState.value.copy(todayLog = updated)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}