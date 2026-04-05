package com.svape.whowhat.domain.usecase.skincare

import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.repository.SkinCareRepository
import javax.inject.Inject

class LogSkincareUseCase @Inject constructor(
    private val repository: SkinCareRepository
) {
    suspend operator fun invoke(log: SkinCareLog) =
        repository.insertOrUpdateLog(log)
}