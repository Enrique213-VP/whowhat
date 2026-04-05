package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.repository.SkinCareRepository
import javax.inject.Inject

class UpdateProductStockUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    suspend operator fun invoke(product: SkinCareProduct, inStock: Boolean) {
        repository.updateProduct(
            product.copy(
                inStock = inStock,
                needsToBuy = !inStock
            )
        )
    }
}