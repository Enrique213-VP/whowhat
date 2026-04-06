package com.svape.whowhat.domain.repository

import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun insertExercise(exercise: Exercise): Long
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exerciseId: Long)

    fun getAllSessions(): Flow<List<WorkoutSession>>
    suspend fun insertSession(session: WorkoutSession): Long
    suspend fun deleteSession(sessionId: Long)

    fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSet>>
    fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSet>>
    suspend fun insertSet(set: WorkoutSet): Long
    suspend fun updateSet(set: WorkoutSet)
    suspend fun deleteSet(setId: Long)
}