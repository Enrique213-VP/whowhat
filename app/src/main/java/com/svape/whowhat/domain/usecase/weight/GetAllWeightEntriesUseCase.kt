package com.svape.whowhat.domain.usecase.weight

import com.svape.whowhat.domain.model.WeightEntry
import com.svape.whowhat.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWeightEntriesUseCase @Inject constructor(
    private val repository: WeightRepository
) {
    operator fun invoke(): Flow<List<WeightEntry>> = repository.getAllEntries()
}