package com.svape.whowhat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.svape.whowhat.data.local.entity.WorkoutTemplateEntity
import com.svape.whowhat.data.local.entity.WorkoutTemplateExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {
    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getExercisesForTemplate(templateId: Long): List<WorkoutTemplateExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(exercises: List<WorkoutTemplateExerciseEntity>)

    @Query("DELETE FROM workout_templates WHERE id = :id")
    suspend fun deleteTemplate(id: Long)

    @Query("DELETE FROM workout_template_exercises WHERE templateId = :templateId")
    suspend fun deleteExercisesForTemplate(templateId: Long)

    @Transaction
    suspend fun replaceTemplateExercises(
        templateId: Long,
        exercises: List<WorkoutTemplateExerciseEntity>
    ) {
        deleteExercisesForTemplate(templateId)
        insertTemplateExercises(exercises)
    }
}