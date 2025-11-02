package com.example.gainscounterapp.ui.addfood

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.FoodDatabase
import com.example.gainscounterapp.model.FoodItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFoodActivity : AppCompatActivity() {

    private lateinit var db: FoodDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        db = Room.databaseBuilder(
            applicationContext,
            FoodDatabase::class.java,
            "food_database"
        ).build()

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val caloriesInput = findViewById<EditText>(R.id.caloriesInput)
        val proteinInput = findViewById<EditText>(R.id.proteinInput)
        val carbsInput = findViewById<EditText>(R.id.carbsInput)
        val fatInput = findViewById<EditText>(R.id.fatInput)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val calories = caloriesInput.text.toString().toDoubleOrNull()
            val protein = proteinInput.text.toString().toDoubleOrNull()
            val carbs = carbsInput.text.toString().toDoubleOrNull()
            val fat = fatInput.text.toString().toDoubleOrNull()

            if (name.isBlank() || calories == null || protein == null || carbs == null || fat == null) {
                statusText.text = "Please fill in all fields correctly."
                return@setOnClickListener
            }

            val food = FoodItem(
                name = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.foodItemDao().insertFood(food)
                runOnUiThread {
                    statusText.text = "Saved '${food.name}' ✅"
                    nameInput.text.clear()
                    caloriesInput.text.clear()
                    proteinInput.text.clear()
                    carbsInput.text.clear()
                    fatInput.text.clear()
                }
            }
        }
    }
}