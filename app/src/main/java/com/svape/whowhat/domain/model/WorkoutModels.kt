package com.svape.whowhat.domain.model

import kotlinx.datetime.LocalDate

data class WorkoutSession(
    val id: Long = 0,
    val name: String,
    val date: LocalDate,
    val notes: String = ""
)

data class WorkoutSet(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int,
    val weightLbs: Float,
    val date: LocalDate
)