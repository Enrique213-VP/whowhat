package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllExercisesUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Exercise>> = repository.getAllExercises()
}