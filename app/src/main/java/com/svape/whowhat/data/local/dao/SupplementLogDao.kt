package com.svape.whowhat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.svape.whowhat.data.local.entity.SupplementLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplementLogDao {
    @Query("SELECT * FROM supplement_logs ORDER BY date DESC")
    fun getAll(): Flow<List<SupplementLogEntity>>

    @Query("SELECT * FROM supplement_logs WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SupplementLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SupplementLogEntity): Long
}