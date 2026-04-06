package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSkincareLogsUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    operator fun invoke(): Flow<List<SkinCareLog>> = repository.getAllLogs()
}