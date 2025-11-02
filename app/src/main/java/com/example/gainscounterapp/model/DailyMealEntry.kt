package com.example.gainscounterapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_meal_entries")
data class DailyMealEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val mealName: String // name = ID for meal
)