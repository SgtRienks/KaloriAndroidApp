package com.example.gainscounterapp.data.local

import androidx.room.*
import com.example.gainscounterapp.model.DailyLog
import com.example.gainscounterapp.model.DailyFoodEntry

@Dao
interface DailyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyLog(log: DailyLog)

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLog(date: String): DailyLog?

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    suspend fun getAllLogs(): List<DailyLog>

    @Insert
    suspend fun insertFoodEntry(entry: DailyFoodEntry)

    @Query("SELECT * FROM daily_food_entries WHERE date = :date")
    suspend fun getFoodsForDay(date: String): List<DailyFoodEntry>

    @Delete
    suspend fun deleteFoodEntry(entry: DailyFoodEntry)
}
