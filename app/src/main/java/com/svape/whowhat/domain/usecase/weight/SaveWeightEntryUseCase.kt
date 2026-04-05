package com.svape.whowhat.domain.usecase.weight

import com.svape.whowhat.domain.model.WeightEntry
import com.svape.whowhat.domain.repository.WeightRepository
import javax.inject.Inject

class SaveWeightEntryUseCase @Inject constructor(
    private val repository: WeightRepository
) {
    suspend operator fun invoke(entry: WeightEntry): Result<Long> = runCatching {
        require(entry.weightKg > 0) { "El peso debe ser mayor a 0" }
        require(entry.weightKg < 300) { "El peso ingresado no es válido" }
        if (entry.id == 0L) repository.insertEntry(entry)
        else { repository.updateEntry(entry); entry.id }
    }
}