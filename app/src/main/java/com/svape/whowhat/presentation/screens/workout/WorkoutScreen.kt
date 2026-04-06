package com.svape.whowhat.presentation.screens.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSession
import com.svape.whowhat.domain.model.WorkoutSet
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Entrenamiento", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                WorkoutCalendar(
                    selectedDate = uiState.selectedDate,
                    datesWithSessions = viewModel.datesWithSessions(),
                    onDateSelected = { viewModel.selectDate(it) },
                    onShowDatePicker = { viewModel.showDatePicker() }
                )
            }
            item {
                DaySessionsSection(
                    selectedDate = uiState.selectedDate,
                    sessions = uiState.sessionsForSelectedDate,
                    selectedSession = uiState.selectedSession,
                    sets = uiState.setsForSelectedSession,
                    exercises = uiState.exercises,
                    onAddSession = { viewModel.showAddSessionDialog() },
                    onSelectSession = { viewModel.selectSession(it) },
                    onDeleteSession = { viewModel.deleteSession(it) },
                    onAddSet = { viewModel.showAddSetDialog(it) },
                    onPickExercises = { viewModel.showExercisePickerSheet(it) },
                    onEditSet = { viewModel.showEditSetDialog(it) },
                    onDeleteSet = { viewModel.deleteSet(it) },
                    onShowExerciseProgress = { viewModel.showExerciseProgress(it) },
                    onDuplicateSet = { exerciseId, exerciseName ->
                        viewModel.duplicateLastSet(exerciseId, exerciseName)
                    }
                )
            }
        }

        if (uiState.showDatePicker) {
            WorkoutDatePickerDialog(
                onDateSelected = { viewModel.selectDate(it) },
                onDismiss = { viewModel.dismissDatePicker() }
            )
        }
        if (uiState.showAddSessionDialog) {
            AddSessionDialog(
                onConfirm = { name, notes -> viewModel.saveSession(name, notes) },
                onDismiss = { viewModel.dismissAddSessionDialog() }
            )
        }
        if (uiState.showAddSetDialog) {
            AddSetDialog(
                exercises = uiState.exercises,
                isSaving = uiState.isSavingSet,
                onConfirm = { exerciseId, exerciseName, setNumber, reps, weightLbs ->
                    viewModel.saveSet(exerciseId, exerciseName, setNumber, reps, weightLbs)
                },
                onDismiss = { viewModel.dismissAddSetDialog() },
                onAddExercise = { viewModel.showAddExerciseDialog() }
            )
        }
        if (uiState.showAddExerciseDialog) {
            AddExerciseDialog(
                onConfirm = { name, muscleGroup -> viewModel.saveExercise(name, muscleGroup) },
                onDismiss = { viewModel.dismissAddExerciseDialog() }
            )
        }
        if (uiState.showEditSetDialog && uiState.editingSet != null) {
            EditSetDialog(
                set = uiState.editingSet!!,
                onConfirm = { reps, weight ->
                    viewModel.updateSet(uiState.editingSet!!.id, reps, weight)
                },
                onDismiss = { viewModel.dismissEditSetDialog() }
            )
        }
        if (uiState.showEditExerciseDialog && uiState.editingExercise != null) {
            EditExerciseDialog(
                exercise = uiState.editingExercise!!,
                onConfirm = { id, name, muscleGroup ->
                    viewModel.updateExercise(id, name, muscleGroup)
                },
                onDismiss = { viewModel.dismissEditExerciseDialog() }
            )
        }
        if (uiState.showExercisePickerSheet) {
            ExercisePickerSheet(
                exercises = uiState.exercises,
                selectedIds = uiState.selectedExerciseIds,
                onToggle = { viewModel.toggleExerciseSelection(it) },
                onConfirm = { viewModel.addExercisesToSession(uiState.selectedExerciseIds) },
                onDismiss = { viewModel.dismissExercisePickerSheet() },
                onAddExercise = { viewModel.showAddExerciseDialog() },
                onEditExercise = { viewModel.showEditExerciseDialog(it) },
                onDeleteExercise = { viewModel.deleteExercise(it) }
            )
        }
        if (uiState.showExerciseProgress && uiState.progressExercise != null) {
            ExerciseProgressBottomSheet(
                exercise = uiState.progressExercise!!,
                sets = uiState.progressSets,
                onDismiss = { viewModel.dismissExerciseProgress() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val instant = Instant.fromEpochMilliseconds(millis)
                    val date = instant.toLocalDateTime(TimeZone.UTC).date
                    onDateSelected(date)
                }
                onDismiss()
            }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun WorkoutCalendar(
    selectedDate: LocalDate?,
    datesWithSessions: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onShowDatePicker: () -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val weekOffset by remember(selectedDate) {
        derivedStateOf {
            if (selectedDate == null) return@derivedStateOf 0
            val todayMonday = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
            val selectedMonday = selectedDate.minus(selectedDate.dayOfWeek.ordinal, DateTimeUnit.DAY)
            var diff = 0
            var d = todayMonday
            while (d != selectedMonday) {
                if (selectedMonday > todayMonday) { d = d.plus(1, DateTimeUnit.WEEK); diff++ }
                else { d = d.minus(1, DateTimeUnit.WEEK); diff-- }
                if (diff > 200 || diff < -200) break
            }
            diff
        }
    }

    val startOfWeek by remember(weekOffset) {
        derivedStateOf {
            val current = today.plus(weekOffset * 7, DateTimeUnit.DAY)
            current.minus(current.dayOfWeek.ordinal, DateTimeUnit.DAY)
        }
    }

    val weekDays by remember(startOfWeek) {
        derivedStateOf {
            (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) }
        }
    }

    val monthLabel by remember(weekDays) {
        derivedStateOf {
            val midWeek = weekDays[3]
            val month = midWeek.month.name.lowercase().replaceFirstChar { it.uppercase() }
            "${month.take(3)} ${midWeek.year}"
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDateSelected(startOfWeek.minus(1, DateTimeUnit.DAY)) }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Semana anterior")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onShowDatePicker() }
                ) {
                    Text(
                        monthLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Today,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onDateSelected(startOfWeek.plus(7, DateTimeUnit.DAY)) }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Semana siguiente")
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { date ->
                    DayCell(
                        day = date.dayOfMonth,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        hasSession = date in datesWithSessions,
                        modifier = Modifier.weight(1f),
                        onClick = { onDateSelected(date) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasSession: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                else if (isToday) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(8.dp)
                )
                else Modifier
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onBackground
            }
        )
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        hasSession -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.background
                    }
                )
        )
    }
}

