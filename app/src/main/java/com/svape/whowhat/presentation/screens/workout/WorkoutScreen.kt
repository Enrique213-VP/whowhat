package com.svape.whowhat.presentation.screens.workout

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.svape.whowhat.domain.model.WorkoutSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Rutinas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddSessionDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar sesión")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.sessions.isEmpty() -> {
                    EmptyWorkoutState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    SessionList(
                        sessions = uiState.sessions,
                        onDeleteSession = { viewModel.deleteSession(it) },
                        onAddSet = { viewModel.showAddSetDialog(it) }
                    )
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
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
                onConfirm = { exerciseId, exerciseName, setNumber, reps, weightLbs ->
                    viewModel.saveSet(exerciseId, exerciseName, setNumber, reps, weightLbs)
                },
                onDismiss = { viewModel.dismissAddSetDialog() }
            )
        }
    }
}

@Composable
private fun EmptyWorkoutState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.FitnessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No hay sesiones aún",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Toca + para agregar tu primera rutina",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SessionList(
    sessions: List<WorkoutSession>,
    onDeleteSession: (Long) -> Unit,
    onAddSet: (WorkoutSession) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sessions, key = { it.id }) { session ->
            SessionCard(
                session = session,
                onDelete = { onDeleteSession(session.id) },
                onAddSet = { onAddSet(session) }
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: WorkoutSession,
    onDelete: () -> Unit,
    onAddSet: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (session.notes.isNotBlank()) {
                    Text(
                        text = session.notes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row {
                IconButton(onClick = onAddSet) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar set")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej: Pierna + Trícep)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, notes) }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun AddSetDialog(
    exercises: List<com.svape.whowhat.domain.model.Exercise>,
    onConfirm: (Long, String, Int, Int, Float) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf(exercises.firstOrNull()) }
    var setNumber by remember { mutableStateOf("1") }
    var reps by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar set") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ejercicio: ${selectedExercise?.name ?: "Ninguno"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = setNumber,
                    onValueChange = { setNumber = it },
                    label = { Text("# Set") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Repeticiones") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = weightLbs,
                    onValueChange = { weightLbs = it },
                    label = { Text("Peso (lbs)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val exercise = selectedExercise ?: return@TextButton
                val repsInt = reps.toIntOrNull() ?: return@TextButton
                val weightFloat = weightLbs.toFloatOrNull() ?: return@TextButton
                val setInt = setNumber.toIntOrNull() ?: return@TextButton
                onConfirm(exercise.id, exercise.name, setInt, repsInt, weightFloat)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}