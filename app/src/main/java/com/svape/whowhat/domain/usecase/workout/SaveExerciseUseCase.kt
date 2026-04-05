package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(exercise: Exercise): Result<Long> = runCatching {
        require(exercise.name.isNotBlank()) { "El nombre no puede estar vacío" }
        require(exercise.muscleGroup.isNotBlank()) { "El grupo muscular no puede estar vacío" }
        if (exercise.id == 0L) repository.insertExercise(exercise)
        else { repository.updateExercise(exercise); exercise.id }
    }
}