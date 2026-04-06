package com.svape.whowhat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.svape.whowhat.data.local.dao.ExerciseDao
import com.svape.whowhat.data.local.dao.SkinCareLogDao
import com.svape.whowhat.data.local.dao.SkinCareProductDao
import com.svape.whowhat.data.local.dao.SupplementLogDao
import com.svape.whowhat.data.local.dao.WeightEntryDao
import com.svape.whowhat.data.local.dao.WorkoutSessionDao
import com.svape.whowhat.data.local.dao.WorkoutSetDao
import com.svape.whowhat.data.local.dao.WorkoutTemplateDao
import com.svape.whowhat.data.local.entity.ExerciseEntity
import com.svape.whowhat.data.local.entity.SkinCareLogEntity
import com.svape.whowhat.data.local.entity.SkinCareProductEntity
import com.svape.whowhat.data.local.entity.SupplementLogEntity
import com.svape.whowhat.data.local.entity.WeightEntryEntity
import com.svape.whowhat.data.local.entity.WorkoutSessionEntity
import com.svape.whowhat.data.local.entity.WorkoutSetEntity
import com.svape.whowhat.data.local.entity.WorkoutTemplateEntity
import com.svape.whowhat.data.local.entity.WorkoutTemplateExerciseEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        WeightEntryEntity::class,
        SupplementLogEntity::class,
        SkinCareLogEntity::class,
        SkinCareProductEntity::class,
        WorkoutTemplateEntity::class,
        WorkoutTemplateExerciseEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun supplementLogDao(): SupplementLogDao
    abstract fun skinCareLogDao(): SkinCareLogDao
    abstract fun skinCareProductDao(): SkinCareProductDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS workout_templates (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        muscleGroups TEXT NOT NULL,
                        notes TEXT NOT NULL DEFAULT ''
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS workout_template_exercises (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        templateId INTEGER NOT NULL,
                        exerciseId INTEGER NOT NULL,
                        exerciseName TEXT NOT NULL,
                        muscleGroup TEXT NOT NULL,
                        defaultSets INTEGER NOT NULL DEFAULT 3,
                        defaultReps INTEGER NOT NULL DEFAULT 10,
                        orderIndex INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (templateId) REFERENCES workout_templates(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_template_exercises_templateId ON workout_template_exercises(templateId)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS exercises_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                muscleGroup TEXT NOT NULL
            )
        """)
                db.execSQL("INSERT INTO exercises_new (id, name, muscleGroup) SELECT id, name, muscleGroup FROM exercises")
                db.execSQL("DROP TABLE exercises")
                db.execSQL("ALTER TABLE exercises_new RENAME TO exercises")
            }
        }
    }
}