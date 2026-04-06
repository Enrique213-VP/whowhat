package com.svape.whowhat.presentation.screens.workout

import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet

data class WorkoutUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val setsForSession: List<WorkoutSet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddSessionDialog: Boolean = false,
    val showAddSetDialog: Boolean = false,
    val selectedSession: WorkoutSession? = null,
    val selectedExercise: Exercise? = null
)