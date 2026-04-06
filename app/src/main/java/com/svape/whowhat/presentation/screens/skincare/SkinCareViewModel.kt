package com.svape.whowhat.presentation.screens.skincare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.usecase.skincare.DeleteSkincareProductUseCase
import com.svape.whowhat.domain.usecase.skincare.GetAllSkincareProductsUseCase
import com.svape.whowhat.domain.usecase.skincare.GetOrCreateTodaySkincareLogUseCase
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
import javax.inject.Inject

@HiltViewModel
class SkinCareViewModel @Inject constructor(
    private val getOrCreateTodaySkincareLogUseCase: GetOrCreateTodaySkincareLogUseCase,
    private val logSkincareUseCase: LogSkincareUseCase,
    private val getAllSkincareProductsUseCase: GetAllSkincareProductsUseCase,
    private val getSkincareStreakUseCase: GetSkincareStreakUseCase,
    private val saveSkincareProductUseCase: SaveSkincareProductUseCase,
    private val updateProductStockUseCase: UpdateProductStockUseCase,
    private val deleteSkincareProductUseCase: DeleteSkincareProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkinCareUiState())
    val uiState: StateFlow<SkinCareUiState> = _uiState.asStateFlow()

    init {
        loadTodayLog()
        loadProductsAndStreak()
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { getOrCreateTodaySkincareLogUseCase() }
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

    private fun loadProductsAndStreak() {
        viewModelScope.launch {
            combine(
                getAllSkincareProductsUseCase(),
                getSkincareStreakUseCase()
            ) { products, streak ->
                _uiState.value.copy(
                    products = products,
                    streak = streak
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleMorning(done: Boolean) {
        val log = _uiState.value.todayLog ?: return
        viewModelScope.launch {
            val updated = log.copy(morningDone = done)
            logSkincareUseCase(updated)
            _uiState.value = _uiState.value.copy(todayLog = updated)
        }
    }

    fun toggleNight(done: Boolean) {
        val log = _uiState.value.todayLog ?: return
        viewModelScope.launch {
            val updated = log.copy(nightDone = done)
            logSkincareUseCase(updated)
            _uiState.value = _uiState.value.copy(todayLog = updated)
        }
    }

    fun saveProduct(name: String) {
        viewModelScope.launch {
            saveSkincareProductUseCase(
                SkinCareProduct(
                    name = name,
                    inStock = true,
                    needsToBuy = false
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            dismissAddProductDialog()
        }
    }

    fun updateStock(product: SkinCareProduct, inStock: Boolean) {
        viewModelScope.launch {
            updateProductStockUseCase(product, inStock)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            deleteSkincareProductUseCase(productId)
        }
    }

    fun showAddProductDialog() {
        _uiState.value = _uiState.value.copy(showAddProductDialog = true)
    }

    fun dismissAddProductDialog() {
        _uiState.value = _uiState.value.copy(showAddProductDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}