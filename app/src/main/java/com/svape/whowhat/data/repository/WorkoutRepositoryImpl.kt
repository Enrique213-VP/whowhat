package com.svape.whowhat.data.repository

import com.svape.whowhat.data.local.dao.ExerciseDao
import com.svape.whowhat.data.local.dao.WorkoutSessionDao
import com.svape.whowhat.data.local.dao.WorkoutSetDao
import com.svape.whowhat.data.local.entity.ExerciseEntity
import com.svape.whowhat.data.local.entity.WorkoutSessionEntity
import com.svape.whowhat.data.local.entity.WorkoutSetEntity
import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val sessionDao: WorkoutSessionDao,
    private val setDao: WorkoutSetDao
) : WorkoutRepository {

    // region Exercise
    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insert(exercise.toEntity())

    override suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.update(exercise.toEntity())

    override suspend fun deleteExercise(exerciseId: Long) =
        exerciseDao.deleteById(exerciseId)
    // endregion

    // region Session
    override fun getAllSessions(): Flow<List<WorkoutSession>> =
        sessionDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertSession(session: WorkoutSession): Long =
        sessionDao.insert(session.toEntity())

    override suspend fun deleteSession(sessionId: Long) =
        sessionDao.deleteById(sessionId)
    // endregion

    // region Set
    override fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSet>> =
        setDao.getBySession(sessionId).map { list -> list.map { it.toDomain() } }

    override fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSet>> =
        setDao.getByExercise(exerciseId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertSet(set: WorkoutSet): Long =
        setDao.insert(set.toEntity())

    override suspend fun deleteSet(setId: Long) =
        setDao.deleteById(setId)
    // endregion

    // region Mappers
    private fun ExerciseEntity.toDomain() = Exercise(id, name, muscleGroup, notes)
    private fun Exercise.toEntity() = ExerciseEntity(id, name, muscleGroup, notes)

    private fun WorkoutSessionEntity.toDomain() = WorkoutSession(
        id = id,
        name = name,
        date = LocalDate.parse(date),
        notes = notes
    )
    private fun WorkoutSession.toEntity() = WorkoutSessionEntity(
        id = id,
        name = name,
        date = date.toString(),
        notes = notes
    )

    private fun WorkoutSetEntity.toDomain() = WorkoutSet(
        id = id,
        sessionId = sessionId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        setNumber = setNumber,
        reps = reps,
        weightLbs = weightLbs,
        date = LocalDate.parse(date)
    )
    private fun WorkoutSet.toEntity() = WorkoutSetEntity(
        id = id,
        sessionId = sessionId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        setNumber = setNumber,
        reps = reps,
        weightLbs = weightLbs,
        date = date.toString()
    )
    // endregion
}