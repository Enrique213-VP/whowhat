package com.svape.whowhat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.svape.whowhat.data.local.entity.SkinCareProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinCareProductDao {
    @Query("SELECT * FROM skincare_products ORDER BY name ASC")
    fun getAll(): Flow<List<SkinCareProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: SkinCareProductEntity): Long

    @Update
    suspend fun update(product: SkinCareProductEntity)

    @Query("DELETE FROM skincare_products WHERE id = :id")
    suspend fun deleteById(id: Long)
}