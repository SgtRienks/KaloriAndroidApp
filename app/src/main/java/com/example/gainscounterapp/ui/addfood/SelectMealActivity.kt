package com.example.gainscounterapp.ui.addfood

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.FoodDatabase
import com.example.gainscounterapp.model.DailyFoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class SelectMealActivity : AppCompatActivity() {

    private lateinit var mealsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_meal)

        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()

        val db = FoodDatabase.getInstance(applicationContext)
        val mealDao = db.mealDao()
        val logDao = db.dailyLogDao()

        mealsListView = findViewById(R.id.mealsListView)

        lifecycleScope.launch(Dispatchers.IO) {
            val meals = mealDao.getAllMeals()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@SelectMealActivity, android.R.layout.simple_list_item_1, meals.map { it.name })
                mealsListView.adapter = adapter

                mealsListView.setOnItemClickListener { _, _, position, _ ->
                    val selectedMeal = meals[position]
                    lifecycleScope.launch(Dispatchers.IO) {
                        selectedMeal.items.forEach { foodItemInput ->
                            logDao.insertFoodEntry(DailyFoodEntry(date = selectedDate, foodId = foodItemInput.foodItem.id, grams = foodItemInput.weight.toFloat()))
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SelectMealActivity, "Meal '${selectedMeal.name}' added to log for $selectedDate!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }
}
