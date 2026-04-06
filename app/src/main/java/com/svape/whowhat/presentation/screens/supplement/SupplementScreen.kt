package com.svape.whowhat.presentation.screens.supplement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svape.whowhat.domain.model.SupplementLog
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
fun SupplementScreen(
    viewModel: SupplementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val isToday = uiState.selectedDate == today

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
                title = { Text("Suplementos", style = MaterialTheme.typography.titleLarge) },
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
                SupplementCalendar(
                    selectedDate = uiState.selectedDate,
                    datesWithLogs = viewModel.datesWithLogs(),
                    onDateSelected = { viewModel.selectDate(it) },
                    onShowDatePicker = { viewModel.showDatePicker() }
                )
            }
            item {
                StreakCard(
                    streakDays = uiState.streak.creatineStreakDays,
                    compliance = uiState.streak.creatineCompliancePercent,
                    totalDays = uiState.streak.totalDaysLogged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                ProteinTrackingCard(
                    streak = uiState.streak,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            item {
                DaySupplementSection(
                    selectedDate = uiState.selectedDate,
                    log = uiState.selectedDateLog,
                    isToday = isToday,
                    onToggleCreatine = { viewModel.toggleCreatine() },
                    onToggleProteinAvailable = { viewModel.toggleProteinAvailable() },
                    onToggleProtein = { viewModel.toggleProtein() },
                    onEdit = { viewModel.showEditLogDialog() },
                    onCreateLog = { viewModel.createLogForDate() }
                )
            }
        }

        if (uiState.showDatePicker) {
            SupplementDatePickerDialog(
                onDateSelected = { viewModel.selectDate(it) },
                onDismiss = { viewModel.dismissDatePicker() }
            )
        }

        if (uiState.showEditLogDialog && uiState.selectedDateLog != null) {
            EditSupplementLogDialog(
                log = uiState.selectedDateLog!!,
                onConfirm = { creatine, proteinAvailable, proteinTaken ->
                    viewModel.saveEditedLog(creatine, proteinAvailable, proteinTaken)
                },
                onDismiss = { viewModel.dismissEditLogDialog() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupplementDatePickerDialog(
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
private fun SupplementCalendar(
    selectedDate: LocalDate?,
    datesWithLogs: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onShowDatePicker: () -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val weekOffset by remember(selectedDate) {
        derivedStateOf {
            if (selectedDate == null) return@derivedStateOf 0
            val todayMonday = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
            val selectedMonday =
                selectedDate.minus(selectedDate.dayOfWeek.ordinal, DateTimeUnit.DAY)
            var diff = 0
            var d = todayMonday
            while (d != selectedMonday) {
                if (selectedMonday > todayMonday) {
                    d = d.plus(1, DateTimeUnit.WEEK); diff++
                } else {
                    d = d.minus(1, DateTimeUnit.WEEK); diff--
                }
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
        derivedStateOf { (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) } }
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
                IconButton(onClick = {
                    onDateSelected(startOfWeek.minus(1, DateTimeUnit.DAY))
                }) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Semana anterior")
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
                IconButton(onClick = {
                    onDateSelected(startOfWeek.plus(7, DateTimeUnit.DAY))
                }) {
                    Icon(Icons.Default.KeyboardArrowRight, "Semana siguiente")
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
                    SupplementDayCell(
                        day = date.dayOfMonth,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        hasLog = date in datesWithLogs,
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
private fun ProteinTrackingCard(
    streak: com.svape.whowhat.domain.model.SupplementStreak,
    modifier: Modifier = Modifier
) {
    if (streak.proteinDaysAvailable == 0) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Proteína",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProteinStatItem(
                    label = "🔥 Racha",
                    value = "${streak.proteinStreakDays} días"
                )
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp)
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f)
                        )
                )
                ProteinStatItem(
                    label = "Tomada",
                    value = "${streak.proteinDaysTaken} días"
                )
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp)
                        .background(
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f)
                        )
                )
                ProteinStatItem(
                    label = "Disponible",
                    value = "${streak.proteinDaysAvailable} días"
                )
            }

            Spacer(Modifier.height(12.dp))

            val progress = (streak.proteinCompliancePercent / 100f).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cumplimiento cuando hay proteína",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    "${"%.0f".format(streak.proteinCompliancePercent)}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(Modifier.height(6.dp))

            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun ProteinStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SupplementDayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasLog: Boolean,
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
                    1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)
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
                        hasLog -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.background
                    }
                )
        )
    }
}

