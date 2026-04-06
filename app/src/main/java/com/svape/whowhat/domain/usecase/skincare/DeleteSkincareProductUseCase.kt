package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.repository.SkinCareRepository
import javax.inject.Inject

class DeleteSkincareProductUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    suspend operator fun invoke(productId: Long) = repository.deleteProduct(productId)
}