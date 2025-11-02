package com.example.gainscounterapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gainscounterapp.data.local.Converters


@Entity(tableName = "meals")
@TypeConverters(Converters::class)
data class Meal(
    @PrimaryKey val name: String,
    val items: List<FoodItemInput>
){
    fun getTotalCalories(): Double {
        return items.sumOf{it.getCalories()}


    }

    fun getTotalProtein(): Double {
        return items.sumOf { it.getProtein() }
    }

    fun getTotalCarbs(): Double {
        return items.sumOf { it.getCarbs() }
    }

    fun getTotalFat(): Double {
        return items.sumOf { it.getFat() }
    }



}



