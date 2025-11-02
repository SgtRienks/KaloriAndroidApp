package com.example.gainscounterapp.data.local

import androidx.room.TypeConverter
import com.example.gainscounterapp.model.FoodItemInput
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromFoodItemInputList(value: List<FoodItemInput>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFoodItemInputList(value: String): List<FoodItemInput> {
        val listType = object : TypeToken<List<FoodItemInput>>() {}.type
        return gson.fromJson(value, listType)
    }
}
