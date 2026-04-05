package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.repository.WorkoutRepository
import javax.inject.Inject

class SaveWorkoutSessionUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(session: WorkoutSession): Result<Long> = runCatching {
        require(session.name.isNotBlank()) { "El nombre de la sesión no puede estar vacío" }
        repository.insertSession(session)
    }
}