package com.svape.whowhat.domain.usecase.workout

import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSetsBySessionUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<WorkoutSet>> =
        repository.getSetsBySession(sessionId)
}