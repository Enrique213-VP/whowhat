package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(exerciseId: Long) = repository.deleteExercise(exerciseId)
}