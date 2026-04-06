package com.svape.whowhat.presentation.screens.skincare

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
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
fun SkinCareScreen(
    viewModel: SkinCareViewModel = hiltViewModel()
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
                title = { Text("Skincare", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddProductDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
            }
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
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                SkinCareCalendar(
                    selectedDate = uiState.selectedDate,
                    datesWithLogs = viewModel.datesWithLogs(),
                    onDateSelected = { viewModel.selectDate(it) },
                    onShowDatePicker = { viewModel.showDatePicker() }
                )
            }
            item {
                SkinCareStreakCard(
                    streakDays = uiState.streak.streakDays,
                    compliance = uiState.streak.compliancePercent,
                    totalDays = uiState.streak.totalDaysLogged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                DaySkinCareSection(
                    selectedDate = uiState.selectedDate,
                    log = uiState.selectedDateLog,
                    isToday = isToday,
                    onToggleMorning = { viewModel.toggleMorning() },
                    onToggleNight = { viewModel.toggleNight() },
                    onEdit = { viewModel.showEditLogDialog() },
                    onCreateLog = { viewModel.createLogForDate() }
                )
            }
            if (uiState.products.isNotEmpty()) {
                item {
                    Text(
                        text = "Mis productos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                    )
                }
                items(uiState.products, key = { it.id }) { product ->
                    SkinCareProductCard(
                        product = product,
                        onToggleStock = { viewModel.updateStock(product, it) },
                        onDelete = { viewModel.deleteProduct(product.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            } else {
                item {
                    EmptyProductsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (uiState.showDatePicker) {
            SkinCareDatePickerDialog(
                onDateSelected = { viewModel.selectDate(it) },
                onDismiss = { viewModel.dismissDatePicker() }
            )
        }
        if (uiState.showAddProductDialog) {
            AddProductDialog(
                onConfirm = { viewModel.saveProduct(it) },
                onDismiss = { viewModel.dismissAddProductDialog() }
            )
        }
        if (uiState.showEditLogDialog && uiState.selectedDateLog != null) {
            EditSkinCareLogDialog(
                log = uiState.selectedDateLog!!,
                onConfirm = { morning, night ->
                    viewModel.saveEditedLog(morning, night)
                },
                onDismiss = { viewModel.dismissEditLogDialog() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkinCareDatePickerDialog(
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
private fun SkinCareCalendar(
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
                    SkinCareDayCell(
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
private fun SkinCareDayCell(
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
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.secondary)
                else if (isToday) Modifier.border(
                    1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)
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
                isSelected -> MaterialTheme.colorScheme.onSecondary
                isToday -> MaterialTheme.colorScheme.secondary
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
                        isSelected -> MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                        hasLog -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.background
                    }
                )
        )
    }
}

@Composable
private fun SkinCareStreakCard(
    streakDays: Int,
    compliance: Float,
    totalDays: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
            SkinCareStatItem("🔥 Racha", "$streakDays días")
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f)
                    )
            )
            SkinCareStatItem("Cumplimiento", "${"%.0f".format(compliance)}%")
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f)
                    )
            )
            SkinCareStatItem("Total días", "$totalDays")
        }
    }
}

@Composable
private fun SkinCareStatItem(label: String, value: String) {
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
private fun DaySkinCareSection(
    selectedDate: LocalDate?,
    log: SkinCareLog?,
    isToday: Boolean,
    onToggleMorning: () -> Unit,
    onToggleNight: () -> Unit,
    onEdit: () -> Unit,
    onCreateLog: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate?.let { formatSkinCareDate(it) } ?: "Hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (log != null && !isToday) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
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
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
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

        val morningDone = log?.morningDone ?: false
        val nightDone = log?.nightDone ?: false

        SkinCareButton(
            label = "Rutina mañana ☀️",
            sublabel = if (morningDone) "Completada" else "Pendiente",
            isActive = morningDone,
            activeColor = MaterialTheme.colorScheme.secondary,
            activeOnColor = MaterialTheme.colorScheme.onSecondary,
            onClick = if (isToday) onToggleMorning else ({})
        )

        Spacer(Modifier.height(10.dp))

        SkinCareButton(
            label = "Rutina noche 🌙",
            sublabel = if (nightDone) "Completada" else "Pendiente",
            isActive = nightDone,
            activeColor = MaterialTheme.colorScheme.secondary,
            activeOnColor = MaterialTheme.colorScheme.onSecondary,
            onClick = if (isToday) onToggleNight else ({})
        )

        if (!isToday && log != null) {
            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text("Editar este registro")
            }
        }
    }
}

@Composable
private fun SkinCareButton(
    label: String,
    sublabel: String,
    isActive: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    activeOnColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val containerColor = if (isActive) activeColor
    else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isActive) activeOnColor
    else MaterialTheme.colorScheme.onSurfaceVariant

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
                    color = activeOnColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = activeOnColor,
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
private fun SkinCareProductCard(
    product: SkinCareProduct,
    onToggleStock: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.needsToBuy)
                MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = when {
                        product.needsToBuy -> "Necesita comprarse"
                        product.inStock -> "En stock"
                        else -> "Sin stock"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (product.needsToBuy)
                        MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = product.inStock,
                onCheckedChange = onToggleStock
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyProductsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Spa, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Toca + para agregar productos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun AddProductDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo producto") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EditSkinCareLogDialog(
    log: SkinCareLog,
    onConfirm: (Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var morningDone by remember { mutableStateOf(log.morningDone) }
    var nightDone by remember { mutableStateOf(log.nightDone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar registro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    formatSkinCareDate(log.date),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                SkinCareEditToggleRow(
                    label = "Rutina mañana ☀️",
                    isActive = morningDone,
                    onToggle = { morningDone = !morningDone }
                )
                HorizontalDivider()
                SkinCareEditToggleRow(
                    label = "Rutina noche 🌙",
                    isActive = nightDone,
                    onToggle = { nightDone = !nightDone }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(morningDone, nightDone) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun SkinCareEditToggleRow(
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
            color = if (isActive) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isActive) {
                    Icon(
                        Icons.Default.Check, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

private fun formatSkinCareDate(date: LocalDate): String {
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