@Composable
private fun DaySessionsSection(
    selectedDate: LocalDate?,
    sessions: List<WorkoutSession>,
    selectedSession: WorkoutSession?,
    sets: List<WorkoutSet>,
    exercises: List<Exercise>,
    onAddSession: () -> Unit,
    onSelectSession: (WorkoutSession) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onAddSet: (WorkoutSession) -> Unit,
    onPickExercises: (WorkoutSession) -> Unit,
    onEditSet: (WorkoutSet) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onShowExerciseProgress: (Exercise) -> Unit,
    onDuplicateSet: (Long, String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

        ExerciseProgressHintCard()

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate?.let { formatDate(it) } ?: "Hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FilledTonalButton(
                onClick = onAddSession,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text("Nueva sesión", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (sessions.isEmpty()) {
            EmptyDayState(onAddSession = onAddSession)
        } else {
            sessions.forEach { session ->
                val isExpanded = selectedSession?.id == session.id
                SessionItem(
                    session = session,
                    isExpanded = isExpanded,
                    sets = if (isExpanded) sets else emptyList(),
                    exercises = exercises,
                    onClick = { onSelectSession(session) },
                    onDelete = { onDeleteSession(session.id) },
                    onAddSet = { onAddSet(session) },
                    onPickExercises = { onPickExercises(session) },
                    onEditSet = onEditSet,
                    onDeleteSet = onDeleteSet,
                    onShowExerciseProgress = onShowExerciseProgress,
                    onDuplicateSet = onDuplicateSet
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EmptyDayState(onAddSession: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onAddSession() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.FitnessCenter, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sin sesiones — toca para agregar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SessionItem(
    session: WorkoutSession,
    isExpanded: Boolean,
    sets: List<WorkoutSet>,
    exercises: List<Exercise>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAddSet: () -> Unit,
    onPickExercises: () -> Unit,
    onEditSet: (WorkoutSet) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onShowExerciseProgress: (Exercise) -> Unit,
    onDuplicateSet: (Long, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isExpanded) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter, null, modifier = Modifier.size(18.dp),
                        tint = if (isExpanded) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExpanded) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    if (session.notes.isNotBlank()) {
                        Text(
                            session.notes, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (sets.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "${sets.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete, "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    if (sets.isEmpty()) {
                        Text(
                            "Sin ejercicios aún — toca 'Ejercicios' para agregar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        sets.groupBy { it.exerciseName }.forEach { (exerciseName, exerciseSets) ->
                            val exercise = exercises.find { it.name == exerciseName }
                            ExerciseSetGroup(
                                exerciseName = exerciseName,
                                sets = exerciseSets,
                                exercise = exercise,
                                onEditSet = onEditSet,
                                onDeleteSet = onDeleteSet,
                                onShowProgress = onShowExerciseProgress,
                                onDuplicateSet = {
                                    exercise?.let { onDuplicateSet(it.id, it.name) }
                                }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onPickExercises,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Ejercicios", style = MaterialTheme.typography.labelLarge)
                        }
                        Button(
                            onClick = onAddSet,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Set manual", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseSetGroup(
    exerciseName: String,
    sets: List<WorkoutSet>,
    exercise: Exercise?,
    onEditSet: (WorkoutSet) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onShowProgress: ((Exercise) -> Unit)?,
    onDuplicateSet: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                exerciseName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            if (exercise != null && onShowProgress != null) {
                IconButton(
                    onClick = { onShowProgress(exercise) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = "Ver progreso",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(
                onClick = onDuplicateSet,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar set",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text(
            "Toca un set para editar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        sets.sortedBy { it.setNumber }.forEach { set ->
            SetRow(
                set = set,
                onEdit = { onEditSet(set) },
                onDelete = { onDeleteSet(set.id) }
            )
        }
    }
}

@Composable
private fun SetRow(
    set: WorkoutSet,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onEdit() }
            .padding(vertical = 6.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (set.weightLbs > 0f)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "${set.setNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (set.weightLbs > 0f)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        if (set.reps > 0) {
            Text(
                "${set.reps} reps",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(56.dp)
            )
        } else {
            Text(
                "— reps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.width(56.dp)
            )
        }
        if (set.weightLbs > 0f) {
            Text(
                "${"%.1f".format(set.weightLbs)} lbs",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                "sin peso",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f)
            )
        }
        Icon(
            Icons.Default.Edit,
            contentDescription = "Editar",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar set",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ExerciseProgressHintCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Toca el ícono de tendencia junto a cada ejercicio para ver tu progreso de peso.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun EditSetDialog(
    set: WorkoutSet,
    onConfirm: (Int, Float) -> Unit,
    onDismiss: () -> Unit
) {
    var reps by remember {
        mutableStateOf(if (set.reps > 0) set.reps.toString() else "")
    }
    var weight by remember {
        mutableStateOf(if (set.weightLbs > 0f) set.weightLbs.toString() else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar set ${set.setNumber}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    set.exerciseName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        placeholder = { Text("ej: 10") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Peso (lbs)") },
                        placeholder = { Text("ej: 75") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = reps.toIntOrNull() ?: return@Button
                    val w = weight.toFloatOrNull() ?: return@Button
                    onConfirm(r, w)
                },
                enabled = reps.isNotBlank() && weight.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExerciseDialog(
    exercise: Exercise,
    onConfirm: (Long, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(exercise.name) }
    var muscleGroup by remember { mutableStateOf(exercise.muscleGroup) }
    val muscleGroups = listOf(
        "Pecho", "Espalda", "Hombros", "Bícep", "Trícep",
        "Pierna", "Glúteo", "Core", "Cardio"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar ejercicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = muscleGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grupo muscular") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        muscleGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = { muscleGroup = group; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && muscleGroup.isNotBlank())
                        onConfirm(exercise.id, name, muscleGroup)
                },
                enabled = name.isNotBlank() && muscleGroup.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSetDialog(
    exercises: List<Exercise>,
    isSaving: Boolean,
    onConfirm: (Long, String, Int, Int, Float) -> Unit,
    onDismiss: () -> Unit,
    onAddExercise: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf(exercises.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }
    var reps by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }
    var setNumber by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar ejercicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (exercises.isEmpty()) {
                    Text(
                        "No tienes ejercicios. Agrega uno primero.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onAddExercise) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Crear ejercicio")
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selectedExercise?.name ?: "", onValueChange = {},
                            readOnly = true, label = { Text("Ejercicio") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            exercises.forEach { exercise ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(exercise.name, fontWeight = FontWeight.Medium)
                                            Text(
                                                exercise.muscleGroup,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = { selectedExercise = exercise; expanded = false }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Add,
                                            null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("Nuevo ejercicio")
                                    }
                                },
                                onClick = { expanded = false; onAddExercise() }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = setNumber,
                            onValueChange = { setNumber = it.filter { c -> c.isDigit() } },
                            label = { Text("Set #") }, singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it.filter { c -> c.isDigit() } },
                            label = { Text("Reps") }, singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = weightLbs,
                        onValueChange = { weightLbs = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Peso (lbs)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exercise = selectedExercise ?: return@Button
                    val repsInt = reps.toIntOrNull() ?: return@Button
                    val weightFloat = weightLbs.toFloatOrNull() ?: return@Button
                    val setInt = setNumber.toIntOrNull() ?: return@Button
                    onConfirm(exercise.id, exercise.name, setInt, repsInt, weightFloat)
                },
                enabled = !isSaving && selectedExercise != null && reps.isNotBlank() && weightLbs.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                else Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun AddSessionDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva sesión") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre (ej: Pierna + Trícep)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, notes) },
                enabled = name.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExerciseDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    val muscleGroups = listOf(
        "Pecho",
        "Espalda",
        "Hombros",
        "Bícep",
        "Trícep",
        "Pierna",
        "Glúteo",
        "Core",
        "Cardio"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo ejercicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = muscleGroup, onValueChange = {}, readOnly = true,
                        label = { Text("Grupo muscular") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        muscleGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = { muscleGroup = group; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && muscleGroup.isNotBlank()) onConfirm(
                        name,
                        muscleGroup
                    )
                },
                enabled = name.isNotBlank() && muscleGroup.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun formatDate(date: LocalDate): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    return when (date) {
        today -> "Hoy"
        yesterday -> "Ayer"
        else -> {
            val day = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            "$day ${date.dayOfMonth}/${date.monthNumber}"
        }
    }
}