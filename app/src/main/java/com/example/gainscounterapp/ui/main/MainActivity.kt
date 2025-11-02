package com.example.gainscounterapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gainscounterapp.R
import com.example.gainscounterapp.ui.addfood.AddFoodActivity
import com.example.gainscounterapp.ui.calculate.CalculateFood
import com.example.gainscounterapp.ui.dailylog.DailyLogActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addButton = findViewById<Button>(R.id.openAddFoodButton)
        addButton.setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }

        val calcButton = findViewById<Button>(R.id.openCalcButton)
        calcButton.setOnClickListener {
            startActivity(Intent(this, CalculateFood::class.java))
        }

        val viewDailyLogButton = findViewById<Button>(R.id.viewDailyLogButton)
        viewDailyLogButton.setOnClickListener {
            startActivity(Intent(this, DailyLogActivity::class.java))
        }

        val viewGraphButton = findViewById<Button>(R.id.viewGraphButton)
        viewGraphButton.setOnClickListener {
            startActivity(Intent(this, GraphActivity::class.java))
        }
    }
}
