package com.example.gainscounterapp.model

import kotlin.div
import kotlin.times

data class FoodItemInput(
    var foodItem: FoodItem,
    var weight: Double,
    ){

    fun getCalories(): Double{
        return foodItem.calories * weight / 100
    }

    fun getProtein(): Double{
        return foodItem.protein * weight / 100
    }

    fun getCarbs(): Double{
        return foodItem.carbs * weight / 100
    }

    fun getFat(): Double{
        return foodItem.fat * weight / 100
    }


}

