package com.svape.whowhat.presentation.screens.skincare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.usecase.skincare.DeleteSkincareProductUseCase
import com.svape.whowhat.domain.usecase.skincare.GetAllSkincareLogsUseCase
import com.svape.whowhat.domain.usecase.skincare.GetAllSkincareProductsUseCase
import com.svape.whowhat.domain.usecase.skincare.GetSkincareStreakUseCase
import com.svape.whowhat.domain.usecase.skincare.LogSkincareUseCase
import com.svape.whowhat.domain.usecase.skincare.SaveSkincareProductUseCase
import com.svape.whowhat.domain.usecase.skincare.UpdateProductStockUseCase
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
class SkinCareViewModel @Inject constructor(
    private val logSkincareUseCase: LogSkincareUseCase,
    private val getAllSkincareProductsUseCase: GetAllSkincareProductsUseCase,
    private val getAllSkincareLogsUseCase: GetAllSkincareLogsUseCase,
    private val getSkincareStreakUseCase: GetSkincareStreakUseCase,
    private val saveSkincareProductUseCase: SaveSkincareProductUseCase,
    private val updateProductStockUseCase: UpdateProductStockUseCase,
    private val deleteSkincareProductUseCase: DeleteSkincareProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkinCareUiState())
    val uiState: StateFlow<SkinCareUiState> = _uiState.asStateFlow()

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        _uiState.value = _uiState.value.copy(selectedDate = today)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                getAllSkincareProductsUseCase(),
                getSkincareStreakUseCase(),
                getAllSkincareLogsUseCase()
            ) { products, streak, logs ->
                val selected = _uiState.value.selectedDate
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                _uiState.value.copy(
                    products = products,
                    streak = streak,
                    allLogs = logs,
                    todayLog = logs.find { it.date == today },
                    selectedDateLog = logs.find { it.date == selected },
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
        val newLog = SkinCareLog(
            date = date,
            morningDone = false,
            nightDone = false
        )
        viewModelScope.launch {
            logSkincareUseCase(newLog)
            _uiState.value = _uiState.value.copy(selectedDateLog = newLog)
        }
    }

    fun toggleMorning() {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(log.copy(morningDone = !log.morningDone))
    }

    fun toggleNight() {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(log.copy(nightDone = !log.nightDone))
    }

    fun saveEditedLog(morningDone: Boolean, nightDone: Boolean) {
        val log = _uiState.value.selectedDateLog ?: return
        saveLog(log.copy(morningDone = morningDone, nightDone = nightDone))
        _uiState.value = _uiState.value.copy(showEditLogDialog = false)
    }

    private fun saveLog(updated: SkinCareLog) {
        viewModelScope.launch {
            logSkincareUseCase(updated)
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            _uiState.value = _uiState.value.copy(
                selectedDateLog = updated,
                todayLog = if (updated.date == today) updated else _uiState.value.todayLog
            )
        }
    }

    fun saveProduct(name: String) {
        viewModelScope.launch {
            saveSkincareProductUseCase(
                SkinCareProduct(name = name, inStock = true, needsToBuy = false)
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            _uiState.value = _uiState.value.copy(showAddProductDialog = false)
        }
    }

    fun updateStock(product: SkinCareProduct, inStock: Boolean) {
        viewModelScope.launch { updateProductStockUseCase(product, inStock) }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch { deleteSkincareProductUseCase(productId) }
    }

    fun showAddProductDialog() {
        _uiState.value = _uiState.value.copy(showAddProductDialog = true)
    }

    fun dismissAddProductDialog() {
        _uiState.value = _uiState.value.copy(showAddProductDialog = false)
    }

    fun showDatePicker() { _uiState.value = _uiState.value.copy(showDatePicker = true) }
    fun dismissDatePicker() { _uiState.value = _uiState.value.copy(showDatePicker = false) }
    fun showEditLogDialog() { _uiState.value = _uiState.value.copy(showEditLogDialog = true) }
    fun dismissEditLogDialog() { _uiState.value = _uiState.value.copy(showEditLogDialog = false) }

    fun datesWithLogs(): Set<LocalDate> = _uiState.value.allLogs
        .filter { it.morningDone || it.nightDone }
        .map { it.date }.toSet()

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}