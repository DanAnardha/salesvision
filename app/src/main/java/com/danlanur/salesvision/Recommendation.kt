package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun LocalDate?.toApiDateFormat(): String? {
    return this?.toString() // Default to 'YYYY-MM-DD' format
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType")
@Composable
fun RecomAndHistory(viewModel: SalesViewModel = viewModel()) {
    val orderSalesPerDay by viewModel.orderSalesPerDay.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
    val earliestOrder = orderSalesPerDay
        .map { LocalDate.parse(it.Order_Date, formatter) }
        .minOrNull()

    val latestOrder = orderSalesPerDay
        .map { LocalDate.parse(it.Order_Date, formatter) }
        .maxOrNull()

    var predictionResults by remember { mutableStateOf<List<PredictionResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedMenu by remember { mutableStateOf("Prediction") }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    val calendarStartState = rememberSheetState()

    CalendarDialog(
        state = calendarStartState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            maxYear = 2030
        ),
        selection = CalendarSelection.Date { date ->
            Log.d("selectedDate", "$date")
            selectedStartDate = date
        }
    )

    val calendarEndState = rememberSheetState()
    CalendarDialog(
        state = calendarEndState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            maxYear = 2030
        ),
        selection = CalendarSelection.Date { date ->
            Log.d("selectedDate", "$date")
            selectedEndDate = date
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Card utama untuk menampilkan menu Prediction dan History
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box( // Box untuk membungkus seluruh konten Card dan overlay
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.wrapContentWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFEEEEEE),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Prediction Button
                                Text(
                                    text = "Prediction",
                                    modifier = Modifier
                                        .background(
                                            if (selectedMenu == "Prediction") Color(0xFFBDBDBD) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp)
                                        .clickable { selectedMenu = "Prediction" },
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMenu == "Prediction") Color.Black else Color.Gray
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // History Button
                                Text(
                                    text = "Recommendation",
                                    modifier = Modifier
                                        .background(
                                            if (selectedMenu == "Recommendation") Color(0xFFBDBDBD) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp)
                                        .clickable { selectedMenu = "Recommendation" },
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMenu == "Recommendation") Color.Black else Color.Gray
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            when (selectedMenu) {
                                "Prediction" -> {
                                    val isDateValid =
                                        selectedStartDate != null && selectedEndDate != null
                                    val startDateText =
                                        selectedStartDate?.toString() ?: "No date selected"
                                    val endDateText =
                                        selectedEndDate?.toString() ?: "No date selected"

                                    Text(
                                        text = "Forecast Period",
                                        fontSize = 30.sp,
                                        color = BlueJC,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Start Date Section
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(
                                                0xFFEEEEEE
                                            )
                                        ),
                                        elevation = CardDefaults.cardElevation(4.dp) // Elevasi sedikit lebih besar
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Start Date:",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = if (selectedStartDate != null) selectedStartDate.toString() else "No date selected",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (selectedStartDate != null) Color.Gray else Color.Red
                                                )
                                            }
                                            Button(
                                                onClick = { calendarStartState.show() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(
                                                        0xFF4CAF50
                                                    )
                                                ) // Green button for select date
                                            ) {
                                                Text("Select Start Date")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // End Date Section
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(
                                                0xFFEEEEEE
                                            )
                                        ),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "End Date:",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = if (selectedEndDate != null) selectedEndDate.toString() else "No date selected",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (selectedEndDate != null) Color.Gray else Color.Red
                                                )
                                            }
                                            Button(
                                                onClick = { calendarEndState.show() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(
                                                        0xFF4CAF50
                                                    )
                                                ) // Green button for select date
                                            ) {
                                                Text("Select End Date")
                                            }
                                        }
                                    }

                                    // Note Text
                                    Text(
                                        text = "*Note: Prediction is based on sales data from ${earliestOrder} to ${latestOrder}.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                    // Predict Button
                                    Button(
                                        onClick = {
                                            val startDateStr = selectedStartDate.toApiDateFormat()
                                            val endDateStr = selectedEndDate.toApiDateFormat()
                                            if (startDateStr != null && endDateStr != null) {
                                                isLoading = true
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    try {
                                                        val response =
                                                            RetrofitInstance.api.predictSales(
                                                                startDateStr,
                                                                endDateStr
                                                            )
                                                        if (response.isSuccessful) {
                                                            predictionResults =
                                                                response.body() ?: emptyList()
                                                        } else {
                                                            errorMessage =
                                                                "Failed to fetch data: ${response.message()}"
                                                        }
                                                        isLoading = false
                                                    } catch (e: Exception) {
                                                        errorMessage = e.message
                                                        isLoading = false
                                                    }
                                                }
                                            }
                                        },
                                        enabled = isDateValid,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(
                                                0xFF2196F3
                                            )
                                        ) // Blue button for predict
                                    ) {
                                        Text(
                                            text = "Predict",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }


                                "Recommendation" -> {
                                    Text(
                                        text = "Recommendation",
                                        fontSize = 30.sp,
                                        color = BlueJC,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = QuickSand
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    // Display sales history content here
                                }
                            }
                        }
                    }
                }
                // Efek Blur dan CircularProgressIndicator yang muncul saat isLoading true
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                            }
                    ) {
                        // Circular progress indicator
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }


                // Error message atau status lainnya bisa ditampilkan di sini
                if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Card terpisah untuk menampilkan chart setelah prediksi berhasil
        if (selectedMenu == "Prediction" && predictionResults.isNotEmpty()) {
            // Card untuk LineChart terpisah dari card utama
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)

                ) {
                    PredictionLineChart(predictionResults = predictionResults)
                }
            }
        }
    }
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Composable
fun PredictionLineChart(predictionResults: List<PredictionResult>) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Format: "27 Dec"

    // Map prediction results to chart entries
    val entries = predictionResults.mapIndexed { index, result ->
        // Pastikan result.ds dapat di-parse ke Date dengan format yang sesuai
        val date =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault()).parse(result.ds)
        Entry(index.toFloat(), result.yhat.toFloat())
    }

    val currencyFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val formattedValue = "$%.2f".format(value) // Format value as dollar
            return formattedValue
        }
    }

    val maxPrediction = predictionResults.maxOfOrNull { it.yhat } ?: 0.0f
    println("Prediksi Tertinggi: $maxPrediction")
    val minPrediction = predictionResults.minOfOrNull { it.yhat } ?: 0.0f
    println("Prediksi Terendah: $minPrediction")
    val averagePrediction = predictionResults.map { it.yhat }.average()
    println("Rata-rata Prediksi: $averagePrediction")
    val totalPrediction = predictionResults.fold(0.0f) { sum, prediction ->
        sum + prediction.yhat
    }
    println("Total Prediksi: $totalPrediction")


    Text(
        text = "Sales Forecast",
        fontSize = 30.sp,
        color = BlueJC,
        fontWeight = FontWeight.Bold,
        fontFamily = QuickSand
    )
    Spacer(modifier = Modifier.height(16.dp))
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = { context ->
            LineChart(context).apply {
                val dataSet = LineDataSet(entries, "Predicted Sales").apply {
                    color = android.graphics.Color.parseColor("#E69F00")
                    valueTextColor = android.graphics.Color.parseColor("#CC79A7") // Set warna text
                    lineWidth = 2f // Set ketebalan garis
                    setDrawValues(true)  // Enable drawing values on each point
                    valueTextSize = 10f  // Set the size of the value text
                    setValueTextColor(android.graphics.Color.BLACK) // Jika ingin menampilkan nilai pada titik
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    valueFormatter = currencyFormatter
                }

                val lineData = LineData(dataSet)
                this.data = lineData
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setLabelCount(predictionResults.size, true)
                xAxis.setLabelCount(4, true)  // Show 5 labels by default
                xAxis.setGranularity(1f)
                xAxis.valueFormatter = IndexAxisValueFormatter(
                    predictionResults.map {
                        val date = SimpleDateFormat(
                            "EEE, dd MMM yyyy HH:mm:ss z",
                            Locale.getDefault()
                        ).parse(it.ds)
                        dateFormat.format(date)
                    }
                )

                legend.isEnabled = false
                this.animateY(1000, Easing.EaseInOutQuart)
                this.isDragEnabled = true
                this.setScaleEnabled(true)
                this.setPinchZoom(true)
                invalidate() // Memastikan chart di-refresh dengan data terbaru
            }
        }
    )
    Spacer(modifier = Modifier.height(32.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Sales Prediction Statistics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Divider for separation
        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Highest Prediction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Highest Prediction:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(maxPrediction),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        // Divider for separation
        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Lowest Prediction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lowest Prediction:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(minPrediction),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        // Divider for separation
        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Average Prediction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Average Prediction:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(averagePrediction.toFloat()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        // Divider for separation
        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Total Prediction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Prediction:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(totalPrediction),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Recommendation(viewModel: SalesViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                RecomAndHistory()
            }
        }
    }
}