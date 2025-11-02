package com.example.gainscounterapp.ui.addfood

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.FoodDatabase
import com.example.gainscounterapp.model.DailyFoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AddFoodToDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_to_day)

        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: LocalDate.now().toString()

        val db = FoodDatabase.getInstance(applicationContext)
        val foodDao = db.foodItemDao()
        val logDao = db.dailyLogDao()

        val searchInput = findViewById<AutoCompleteTextView>(R.id.searchFoodInput)
        val gramsInput = findViewById<EditText>(R.id.gramsInput)
        val addBtn = findViewById<Button>(R.id.addFoodToDayBtn)

        lifecycleScope.launch(Dispatchers.IO) {
            val foodNames = foodDao.getAllFoods().map { it.name }
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AddFoodToDayActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    foodNames
                )
                searchInput.setAdapter(adapter)
            }
        }

        addBtn.setOnClickListener {
            val name = searchInput.text.toString()
            val grams = gramsInput.text.toString().toFloatOrNull()

            if (name.isBlank() || grams == null) {
                Toast.makeText(this, "Enter food & grams", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val food = foodDao.findByName(name)
                if (food == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddFoodToDayActivity, "Food not found", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                logDao.insertFoodEntry(DailyFoodEntry(date = selectedDate, foodId = food.id, grams = grams))

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddFoodToDayActivity, "Added!", Toast.LENGTH_SHORT).show()
                    finish() // back to DailyLog
                }
            }
        }
    }
}
