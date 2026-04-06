package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class UpdateWorkoutSetUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(set: WorkoutSet): Result<Unit> = runCatching {
        require(set.reps > 0) { "Las repeticiones deben ser mayor a 0" }
        require(set.weightLbs >= 0) { "El peso no puede ser negativo" }
        repository.updateSet(set)
    }
}