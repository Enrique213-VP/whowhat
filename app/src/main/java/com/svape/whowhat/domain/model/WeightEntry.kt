package com.svape.whowhat.domain.model

import kotlinx.datetime.LocalDate

data class WeightEntry(
    val id: Long = 0,
    val weightKg: Float,
    val date: LocalDate,
    val notes: String = ""
)