package com.example.gainscounterapp.model

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gainscounterapp.data.local.Converters

@Entity(tableName = "daily_logs")
@TypeConverters(Converters::class)
data class DailyLog(
        @PrimaryKey val date: String, // "2025-10-28"
)
//        val meals: List<Meal>,
//        val foodInputs: List<FoodItemInput> // direct food entries
//    ){
//    fun getDailyCalories(): Double{
//        return meals.sumOf { it.getTotalCalories() } + foodInputs.sumOf { it.getCalories() }
//    }
//
//    fun getDailyProtein(): Double{
//        return meals.sumOf { it.getTotalProtein() } + foodInputs.sumOf { it.getProtein() }
//    }
//
//    fun getDailyCarbs(): Double{
//        return meals.sumOf { it.getTotalCarbs() } + foodInputs.sumOf { it.getCarbs() }
//    }
//
//    fun getDailyFat(): Double{
//        return meals.sumOf { it.getTotalFat() } + foodInputs.sumOf { it.getFat() }
//    }
//
////    fun addMeal(meal: Meal) {
////        meals.add(meal)
////    }
//
//    fun printSummary(){
//        println("📅 ${date}")
//        for (meal in meals){
//            println(" - ${meal.name}: ${meal.getTotalCalories()} kcal and ${meal.getTotalProtein()}g protein")
//
//        }
//        println("Total: ${getDailyCalories()} kcal")
//        println("Total: ${getDailyProtein()}g protein")
//
//    }
//}
