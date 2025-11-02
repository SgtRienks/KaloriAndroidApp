package com.example.gainscounterapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gainscounterapp.model.DailyFoodEntry
import com.example.gainscounterapp.model.DailyLog
import com.example.gainscounterapp.model.FoodItem
import com.example.gainscounterapp.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(entities = [FoodItem::class, Meal::class, DailyLog::class, DailyFoodEntry::class], version = 4)
@TypeConverters(Converters::class)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun mealDao(): MealDao
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getInstance(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(FoodDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class FoodDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulateDatabase(context, database.foodItemDao())
                }
            }
        }

        private suspend fun prePopulateDatabase(context: Context, foodItemDao: FoodItemDao) {
            try {
                val inputStream = context.assets.open("appmatvaredata.txt")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = reader.readLines()
                reader.close()

                for (line in lines.drop(1)) { // Iterate over the list, skipping the header
                    val tokens = line.split(',')
                    // Format from file: navn,protein,kalorier,karbohydrater,fett
                    if (tokens.size == 5) {
                        val foodItem = FoodItem(
                            name = tokens[0],
                            protein = tokens[1].toDoubleOrNull() ?: 0.0,
                            calories = tokens[2].toDoubleOrNull() ?: 0.0,
                            carbs = tokens[3].toDoubleOrNull() ?: 0.0,
                            fat = tokens[4].toDoubleOrNull() ?: 0.0
                        )
                        foodItemDao.insertFood(foodItem)
                    }
                }
            } catch (e: Exception) {
                // Log the exception, e.g., using Log.e("PrepopulateDB", "Error reading file", e)
                e.printStackTrace()
            }
        }
    }
}
