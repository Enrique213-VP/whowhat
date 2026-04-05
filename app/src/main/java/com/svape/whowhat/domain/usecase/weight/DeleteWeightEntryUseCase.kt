package com.svape.whowhat.domain.usecase.weight

import com.svape.whowhat.domain.repository.WeightRepository
import javax.inject.Inject

class DeleteWeightEntryUseCase @Inject constructor(
    private val repository: WeightRepository
) {
    suspend operator fun invoke(entryId: Long) = repository.deleteEntry(entryId)
}