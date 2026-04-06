package com.svape.whowhat.di

import android.content.Context
import androidx.room.Room
import com.svape.whowhat.data.local.AppDatabase
import com.svape.whowhat.data.local.dao.ExerciseDao
import com.svape.whowhat.data.local.dao.SkinCareLogDao
import com.svape.whowhat.data.local.dao.SkinCareProductDao
import com.svape.whowhat.data.local.dao.SupplementLogDao
import com.svape.whowhat.data.local.dao.WeightEntryDao
import com.svape.whowhat.data.local.dao.WorkoutSessionDao
import com.svape.whowhat.data.local.dao.WorkoutSetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "whowhat_db"
        ).build()

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideWorkoutSessionDao(db: AppDatabase): WorkoutSessionDao = db.workoutSessionDao()

    @Provides
    fun provideWorkoutSetDao(db: AppDatabase): WorkoutSetDao = db.workoutSetDao()

    @Provides
    fun provideWeightEntryDao(db: AppDatabase): WeightEntryDao = db.weightEntryDao()

    @Provides
    fun provideSupplementLogDao(db: AppDatabase): SupplementLogDao = db.supplementLogDao()

    @Provides
    fun provideSkinCareLogDao(db: AppDatabase): SkinCareLogDao = db.skinCareLogDao()

    @Provides
    fun provideSkinCareProductDao(db: AppDatabase): SkinCareProductDao = db.skinCareProductDao()
}