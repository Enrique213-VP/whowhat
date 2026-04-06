package com.svape.whowhat.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.usecase.workout.DeleteExerciseUseCase
import com.svape.whowhat.domain.usecase.workout.DeleteWorkoutSessionUseCase
import com.svape.whowhat.domain.usecase.workout.GetAllExercisesUseCase
import com.svape.whowhat.domain.usecase.workout.GetAllSessionsUseCase
import com.svape.whowhat.domain.usecase.workout.SaveExerciseUseCase
import com.svape.whowhat.domain.usecase.workout.SaveWorkoutSessionUseCase
import com.svape.whowhat.domain.usecase.workout.SaveWorkoutSetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val saveWorkoutSessionUseCase: SaveWorkoutSessionUseCase,
    private val deleteWorkoutSessionUseCase: DeleteWorkoutSessionUseCase,
    private val getAllExercisesUseCase: GetAllExercisesUseCase,
    private val saveExerciseUseCase: SaveExerciseUseCase,
    private val deleteExerciseUseCase: DeleteExerciseUseCase,
    private val saveWorkoutSetUseCase: SaveWorkoutSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAllSessionsUseCase(),
                getAllExercisesUseCase()
            ) { sessions, exercises ->
                _uiState.value.copy(
                    sessions = sessions,
                    exercises = exercises,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun saveSession(name: String, notes: String = "") {
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            saveWorkoutSessionUseCase(
                WorkoutSession(
                    name = name,
                    date = today,
                    notes = notes
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            dismissAddSessionDialog()
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            deleteWorkoutSessionUseCase(sessionId)
        }
    }

    fun saveExercise(name: String, muscleGroup: String, notes: String = "") {
        viewModelScope.launch {
            saveExerciseUseCase(
                Exercise(
                    name = name,
                    muscleGroup = muscleGroup,
                    notes = notes
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            deleteExerciseUseCase(exerciseId)
        }
    }

    fun saveSet(
        exerciseId: Long,
        exerciseName: String,
        setNumber: Int,
        reps: Int,
        weightLbs: Float
    ) {
        val sessionId = _uiState.value.selectedSession?.id ?: return
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            saveWorkoutSetUseCase(
                WorkoutSet(
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    setNumber = setNumber,
                    reps = reps,
                    weightLbs = weightLbs,
                    date = today
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            dismissAddSetDialog()
        }
    }

    fun selectSession(session: WorkoutSession) {
        _uiState.value = _uiState.value.copy(selectedSession = session)
    }

    fun selectExercise(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(selectedExercise = exercise)
    }

    fun showAddSessionDialog() {
        _uiState.value = _uiState.value.copy(showAddSessionDialog = true)
    }

    fun dismissAddSessionDialog() {
        _uiState.value = _uiState.value.copy(showAddSessionDialog = false)
    }

    fun showAddSetDialog(session: WorkoutSession) {
        _uiState.value = _uiState.value.copy(
            selectedSession = session,
            showAddSetDialog = true
        )
    }

    fun dismissAddSetDialog() {
        _uiState.value = _uiState.value.copy(showAddSetDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}