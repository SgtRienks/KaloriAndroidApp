package com.example.gainscounterapp.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gainscounterapp.R
import com.example.gainscounterapp.data.local.FoodDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val db = FoodDatabase.getInstance(applicationContext)
        val logDao = db.dailyLogDao()
        val foodDao = db.foodItemDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val allLogs = logDao.getAllLogs().sortedBy { it.date } // Sort chronologically
            val entries = mutableListOf<Entry>()
            val labels = mutableListOf<String>()
            val fullData = mutableListOf<String>()

            allLogs.forEachIndexed { index, log ->
                val entriesForDay = logDao.getFoodsForDay(log.date)
                val totalCalories = entriesForDay.sumOf { entry ->
                    val food = foodDao.getFoodById(entry.foodId)
                    (food?.calories ?: 0.0) * entry.grams / 100.0
                }
                val totalProtein = entriesForDay.sumOf { entry ->
                    val food = foodDao.getFoodById(entry.foodId)
                    (food?.protein ?: 0.0) * entry.grams / 100.0
                }
                val totalCarbs = entriesForDay.sumOf { entry ->
                    val food = foodDao.getFoodById(entry.foodId)
                    (food?.carbs ?: 0.0) * entry.grams / 100.0
                }
                val totalFat = entriesForDay.sumOf { entry ->
                    val food = foodDao.getFoodById(entry.foodId)
                    (food?.fat ?: 0.0) * entry.grams / 100.0
                }

                entries.add(Entry(index.toFloat(), totalCalories.toFloat()))
                labels.add(LocalDate.parse(log.date).format(DateTimeFormatter.ofPattern("dd/MM")))
                fullData.add("Date: ${log.date}\nCalories: %.0f\nProtein: %.0fg\nCarbs: %.0fg\nFat: %.0fg".format(totalCalories, totalProtein, totalCarbs, totalFat))
            }

            withContext(Dispatchers.Main) {
                val dataSet = LineDataSet(entries, "Calories")
                dataSet.valueTextColor = Color.WHITE // Color for the numbers on the chart

                val lineData = LineData(dataSet)
                lineChart.data = lineData

                // --- Styling ---
                lineChart.description.isEnabled = false
                lineChart.legend.textColor = Color.WHITE // Color for the "Calories" legend
                lineChart.setExtraBottomOffset(30f) // Add padding at the bottom

                // X-Axis (Dates)
                val xAxis = lineChart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.labelRotationAngle = -45f
                xAxis.textColor = Color.WHITE // Color for the date labels

                // Y-Axis (Calories)
                val yAxisLeft = lineChart.axisLeft
                yAxisLeft.axisMinimum = 0f
                yAxisLeft.textColor = Color.WHITE // Color for the calorie numbers
                lineChart.axisRight.isEnabled = false

                // Set marker view
                val markerView = CustomMarkerView(this@GraphActivity, R.layout.marker_view, fullData)
                lineChart.marker = markerView

                lineChart.invalidate() // Refresh chart
            }
        }
    }
}

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val data: List<String>
) : MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.markerText)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            if (it.x.toInt() < data.size) {
                textView.text = data[it.x.toInt()]
            }
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
