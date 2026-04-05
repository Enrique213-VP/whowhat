package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<WorkoutSession>> = repository.getAllSessions()
}