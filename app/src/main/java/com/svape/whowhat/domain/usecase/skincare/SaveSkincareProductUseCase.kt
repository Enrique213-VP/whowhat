package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.repository.SkinCareRepository
import javax.inject.Inject

class SaveSkincareProductUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    suspend operator fun invoke(product: SkinCareProduct): Result<Long> = runCatching {
        require(product.name.isNotBlank()) { "El nombre del producto no puede estar vacío" }
        if (product.id == 0L) repository.insertProduct(product)
        else { repository.updateProduct(product); product.id }
    }
}