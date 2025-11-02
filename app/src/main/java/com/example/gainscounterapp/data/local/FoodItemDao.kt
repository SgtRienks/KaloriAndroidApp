package com.example.gainscounterapp.data.local

import androidx.room.*
import com.example.gainscounterapp.model.FoodItem // Pass på at denne stien er riktig


@Dao
interface FoodItemDao {

    /**
     * Setter inn en ny FoodItem. Hvis en matvare med samme unik ID/navn
     * (som forårsaket feilen) allerede eksisterer, erstattes den gamle.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItem)

    @Query("SELECT * FROM food_items")
    suspend fun getAllFoods(): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE id = :foodId LIMIT 1")
    suspend fun getFoodById(foodId: Int): FoodItem?

    @Delete
    suspend fun deleteFood(food: FoodItem)

    @Query("SELECT * FROM food_items WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): FoodItem?

}