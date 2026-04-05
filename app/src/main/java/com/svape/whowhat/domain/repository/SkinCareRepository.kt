package com.svape.whowhat.domain.repository

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SkinCareRepository {
    fun getAllLogs(): Flow<List<SkinCareLog>>
    suspend fun getLogByDate(date: LocalDate): SkinCareLog?
    suspend fun insertOrUpdateLog(log: SkinCareLog)

    fun getAllProducts(): Flow<List<SkinCareProduct>>
    suspend fun insertProduct(product: SkinCareProduct): Long
    suspend fun updateProduct(product: SkinCareProduct)
    suspend fun deleteProduct(productId: Long)
}