@Composable
private fun StreakCard(
    streakDays: Int,
    compliance: Float,
    totalDays: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakStatItem("🔥 Racha", "$streakDays días")
            VerticalDivider()
            StreakStatItem("Cumplimiento", "${"%.0f".format(compliance)}%")
            VerticalDivider()
            StreakStatItem("Total días", "$totalDays")
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
    )
}

@Composable
private fun StreakStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun DaySupplementSection(
    selectedDate: LocalDate?,
    log: SupplementLog?,
    isToday: Boolean,
    onToggleCreatine: () -> Unit,
    onToggleProteinAvailable: () -> Unit,
    onToggleProtein: () -> Unit,
    onEdit: () -> Unit,
    onCreateLog: () -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate?.let { formatSupplementDate(it) } ?: "Hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (log != null && !isToday) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (log == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onCreateLog() }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tocar para registrar este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            return
        }

        val creatineTaken = log?.creatineTaken ?: false
        val proteinAvailable = log?.proteinAvailable ?: false
        val proteinTaken = log?.proteinTaken ?: false

        SupplementButton(
            label = "Creatina",
            sublabel = "Diaria obligatoria",
            isActive = creatineTaken,
            onClick = if (isToday) onToggleCreatine else ({})
        )

        Spacer(Modifier.height(10.dp))

        SupplementButton(
            label = "Tengo proteína",
            sublabel = if (proteinAvailable) "Disponible" else "No disponible",
            isActive = proteinAvailable,
            onClick = if (isToday) onToggleProteinAvailable else ({})
        )

        if (proteinAvailable) {
            Spacer(Modifier.height(10.dp))
            SupplementButton(
                label = "Proteína tomada",
                sublabel = if (proteinTaken) "Ya la tomé hoy" else "Pendiente",
                isActive = proteinTaken,
                onClick = if (isToday) onToggleProtein else ({})
            )
        }

        if (!isToday && log != null) {
            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Edit,
                    null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Editar este registro")
            }
        }
    }
}

@Composable
private fun SupplementButton(
    label: String,
    sublabel: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isActive)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val contentColor = if (isActive)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.75f)
                )
            }
            if (isActive) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                )
            }
        }
    }
}

@Composable
private fun EditSupplementLogDialog(
    log: SupplementLog,
    onConfirm: (Boolean, Boolean, Boolean?) -> Unit,
    onDismiss: () -> Unit
) {
    var creatine by remember { mutableStateOf(log.creatineTaken) }
    var proteinAvailable by remember { mutableStateOf(log.proteinAvailable) }
    var proteinTaken by remember { mutableStateOf(log.proteinTaken ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    formatSupplementDate(log.date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                EditToggleRow(
                    label = "Creatina tomada",
                    isActive = creatine,
                    onToggle = { creatine = !creatine }
                )
                HorizontalDivider()
                EditToggleRow(
                    label = "Proteína disponible",
                    isActive = proteinAvailable,
                    onToggle = {
                        proteinAvailable = !proteinAvailable
                        if (!proteinAvailable) proteinTaken = false
                    }
                )
                if (proteinAvailable) {
                    EditToggleRow(
                        label = "Proteína tomada",
                        isActive = proteinTaken,
                        onToggle = { proteinTaken = !proteinTaken }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(creatine, proteinAvailable, if (proteinAvailable) proteinTaken else null)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditToggleRow(
    label: String,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Surface(
            shape = CircleShape,
            color = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isActive) {
                    Icon(
                        Icons.Default.Check,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

private fun formatSupplementDate(date: LocalDate): String {
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