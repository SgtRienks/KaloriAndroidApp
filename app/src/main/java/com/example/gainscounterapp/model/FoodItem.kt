package com.example.gainscounterapp.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "food_items",
        indices = [Index(value = ["name"], unique = true)]
)
data class FoodItem(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        var name: String,
        var calories: Double,
        var protein: Double,
        var carbs: Double,
        var fat: Double,
    ){




}




