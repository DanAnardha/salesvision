package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.isDigitsOnly
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
    val orderDate by viewModel.orderDates.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")

    val earliestOrderString = orderDate.firstOrNull()?.earliest_order
    val latestOrderString = orderDate.firstOrNull()?.latest_order

    val earliestOrder = earliestOrderString?.let { LocalDate.parse(it, formatter) }
    val latestOrder = latestOrderString?.let { LocalDate.parse(it, formatter) }


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
            .padding(4.dp)
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
                                    Spacer(modifier = Modifier.height(24.dp))
                                    // Display sales history content here
                                    RecommendationForm()
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
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Composable
fun PredictionLineChart(predictionResults: List<PredictionResult>) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Format: "27 Dec"

    val entries = predictionResults.mapIndexed { index, result ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationForm() {
    var qty by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var freightPrice by remember { mutableStateOf("") }
    var comp1 by remember { mutableStateOf("") }
    var productScore by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    fun String.isValidDecimal(): Boolean {
        return matches(Regex("^\\d*\\.?\\d*$"))
    }

    var recomResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    fun onSubmit(recomResult: String) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                isLoading = false
                isDialogVisible = true // Menampilkan dialog setelah hasil tersedia
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = "Price recommendation based on competitor pricing and market trends.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        // Quantity
        var err_qty = remember { mutableStateOf("") }
        Text(
            text = "Quantity",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Text(
            text = "Enter quantity of items.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            value = qty,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEEEEEE),
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.isValidDecimal()) {
                    qty = it
                    err_qty.value = ""
                } else {
                    err_qty.value = "Please enter a valid quantity."
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Competitor Price Icon"
                )
            },
            trailingIcon = {
                if (qty.isNotEmpty()) {
                    IconButton(onClick = { qty = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        if (err_qty.value.isNotEmpty()) {
            Text(
                text = err_qty.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        // Unit Price
        var err_up = remember { mutableStateOf("") }
        Text(
            text = "Unit Price",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Text(
            text = "Enter unit price of product.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            value = unitPrice,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEEEEEE),
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.isValidDecimal()) {
                    unitPrice = it
                    err_up.value = ""
                } else {
                    err_up.value = "Please enter a valid price."
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Competitor Price Icon"
                )
            },
            trailingIcon = {
                if (unitPrice.isNotEmpty()) {
                    IconButton(onClick = { unitPrice = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        if (err_up.value.isNotEmpty()) {
            Text(
                text = err_up.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        // Freight Price
        var err_fp = remember { mutableStateOf("") }
        Text(
            text = "Freight Price",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Text(
            text = "Enter shipping cost.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            value = freightPrice,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEEEEEE),
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.isValidDecimal()) {
                    freightPrice = it
                    err_fp.value = ""
                } else {
                    err_fp.value = "Please enter a valid price."
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Competitor Price Icon"
                )
            },
            trailingIcon = {
                if (freightPrice.isNotEmpty()) {
                    IconButton(onClick = { freightPrice = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        if (err_fp.value.isNotEmpty()) {
            Text(
                text = err_fp.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        // Product Score
        var err_ps = remember { mutableStateOf("") }
        Text(
            text = "Product Score (0 - 5)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Text(
            text = "Enter product rating.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            value = productScore,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEEEEEE),
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.isValidDecimal()) {
                    productScore = it
                    err_ps.value = ""
                } else {
                    err_ps.value = "Please enter a valid rating."
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Competitor Price Icon"
                )
            },
            trailingIcon = {
                if (productScore.isNotEmpty()) {
                    IconButton(onClick = { productScore = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        if (err_ps.value.isNotEmpty()) {
            Text(
                text = err_ps.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Divider for separation
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        // Title
        var err_cp = remember { mutableStateOf("") }
        Text(
            text = "Competitor Price",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Black
        )

        // Description/Notes for the TextField (Optional)
        Text(
            text = "Enter the price of the competitor's product.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray
        )

        // TextField with Shadow and Rounded Corners
        TextField(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            value = comp1,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEEEEEE),
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.isValidDecimal()) {
                    comp1 = it
                    err_cp.value = ""
                } else {
                    err_cp.value = "Please enter a valid price."
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Competitor Price Icon"
                )
            },
            trailingIcon = {
                if (comp1.isNotEmpty()) {
                    IconButton(onClick = { comp1 = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )

        if (err_cp.value.isNotEmpty()) {
            Text(
                text = err_cp.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Divider for separation
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )

        Text(
            text = "Make sure to enter the correct value to ensure accurate predictions.",
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.Gray
        )

        // Another Divider for better structure and separation
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
    }

    val isFormValid =
        qty.isNotEmpty() && unitPrice.isNotEmpty() && freightPrice.isNotEmpty() && productScore.isNotEmpty() && comp1.isNotEmpty()
    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = {
            if (isFormValid) {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.api.predictRecom(
                            qty.toFloat(),
                            unitPrice.toFloat(),
                            freightPrice.toFloat(),
                            comp1.toFloat(),
                            productScore.toFloat()
                        )

                        if (response.isSuccessful) {
                            val predictionResponse = response.body()
                            if (predictionResponse != null && predictionResponse.prediction.isNotEmpty()) {
                                recomResult =
                                    predictionResponse.prediction.toString() // Atur hasil sesuai
                                onSubmit(recomResult!!)
                            } else {
                                recomResult = "No predictions available"
                            }
                            Log.d("Result = ", recomResult!!) // Log hasil setelah diupdate
                        } else {
                            errorMessage = "Failed to fetch data: ${response.message()}"
                        }

                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Unknown error"
                    } finally {
                        isLoading = false
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
        enabled = isFormValid && !isLoading,
    ) {
        Text(
            text = "Submit",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
    // Dialog Alert
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications, // Icon yang relevan
                        contentDescription = "Price Icon",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Price Recommendation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "The recommended price based on competitor pricing and market trends is:",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                        val formattedPrice = recomResult?.let {
                            val cleanedValue = it.replace("[", "").replace("]", "")
                            cleanedValue.toDoubleOrNull()?.let { value ->
                                "$${"%.2f".format(value)}"
                            } ?: "$0.00"
                        }
                    if (formattedPrice != null) {
                        Text(
                            text = formattedPrice,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { isDialogVisible = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("OK", color = Color.White)
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        )
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