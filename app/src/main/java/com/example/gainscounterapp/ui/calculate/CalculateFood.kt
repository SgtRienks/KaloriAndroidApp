package com.example.gainscounterapp.ui.calculate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.FoodDatabase
import com.example.gainscounterapp.model.FoodItemInput
import com.example.gainscounterapp.model.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalculateFood : AppCompatActivity() {

    private val addedFoods = mutableListOf<FoodItemInput>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_food)

        // Database
        val db = FoodDatabase.getInstance(applicationContext)
        val foodDao = db.foodItemDao()
        val mealDao = db.mealDao()

        // UI references
        val nameInput = findViewById<AutoCompleteTextView>(R.id.nameCalcInput)
        val weightInput = findViewById<EditText>(R.id.weightCalcInput)
        val addFoodButton = findViewById<Button>(R.id.addFoodButton)
        val saveMealButton = findViewById<Button>(R.id.saveMealButton)
        val resultText = findViewById<TextView>(R.id.resultText)
        val totalText = findViewById<TextView>(R.id.totalText)

        // Setup autocomplete for the food name input
        lifecycleScope.launch(Dispatchers.IO) {
            val foodNames = foodDao.getAllFoods().map { it.name }
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@CalculateFood,
                    android.R.layout.simple_dropdown_item_1line,
                    foodNames
                )
                nameInput.setAdapter(adapter)
            }
        }

        var totalCalories = 0.0
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0

        // ➕ Add food to current meal
        addFoodButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val weight = weightInput.text.toString().toDoubleOrNull()

            if (name.isEmpty() || weight == null) {
                Toast.makeText(this, "Enter valid name and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val food = foodDao.findByName(name)
                if (food != null) {
                    val input = FoodItemInput(food, weight)
                    addedFoods.add(input)

                    totalCalories += (food.calories * weight) / 100
                    totalProtein += (food.protein * weight) / 100
                    totalCarbs += (food.carbs * weight) / 100
                    totalFat += (food.fat * weight) / 100

                    withContext(Dispatchers.Main) {
                        resultText.text = buildString {
                            append("Added foods:\n")
                            addedFoods.forEach {
                                append("- ${it.foodItem.name} (${it.weight}g)\n")
                            }
                        }
                        totalText.text =
                            "Totals: %.1f kcal, %.1fg protein, %.1fg carbs, %.1fg fat".format(
                                totalCalories, totalProtein, totalCarbs, totalFat
                            )
                        nameInput.text.clear()
                        weightInput.text.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CalculateFood, "Food not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 💾 Save entire meal
        saveMealButton.setOnClickListener {
            if (addedFoods.isEmpty()) {
                Toast.makeText(this, "No foods added", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val input = EditText(this)
            input.hint = "Meal name"

            AlertDialog.Builder(this)
                .setTitle("Save Meal")
                .setMessage("Enter a name for this meal:")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val mealName = input.text.toString().trim()

                    if (mealName.isEmpty()) {
                        Toast.makeText(this, "Meal name required", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                        val existing = mealDao.getMealByName(mealName)
                        if (existing != null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CalculateFood, "A meal with this name already exists", Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }

                        val meal = Meal(name = mealName, items = addedFoods.toList())
                        mealDao.insertMeal(meal)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CalculateFood, "Meal saved!", Toast.LENGTH_SHORT).show()

                            addedFoods.clear()
                            resultText.text = "Added foods:\n"
                            totalText.text = "Totals: 0 kcal, 0g protein, 0g carbs, 0g fat"
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

    }
}
