package com.svape.whowhat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svape.whowhat.data.local.entity.SkinCareLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinCareLogDao {
    @Query("SELECT * FROM skincare_logs ORDER BY date DESC")
    fun getAll(): Flow<List<SkinCareLogEntity>>

    @Query("SELECT * FROM skincare_logs WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SkinCareLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SkinCareLogEntity): Long
}