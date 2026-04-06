package com.svape.whowhat.presentation.screens.workout

import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import kotlinx.datetime.LocalDate

data class WorkoutUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val setsForSelectedSession: List<WorkoutSet> = emptyList(),
    val selectedDate: LocalDate? = null,
    val sessionsForSelectedDate: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDatePicker: Boolean = false,
    val showAddSessionDialog: Boolean = false,
    val showAddSetDialog: Boolean = false,
    val showAddExerciseDialog: Boolean = false,
    val showExercisePickerSheet: Boolean = false,
    val showEditSetDialog: Boolean = false,
    val selectedSession: WorkoutSession? = null,
    val editingSet: WorkoutSet? = null,
    val isSavingSet: Boolean = false,
    val selectedExerciseIds: Set<Long> = emptySet(),
    val showExerciseProgress: Boolean = false,
    val progressExercise: Exercise? = null,
    val progressSets: List<WorkoutSet> = emptyList(),
    val showEditExerciseDialog: Boolean = false,
    val editingExercise: Exercise? = null
)