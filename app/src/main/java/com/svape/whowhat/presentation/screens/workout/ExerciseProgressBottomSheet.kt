package com.svape.whowhat.presentation.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.SheetValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svape.whowhat.domain.model.Exercise
import com.svape.whowhat.domain.model.WorkoutSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseProgressBottomSheet(
    exercise: Exercise,
    sets: List<WorkoutSet>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )

    val sortedSets = sets.sortedBy { it.date }
    val bestSet = sortedSets.maxByOrNull { it.weightLbs }
    val latestSessionSets = sortedSets
        .groupBy { it.date }
        .entries
        .maxByOrNull { it.key }
        ?.value ?: emptyList()
    val prevSessionSets = sortedSets
        .groupBy { it.date }
        .entries
        .sortedByDescending { it.key }
        .getOrNull(1)
        ?.value ?: emptyList()

    val sessionGroups = sortedSets
        .groupBy { it.date }
        .entries
        .sortedByDescending { it.key }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        exercise.muscleGroup,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                ProgressStatsRow(
                    bestSet = bestSet,
                    totalSessions = sessionGroups.size,
                    totalSets = sortedSets.size
                )
            }

            if (sortedSets.size >= 2) {
                item {
                    WeightChart(
                        sets = sortedSets,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (latestSessionSets.isNotEmpty()) {
                item {
                    ComparisonTable(
                        latestSets = latestSessionSets,
                        prevSets = prevSessionSets,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        "Historial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    sessionGroups.forEach { (date, sessionSets) ->
                        SessionHistoryRow(
                            dateLabel = formatProgressDate(date),
                            sets = sessionSets.sortedBy { it.setNumber }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressStatsRow(
    bestSet: WorkoutSet?,
    totalSessions: Int,
    totalSets: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            label = "Mejor peso",
            value = bestSet?.let { "${"%.1f".format(it.weightLbs)} lbs" } ?: "--",
            modifier = Modifier.weight(1f),
            highlight = true
        )
        StatCard(
            label = "Sesiones",
            value = "$totalSessions",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total sets",
            value = "$totalSets",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (highlight) {
                androidx.compose.material3.Icon(
                    Icons.Default.EmojiEvents,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (highlight) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeightChart(
    sets: List<WorkoutSet>,
    modifier: Modifier = Modifier
) {
    val maxWeightPerSession = sets
        .groupBy { it.date }
        .entries
        .sortedBy { it.key }
        .map { (date, sessionSets) ->
            date to sessionSets.maxOf { it.weightLbs }
        }

    if (maxWeightPerSession.size < 2) return

    val maxVal = maxWeightPerSession.maxOf { it.second }
    val minVal = maxWeightPerSession.minOf { it.second }
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Column(modifier = modifier) {
        Text(
            "Evolución del peso máximo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            val barColor = MaterialTheme.colorScheme.primary
            val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                maxWeightPerSession.forEach { (date, weight) ->
                    val heightFraction = ((weight - minVal) / range * 0.75f + 0.25f)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            "${"%.0f".format(weight)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = labelColor,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height((80 * heightFraction).dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(maxWeightPerSession) { (date, _) ->
                Text(
                    "${date.dayOfMonth}/${date.monthNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ComparisonTable(
    latestSets: List<WorkoutSet>,
    prevSets: List<WorkoutSet>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Última vs anterior",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Set", modifier = Modifier.width(36.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Última", modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                    Text("Anterior", modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center)
                    Text("Cambio", modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End)
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                val maxSets = maxOf(latestSets.size, prevSets.size)
                (1..maxSets).forEach { setNum ->
                    val latest = latestSets.find { it.setNumber == setNum }
                    val prev = prevSets.find { it.setNumber == setNum }
                    val diff = if (latest != null && prev != null)
                        latest.weightLbs - prev.weightLbs
                    else null

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$setNum",
                            modifier = Modifier.width(36.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            latest?.let { "${it.reps}r × ${"%.0f".format(it.weightLbs)}lb" } ?: "—",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            prev?.let { "${it.reps}r × ${"%.0f".format(it.weightLbs)}lb" } ?: "—",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when {
                                diff == null -> Text("—", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                diff > 0f -> {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.TrendingUp, null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        "+${"%.1f".format(diff)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                diff < 0f -> {
                                    androidx.compose.material3.Icon(
                                        Icons.Default.TrendingDown, null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        "${"%.1f".format(diff)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                else -> Text("=", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionHistoryRow(
    dateLabel: String,
    sets: List<WorkoutSet>
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                dateLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            sets.forEach { set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Set ${set.setNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(44.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${set.reps} reps",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(56.dp)
                    )
                    Text(
                        "${"%.1f".format(set.weightLbs)} lbs",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

private fun formatProgressDate(date: kotlinx.datetime.LocalDate): String {
    val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$dayName ${date.dayOfMonth}/${date.monthNumber}/${date.year}"
}