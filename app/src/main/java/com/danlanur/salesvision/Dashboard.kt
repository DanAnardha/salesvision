package com.danlanur.salesvision

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainHightlight(salesByMonth: List<SalesByMonth>) {
    val totalProfit = salesByMonth.sumOf { it.Total_Profit.toInt() }
    val totalSales = salesByMonth.sumOf { it.Total_Sales.toInt() }
    val totalQuantity = salesByMonth.sumOf { it.Total_Quantity.toInt() }

    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault()) // Format: "Oct 2024"

    val currencyFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val formattedValue = "$%.2f".format(value) // Format value as dollar
            return formattedValue
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // SALES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            val entriesSales = salesByMonth.mapIndexed { index, result ->
                val date =
                    SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month) // Parse "2024-10"
                val formattedDate = date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                Entry(index.toFloat(), result.Total_Sales.toFloat())
            }
            AndroidView(
                modifier = Modifier.height(150.dp),
                factory = { context ->
                    LineChart(context).apply {
                        val dataSet = LineDataSet(entriesSales, "Predicted Sales").apply {
                            color = android.graphics.Color.parseColor("#E69F00")
//                            valueTextColor =
                                android.graphics.Color.parseColor("#CC79A7") // Set warna text
                            lineWidth = 2f // Set ketebalan garis
                            setDrawValues(true)  // Enable drawing values on each point
//                            valueTextSize = 10f  // Set the size of the value text
                            setValueTextColor(android.graphics.Color.BLACK) // Jika ingin menampilkan nilai pada titik
                            mode = LineDataSet.Mode.CUBIC_BEZIER
//                            valueFormatter = currencyFormatter
                        }

                        val lineData = LineData(dataSet)
                        this.data = lineData
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setLabelCount(predictionResults.size, true)
                        xAxis.setLabelCount(5, true)  // Show 5 labels by default
                        xAxis.setGranularity(1f)
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            salesByMonth.map {
                                val date = SimpleDateFormat(
                                    "yyyy-MM",
                                    Locale.getDefault()
                                ).parse(it.Month)
                                dateFormat.format(date)
                            }
                        )
                        axisLeft.isEnabled = false
                        axisRight.isEnabled = false
                        xAxis.isEnabled = false
                        description.isEnabled = false
                        legend.isEnabled = false
                        this.animateY(1000, Easing.EaseInOutQuart)
                        this.isDragEnabled = true
                        this.setScaleEnabled(true)
                        this.setPinchZoom(true)
                        invalidate() // Memastikan chart di-refresh dengan data terbaru
                    }
                }
            )
        }

        // SALES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            val entriesProfit = salesByMonth.mapIndexed { index, result ->
                val date =
                    SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month) // Parse "2024-10"
                val formattedDate = date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                Entry(index.toFloat(), result.Total_Profit.toFloat())
            }
            AndroidView(
                modifier = Modifier.height(150.dp),
                factory = { context ->
                    LineChart(context).apply {
                        val dataSet = LineDataSet(entriesProfit, "Predicted Sales").apply {
                            color = android.graphics.Color.parseColor("#E69F00")
//                            valueTextColor =
                            android.graphics.Color.parseColor("#CC79A7") // Set warna text
                            lineWidth = 2f // Set ketebalan garis
                            setDrawValues(true)  // Enable drawing values on each point
//                            valueTextSize = 10f  // Set the size of the value text
                            setValueTextColor(android.graphics.Color.BLACK) // Jika ingin menampilkan nilai pada titik
                            mode = LineDataSet.Mode.CUBIC_BEZIER
//                            valueFormatter = currencyFormatter
                        }

                        val lineData = LineData(dataSet)
                        this.data = lineData
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setLabelCount(predictionResults.size, true)
                        xAxis.setLabelCount(5, true)  // Show 5 labels by default
                        xAxis.setGranularity(1f)
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            salesByMonth.map {
                                val date = SimpleDateFormat(
                                    "yyyy-MM",
                                    Locale.getDefault()
                                ).parse(it.Month)
                                dateFormat.format(date)
                            }
                        )
                        axisLeft.isEnabled = false
                        axisRight.isEnabled = false
                        xAxis.isEnabled = false
                        description.isEnabled = false
                        legend.isEnabled = false
                        this.animateY(1000, Easing.EaseInOutQuart)
                        this.isDragEnabled = true
                        this.setScaleEnabled(true)
                        this.setPinchZoom(true)
                        invalidate() // Memastikan chart di-refresh dengan data terbaru
                    }
                }
            )
        }

        // SALES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            val entriesQuantity = salesByMonth.mapIndexed { index, result ->
                val date =
                    SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month) // Parse "2024-10"
                val formattedDate = date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                Entry(index.toFloat(), result.Total_Quantity.toFloat())
            }
            AndroidView(
                modifier = Modifier.height(150.dp),
                factory = { context ->
                    LineChart(context).apply {
                        val dataSet = LineDataSet(entriesQuantity, "Predicted Sales").apply {
                            color = android.graphics.Color.parseColor("#E69F00")
//                            valueTextColor =
                            android.graphics.Color.parseColor("#CC79A7") // Set warna text
                            lineWidth = 2f // Set ketebalan garis
                            setDrawValues(true)  // Enable drawing values on each point
//                            valueTextSize = 10f  // Set the size of the value text
                            setValueTextColor(android.graphics.Color.BLACK) // Jika ingin menampilkan nilai pada titik
                            mode = LineDataSet.Mode.CUBIC_BEZIER
//                            valueFormatter = currencyFormatter
                        }

                        val lineData = LineData(dataSet)
                        this.data = lineData
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setLabelCount(predictionResults.size, true)
                        xAxis.setLabelCount(5, true)  // Show 5 labels by default
                        xAxis.setGranularity(1f)
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            salesByMonth.map {
                                val date = SimpleDateFormat(
                                    "yyyy-MM",
                                    Locale.getDefault()
                                ).parse(it.Month)
                                dateFormat.format(date)
                            }
                        )
                        axisLeft.isEnabled = false
                        axisRight.isEnabled = false
                        xAxis.isEnabled = false
                        description.isEnabled = false
                        legend.isEnabled = false
                        this.animateY(1000, Easing.EaseInOutQuart)
                        this.isDragEnabled = true
                        this.setScaleEnabled(true)
                        this.setPinchZoom(true)
                        invalidate() // Memastikan chart di-refresh dengan data terbaru
                    }
                }
            )
        }
    }
}

@Composable
fun Dashboard(viewModel: SalesViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val salesByMonth by viewModel.salesByMonth.collectAsState()
        MainHightlight(salesByMonth)
    }
}