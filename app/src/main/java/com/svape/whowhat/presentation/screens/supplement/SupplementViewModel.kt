package com.svape.whowhat.presentation.screens.supplement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.usecase.supplement.GetAllSupplementLogsUseCase
import com.svape.whowhat.domain.usecase.supplement.GetSupplementStreakUseCase
import com.svape.whowhat.domain.usecase.supplement.LogSupplementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class SupplementViewModel @Inject constructor(
    private val logSupplementUseCase: LogSupplementUseCase,
    private val getAllSupplementLogsUseCase: GetAllSupplementLogsUseCase,
    private val getSupplementStreakUseCase: GetSupplementStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupplementUiState())
    val uiState: StateFlow<SupplementUiState> = _uiState.asStateFlow()

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        _uiState.value = _uiState.value.copy(selectedDate = today)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                getAllSupplementLogsUseCase(),
                getSupplementStreakUseCase()
            ) { logs, streak ->
                val selected = _uiState.value.selectedDate
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val logForSelected = logs.find { it.date == selected }
                _uiState.value.copy(
                    allLogs = logs,
                    streak = streak,
                    todayLog = logs.find { it.date == today },
                    selectedDateLog = logForSelected,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val logForDate = _uiState.value.allLogs.find { it.date == date }
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedDateLog = logForDate
        )
    }

    fun createLogForDate() {
        val date = _uiState.value.selectedDate ?: return
        val newLog = SupplementLog(
            date = date,
            creatineTaken = false,
            proteinTaken = null,
            proteinAvailable = false
        )
        viewModelScope.launch {
            logSupplementUseCase(newLog)
            _uiState.value = _uiState.value.copy(selectedDateLog = newLog)
        }
    }

    fun toggleCreatine() {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(log.copy(creatineTaken = !log.creatineTaken))
    }

    fun toggleProteinAvailable() {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(
            log.copy(
                proteinAvailable = !log.proteinAvailable,
                proteinTaken = if (log.proteinAvailable) null else log.proteinTaken
            )
        )
    }

    fun toggleProtein() {
        val log = _uiState.value.selectedDateLog ?: return
        if (!log.proteinAvailable) return
        saveLog(log.copy(proteinTaken = !(log.proteinTaken ?: false)))
    }

    fun saveEditedLog(creatine: Boolean, proteinAvailable: Boolean, proteinTaken: Boolean?) {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(
            log.copy(
                creatineTaken = creatine,
                proteinAvailable = proteinAvailable,
                proteinTaken = if (proteinAvailable) proteinTaken else null
            )
        )
        _uiState.value = _uiState.value.copy(showEditLogDialog = false)
    }

    private fun saveLog(updated: SupplementLog) {
        viewModelScope.launch {
            logSupplementUseCase(updated)
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            _uiState.value = _uiState.value.copy(
                selectedDateLog = updated,
                todayLog = if (updated.date == today) updated else _uiState.value.todayLog
            )
        }
    }

    fun showDatePicker() { _uiState.value = _uiState.value.copy(showDatePicker = true) }
    fun dismissDatePicker() { _uiState.value = _uiState.value.copy(showDatePicker = false) }
    fun showEditLogDialog() { _uiState.value = _uiState.value.copy(showEditLogDialog = true) }
    fun dismissEditLogDialog() { _uiState.value = _uiState.value.copy(showEditLogDialog = false) }

    fun datesWithLogs(): Set<LocalDate> = _uiState.value.allLogs
        .filter { it.creatineTaken }
        .map { it.date }.toSet()

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}