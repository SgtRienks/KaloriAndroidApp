package com.example.gainscounterapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_food_entries")
data class DailyFoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,   // YYYY-MM-DD
    val foodId: Int,
    val grams: Float
)
