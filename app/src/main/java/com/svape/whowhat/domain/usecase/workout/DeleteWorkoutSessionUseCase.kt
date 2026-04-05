package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteWorkoutSessionUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(sessionId: Long) = repository.deleteSession(sessionId)
}