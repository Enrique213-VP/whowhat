package com.svape.whowhat.domain.usecase.supplement

import com.svape.whowhat.domain.model.SupplementLog
import com.svape.whowhat.domain.repository.SupplementRepository
import javax.inject.Inject

class LogSupplementUseCase @Inject constructor(
    private val repository: SupplementRepository
) {
    suspend operator fun invoke(log: SupplementLog) =
        repository.insertOrUpdateLog(log)
}