package com.example.gainscounterapp.data.local

import androidx.room.*
import com.example.gainscounterapp.model.Meal

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE name = :name LIMIT 1")
    suspend fun getMealByName(name: String): Meal?
    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<Meal>
}

