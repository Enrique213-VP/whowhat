package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSkincareProductsUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    operator fun invoke(): Flow<List<SkinCareProduct>> = repository.getAllProducts()
}