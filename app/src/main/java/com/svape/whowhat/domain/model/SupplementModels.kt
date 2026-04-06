package com.svape.whowhat.domain.model

import kotlinx.datetime.LocalDate

data class SupplementLog(
    val id: Long = 0,
    val date: LocalDate,
    val creatineTaken: Boolean,
    val proteinAvailable: Boolean,
    val proteinTaken: Boolean?,
    val notes: String = ""
)