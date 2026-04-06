package com.svape.whowhat.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import com.svape.whowhat.domain.usecase.workout.DeleteExerciseUseCase
import com.svape.whowhat.domain.usecase.workout.DeleteWorkoutSessionUseCase
import com.svape.whowhat.domain.usecase.workout.DeleteWorkoutSetUseCase
import com.svape.whowhat.domain.usecase.workout.GetAllExercisesUseCase
import com.svape.whowhat.domain.usecase.workout.GetAllSessionsUseCase
import com.svape.whowhat.domain.usecase.workout.GetSetsByExerciseUseCase
import com.svape.whowhat.domain.usecase.workout.GetSetsBySessionUseCase
import com.svape.whowhat.domain.usecase.workout.SaveExerciseUseCase
import com.svape.whowhat.domain.usecase.workout.SaveWorkoutSessionUseCase
import com.svape.whowhat.domain.usecase.workout.SaveWorkoutSetUseCase
import com.svape.whowhat.domain.usecase.workout.UpdateWorkoutSetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
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
    private val saveWorkoutSetUseCase: SaveWorkoutSetUseCase,
    private val updateWorkoutSetUseCase: UpdateWorkoutSetUseCase,
    private val deleteWorkoutSetUseCase: DeleteWorkoutSetUseCase,
    private val getSetsBySessionUseCase: GetSetsBySessionUseCase,
    private val getSetsByExerciseUseCase: GetSetsByExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var progressJob: Job? = null

    init {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        _uiState.value = _uiState.value.copy(selectedDate = today)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                getAllSessionsUseCase(),
                getAllExercisesUseCase()
            ) { sessions, exercises ->
                val selected = _uiState.value.selectedDate
                _uiState.value.copy(
                    sessions = sessions,
                    exercises = exercises,
                    sessionsForSelectedDate = sessions.filter { it.date == selected },
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            sessionsForSelectedDate = _uiState.value.sessions.filter { it.date == date },
            selectedSession = null,
            setsForSelectedSession = emptyList()
        )
    }

    fun selectSession(session: WorkoutSession) {
        if (_uiState.value.selectedSession?.id == session.id) {
            _uiState.value = _uiState.value.copy(
                selectedSession = null,
                setsForSelectedSession = emptyList()
            )
            return
        }
        _uiState.value = _uiState.value.copy(selectedSession = session)
        viewModelScope.launch {
            getSetsBySessionUseCase(session.id).collect { sets ->
                _uiState.value = _uiState.value.copy(setsForSelectedSession = sets)
            }
        }
    }

    fun saveSession(name: String, notes: String = "") {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate
                ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
            saveWorkoutSessionUseCase(
                WorkoutSession(name = name, date = date, notes = notes)
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            _uiState.value = _uiState.value.copy(showAddSessionDialog = false)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            deleteWorkoutSessionUseCase(sessionId)
            if (_uiState.value.selectedSession?.id == sessionId) {
                _uiState.value = _uiState.value.copy(
                    selectedSession = null,
                    setsForSelectedSession = emptyList()
                )
            }
        }
    }

    fun saveExercise(name: String, muscleGroup: String) {
        viewModelScope.launch {
            saveExerciseUseCase(
                Exercise(name = name, muscleGroup = muscleGroup)
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            _uiState.value = _uiState.value.copy(showAddExerciseDialog = false)
        }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch { deleteExerciseUseCase(exerciseId) }
    }

    fun saveSet(
        exerciseId: Long,
        exerciseName: String,
        setNumber: Int,
        reps: Int,
        weightLbs: Float
    ) {
        val sessionId = _uiState.value.selectedSession?.id ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecciona una sesión primero")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingSet = true)
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
            _uiState.value = _uiState.value.copy(isSavingSet = false, showAddSetDialog = false)
        }
    }

    fun addExercisesToSession(exerciseIds: Set<Long>) {
        val sessionId = _uiState.value.selectedSession?.id ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecciona una sesión primero")
            return
        }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val exercises = _uiState.value.exercises.filter { it.id in exerciseIds }
        viewModelScope.launch {
            exercises.forEach { exercise ->
                val currentSets = _uiState.value.setsForSelectedSession
                    .filter { it.exerciseId == exercise.id }
                val nextSetNumber = (currentSets.maxOfOrNull { it.setNumber } ?: 0) + 1
                saveWorkoutSetUseCase(
                    WorkoutSet(
                        sessionId = sessionId,
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        setNumber = nextSetNumber,
                        reps = 0,
                        weightLbs = 0f,
                        date = today
                    )
                ).onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            }
            _uiState.value = _uiState.value.copy(
                showExercisePickerSheet = false,
                selectedExerciseIds = emptySet()
            )
        }
    }

    fun toggleExerciseSelection(exerciseId: Long) {
        val current = _uiState.value.selectedExerciseIds.toMutableSet()
        if (exerciseId in current) current.remove(exerciseId) else current.add(exerciseId)
        _uiState.value = _uiState.value.copy(selectedExerciseIds = current)
    }

    fun updateSet(setId: Long, reps: Int, weightLbs: Float) {
        val current = _uiState.value.editingSet ?: return
        viewModelScope.launch {
            updateWorkoutSetUseCase(current.copy(reps = reps, weightLbs = weightLbs))
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
            _uiState.value = _uiState.value.copy(showEditSetDialog = false, editingSet = null)
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch { deleteWorkoutSetUseCase(setId) }
    }

    fun showExercisePickerSheet(session: WorkoutSession) {
        _uiState.value = _uiState.value.copy(
            selectedSession = session,
            showExercisePickerSheet = true,
            selectedExerciseIds = emptySet()
        )
    }

    fun dismissExercisePickerSheet() {
        _uiState.value = _uiState.value.copy(
            showExercisePickerSheet = false,
            selectedExerciseIds = emptySet()
        )
    }

    fun showEditSetDialog(set: WorkoutSet) {
        _uiState.value = _uiState.value.copy(editingSet = set, showEditSetDialog = true)
    }

    fun dismissEditSetDialog() {
        _uiState.value = _uiState.value.copy(showEditSetDialog = false, editingSet = null)
    }

    fun showAddSessionDialog() {
        _uiState.value = _uiState.value.copy(showAddSessionDialog = true)
    }

    fun dismissAddSessionDialog() {
        _uiState.value = _uiState.value.copy(showAddSessionDialog = false)
    }

    fun showAddSetDialog(session: WorkoutSession) {
        _uiState.value = _uiState.value.copy(selectedSession = session, showAddSetDialog = true)
    }

    fun dismissAddSetDialog() {
        _uiState.value = _uiState.value.copy(showAddSetDialog = false)
    }

    fun showAddExerciseDialog() {
        _uiState.value = _uiState.value.copy(showAddExerciseDialog = true)
    }

    fun dismissAddExerciseDialog() {
        _uiState.value = _uiState.value.copy(showAddExerciseDialog = false)
    }

    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    fun dismissDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    fun datesWithSessions(): Set<LocalDate> =
        _uiState.value.sessions.map { it.date }.toSet()

    fun showExerciseProgress(exercise: Exercise) {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            getSetsByExerciseUseCase(exercise.id).collect { sets ->
                _uiState.value = _uiState.value.copy(
                    showExerciseProgress = true,
                    progressExercise = exercise,
                    progressSets = sets
                )
            }
        }
    }

    fun dismissExerciseProgress() {
        progressJob?.cancel()
        progressJob = null
        _uiState.value = _uiState.value.copy(
            showExerciseProgress = false,
            progressExercise = null,
            progressSets = emptyList()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }


    fun duplicateLastSet(exerciseId: Long, exerciseName: String) {
        val sessionId = _uiState.value.selectedSession?.id ?: run {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecciona una sesión primero")
            return
        }
        val existingSets = _uiState.value.setsForSelectedSession
            .filter { it.exerciseId == exerciseId }
        val lastSet = existingSets.maxByOrNull { it.setNumber }
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            saveWorkoutSetUseCase(
                WorkoutSet(
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    setNumber = (lastSet?.setNumber ?: 0) + 1,
                    reps = lastSet?.reps ?: 0,
                    weightLbs = lastSet?.weightLbs ?: 0f,
                    date = today
                )
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun showEditExerciseDialog(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(
            editingExercise = exercise,
            showEditExerciseDialog = true
        )
    }

    fun dismissEditExerciseDialog() {
        _uiState.value = _uiState.value.copy(
            showEditExerciseDialog = false,
            editingExercise = null
        )
    }

    fun updateExercise(id: Long, name: String, muscleGroup: String) {
        viewModelScope.launch {
            saveExerciseUseCase(
                Exercise(id = id, name = name, muscleGroup = muscleGroup)
            ).onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            _uiState.value = _uiState.value.copy(
                showEditExerciseDialog = false,
                editingExercise = null
            )
        }
    }
}