package com.svape.whowhat.data.repository

import com.svape.whowhat.data.local.dao.SkinCareLogDao
import com.svape.whowhat.data.local.dao.SkinCareProductDao
import com.svape.whowhat.data.local.entity.SkinCareLogEntity
import com.svape.whowhat.data.local.entity.SkinCareProductEntity
import com.svape.whowhat.domain.model.SkinCareLog
import com.svape.whowhat.domain.model.SkinCareProduct
import com.svape.whowhat.domain.repository.SkinCareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class SkinCareRepositoryImpl @Inject constructor(
    private val logDao: SkinCareLogDao,
    private val productDao: SkinCareProductDao
) : SkinCareRepository {

    override fun getAllLogs(): Flow<List<SkinCareLog>> =
        logDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getLogByDate(date: LocalDate): SkinCareLog? =
        logDao.getByDate(date.toString())?.toDomain()

    override suspend fun insertOrUpdateLog(log: SkinCareLog) {
        logDao.insert(log.toEntity())
    }

    override fun getAllProducts(): Flow<List<SkinCareProduct>> =
        productDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insertProduct(product: SkinCareProduct): Long =
        productDao.insert(product.toEntity())

    override suspend fun updateProduct(product: SkinCareProduct) =
        productDao.update(product.toEntity())

    override suspend fun deleteProduct(productId: Long) =
        productDao.deleteById(productId)

    private fun SkinCareLogEntity.toDomain() = SkinCareLog(
        id = id,
        date = LocalDate.parse(date),
        morningDone = morningDone,
        nightDone = nightDone
    )

    private fun SkinCareLog.toEntity() = SkinCareLogEntity(
        id = id,
        date = date.toString(),
        morningDone = morningDone,
        nightDone = nightDone
    )

    private fun SkinCareProductEntity.toDomain() = SkinCareProduct(
        id = id,
        name = name,
        inStock = inStock,
        needsToBuy = needsToBuy
    )

    private fun SkinCareProduct.toEntity() = SkinCareProductEntity(
        id = id,
        name = name,
        inStock = inStock,
        needsToBuy = needsToBuy
    )
}