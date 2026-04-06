package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveWorkoutSetUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(set: WorkoutSet): Result<Long> = runCatching {
        require(set.weightLbs >= 0) { "El peso no puede ser negativo" }
        repository.insertSet(set)
    }
}