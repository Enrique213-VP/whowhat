package com.svape.whowhat.domain.usecase.supplement

import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSupplementLogsUseCase @Inject constructor(
    private val repository: SupplementRepository
) {
    operator fun invoke(): Flow<List<SupplementLog>> = repository.getAllLogs()
}