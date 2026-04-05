package com.svape.whowhat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.svape.whowhat.data.local.dao.ExerciseDao
import com.svape.whowhat.data.local.dao.SkinCareLogDao
import com.svape.whowhat.data.local.dao.SkinCareProductDao
import com.svape.whowhat.data.local.dao.SupplementLogDao
import com.svape.whowhat.data.local.dao.WeightEntryDao
import com.svape.whowhat.data.local.dao.WorkoutSessionDao
import com.svape.whowhat.data.local.dao.WorkoutSetDao
import com.svape.whowhat.data.local.entity.ExerciseEntity
import com.svape.whowhat.data.local.entity.SkinCareLogEntity
import com.svape.whowhat.data.local.entity.SkinCareProductEntity
import com.svape.whowhat.data.local.entity.SupplementLogEntity
import com.svape.whowhat.data.local.entity.WeightEntryEntity
import com.svape.whowhat.data.local.entity.WorkoutSessionEntity
import com.svape.whowhat.data.local.entity.WorkoutSetEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        WeightEntryEntity::class,
        SupplementLogEntity::class,
        SkinCareLogEntity::class,
        SkinCareProductEntity::class
    ],
    version = 1,
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
}