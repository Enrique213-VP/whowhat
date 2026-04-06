package com.svape.whowhat.di

import com.svape.whowhat.data.repository.SkinCareRepositoryImpl
import com.svape.whowhat.data.repository.SupplementRepositoryImpl
import com.svape.whowhat.data.repository.WeightRepositoryImpl
import com.svape.whowhat.data.repository.WorkoutRepositoryImpl
import com.svape.whowhat.domain.repository.SkinCareRepository
import com.svape.whowhat.domain.repository.SupplementRepository
import com.svape.whowhat.domain.repository.WeightRepository
import com.svape.whowhat.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        impl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindWeightRepository(
        impl: WeightRepositoryImpl
    ): WeightRepository

    @Binds
    @Singleton
    abstract fun bindSupplementRepository(
        impl: SupplementRepositoryImpl
    ): SupplementRepository

    @Binds
    @Singleton
    abstract fun bindSkinCareRepository(
        impl: SkinCareRepositoryImpl
    ): SkinCareRepository
}