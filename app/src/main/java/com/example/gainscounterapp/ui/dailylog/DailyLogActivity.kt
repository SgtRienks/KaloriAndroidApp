package com.example.gainscounterapp.ui.dailylog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.DailyLogDao
import com.example.gainscounterapp.data.local.FoodDatabase
import com.example.gainscounterapp.data.local.FoodItemDao
import com.example.gainscounterapp.model.DailyFoodEntry
import com.example.gainscounterapp.model.DailyLog
import com.example.gainscounterapp.model.FoodItem
import com.example.gainscounterapp.ui.addfood.AddFoodToDayActivity
import com.example.gainscounterapp.ui.addfood.SelectMealActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DailyLogActivity : AppCompatActivity() {

    private lateinit var foodDao: FoodItemDao
    private lateinit var logDao: DailyLogDao
    private lateinit var dailyItemsContainer: LinearLayout
    private lateinit var dayTitle: TextView
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var totalProteinTextView: TextView
    private lateinit var totalCarbsTextView: TextView
    private lateinit var totalFatTextView: TextView
    private lateinit var addFoodBtn: Button
    private lateinit var addMealBtn: Button
    private lateinit var saveDayBtn: Button

    private var currentDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_log)

        val db = FoodDatabase.getInstance(applicationContext)
        foodDao = db.foodItemDao()
        logDao = db.dailyLogDao()

        // Initialize Views
        dailyItemsContainer = findViewById(R.id.dailyItemsContainer)
        dayTitle = findViewById(R.id.dayTitle)
        totalCaloriesTextView = findViewById(R.id.totalCalories)
        totalProteinTextView = findViewById(R.id.totalProtein)
        totalCarbsTextView = findViewById(R.id.totalCarbs)
        totalFatTextView = findViewById(R.id.totalFat)
        addFoodBtn = findViewById(R.id.addFoodButton)
        addMealBtn = findViewById(R.id.addMealButton)
        saveDayBtn = findViewById(R.id.saveDayButton)

        setupListeners()
    }

    private fun setupListeners() {
        dayTitle.setOnClickListener {
            showDatePicker()
        }

        addFoodBtn.setOnClickListener {
            val intent = Intent(this, AddFoodToDayActivity::class.java)
            intent.putExtra("SELECTED_DATE", currentDate.toString())
            startActivity(intent)
        }

        addMealBtn.setOnClickListener {
            val intent = Intent(this, SelectMealActivity::class.java)
            intent.putExtra("SELECTED_DATE", currentDate.toString())
            startActivity(intent)
        }

        saveDayBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val dateString = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                logDao.saveDailyLog(DailyLog(date = dateString))
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DailyLogActivity, "Day $dateString saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = java.sql.Date.valueOf(currentDate.toString())

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                currentDate = LocalDate.of(year, month + 1, dayOfMonth)
                loadDay(currentDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadDay(date: LocalDate) {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        lifecycleScope.launch(Dispatchers.IO) {
            val entries = logDao.getFoodsForDay(dateString)
            val foodsWithEntries = entries.mapNotNull { entry ->
                foodDao.getFoodById(entry.foodId)?.let { food ->
                    Pair(food, entry)
                }
            }

            val totalCalories = foodsWithEntries.sumOf { (food, entry) -> (food.calories * entry.grams) / 100.0 }
            val totalProtein = foodsWithEntries.sumOf { (food, entry) -> (food.protein * entry.grams) / 100.0 }
            val totalCarbs = foodsWithEntries.sumOf { (food, entry) -> (food.carbs * entry.grams) / 100.0 }
            val totalFat = foodsWithEntries.sumOf { (food, entry) -> (food.fat * entry.grams) / 100.0 }

            withContext(Dispatchers.Main) {
                dayTitle.text = "Date: $dateString"
                totalCaloriesTextView.text = "Calories: %.1f kcal".format(totalCalories)
                totalProteinTextView.text = "Protein: %.1fg".format(totalProtein)
                totalCarbsTextView.text = "Carbs: %.1fg".format(totalCarbs)
                totalFatTextView.text = "Fat: %.1fg".format(totalFat)

                dailyItemsContainer.removeAllViews()
                foodsWithEntries.forEach { (food, entry) ->
                    val textView = TextView(this@DailyLogActivity)
                    val calculatedCalories = (food.calories * entry.grams) / 100.0
                    textView.text = "${food.name} - ${entry.grams.toInt()}g  (${ "%.1f".format(calculatedCalories)} kcal)"
                    textView.setOnClickListener { showDeleteConfirmationDialog(entry) }
                    dailyItemsContainer.addView(textView)
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(entry: DailyFoodEntry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    logDao.deleteFoodEntry(entry)
                    withContext(Dispatchers.Main) {
                        loadDay(currentDate)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        val dateString = intent.getStringExtra("SELECTED_DATE")
        currentDate = if (dateString != null) LocalDate.parse(dateString) else LocalDate.now()
        loadDay(currentDate)
    }
}
