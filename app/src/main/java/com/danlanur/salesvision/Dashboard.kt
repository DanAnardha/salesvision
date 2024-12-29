package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.sunnychung.lib.android.composabletable.ux.Table
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainHightlight(salesByMonth: List<SalesByMonth>) {
    val totalProfit = salesByMonth.sumOf { it.Total_Profit.toInt() }
    val totalSales = salesByMonth.sumOf { it.Total_Sales.toInt() }
    val totalQuantity = salesByMonth.sumOf { it.Total_Quantity.toInt() }
    val totalOrder = salesByMonth.sumOf { it.Total_Order.toInt() }
    val totalCustomer = salesByMonth.sumOf { it.Total_Customer.toInt() }

    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault()) // Format: "Oct 2024"
    Spacer(modifier = Modifier.height(60.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp), // Padding di kiri dan kanan baris
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spasi antar-kartu
        ) {

            // SALES
            Card(
                modifier = Modifier
                    .weight(1f) // Membagi lebar secara proporsional
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label "Sales"
                        Text(
                            text = "SALES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueJC
                        )

                        // Total Sales dengan frame abu-abu
                        val formatter = DecimalFormat("#,###")
                        val formattedSales = "$" + formatter.format(totalSales)

                        Text(
                            text = "$formattedSales",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.Gray,
                        thickness = 1.dp
                    )

                    // Chart bagian bawah
                    val entriesSales = salesByMonth.mapIndexed { index, result ->
                        val date =
                            SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month)
                        val formattedDate = date?.let { dateFormat.format(it) }
                        Entry(index.toFloat(), result.Total_Sales.toFloat())
                    }

                    AndroidView(
                        modifier = Modifier.height(50.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                val dataSet = LineDataSet(entriesSales, "Predicted Sales").apply {
                                    color = android.graphics.Color.parseColor("#E69F00")
                                    lineWidth = 2f
                                    setDrawValues(false)
                                    setDrawCircles(false)
                                    setValueTextColor(android.graphics.Color.BLACK)
                                    mode = LineDataSet.Mode.CUBIC_BEZIER
                                }

                                val lineData = LineData(dataSet)
                                this.data = lineData
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                xAxis.setLabelCount(5, true)
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
                                invalidate()

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        e?.let {
                                            val index = it.x.toInt() // Index of the entry
                                            val month =
                                                salesByMonth[index].Month // Access the month
                                            val sales = salesByMonth[index].Total_Sales
                                            val formatter = DecimalFormat("#,###")
                                            val formattedSales = "$" + formatter.format(sales)
                                            val originalFormat =
                                                SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat =
                                                SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                            val formattedMonth =
                                                originalFormat.parse(month)?.let { date ->
                                                    targetFormat.format(date)
                                                } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth Sales: $formattedSales",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected() {
                                        // No action required when nothing is selected
                                    }
                                })
                            }
                        }
                    )
                }
            }

            // PROFIT
            Card(
                modifier = Modifier
                    .weight(1f) // Membagi lebar secara proporsional
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label "Sales"
                        Text(
                            text = "PROFIT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueJC
                        )

                        // Total Sales dengan frame abu-abu
                        val formatter = DecimalFormat("#,###")
                        val formattedProfit = "$" + formatter.format(totalProfit)

                        Text(
                            text = "$formattedProfit",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )

                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                    val entriesProfit = salesByMonth.mapIndexed { index, result ->
                        val date =
                            SimpleDateFormat(
                                "yyyy-MM",
                                Locale.getDefault()
                            ).parse(result.Month) // Parse "2024-10"
                        val formattedDate =
                            date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                        Entry(index.toFloat(), result.Total_Profit.toFloat())
                    }
                    AndroidView(
                        modifier = Modifier.height(50.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                val dataSet = LineDataSet(entriesProfit, "Predicted Sales").apply {
                                    color = android.graphics.Color.parseColor("#E69F00")
//                            valueTextColor =
                                    setDrawCircles(false)
                                    android.graphics.Color.parseColor("#CC79A7") // Set warna text
                                    lineWidth = 2f // Set ketebalan garis
                                    setDrawValues(false)  // Enable drawing values on each point
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
                                invalidate()

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        e?.let {
                                            val index = it.x.toInt() // Index of the entry
                                            val month =
                                                salesByMonth[index].Month // Access the month
                                            val profit = salesByMonth[index].Total_Profit
                                            val formatter = DecimalFormat("#,###")
                                            val formattedProfit = "$" + formatter.format(profit)
                                            val originalFormat =
                                                SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat =
                                                SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                            val formattedMonth =
                                                originalFormat.parse(month)?.let { date ->
                                                    targetFormat.format(date)
                                                } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth Profit: $formattedProfit",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected() {
                                        // No action required when nothing is selected
                                    }
                                })
                            }
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp), // Padding di kiri dan kanan baris
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spasi antar-kartu
        ) {

            // QUANTITY
            Card(
                modifier = Modifier
                    .weight(1f) // Membagi lebar secara proporsional
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label "Sales"
                        Text(
                            text = "QUANTITY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueJC
                        )


                        Text(
                            text = "$totalQuantity",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                    val entriesQuantity = salesByMonth.mapIndexed { index, result ->
                        val date =
                            SimpleDateFormat(
                                "yyyy-MM",
                                Locale.getDefault()
                            ).parse(result.Month) // Parse "2024-10"
                        val formattedDate =
                            date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                        Entry(index.toFloat(), result.Total_Quantity.toFloat())
                    }
                    AndroidView(
                        modifier = Modifier.height(50.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                val dataSet =
                                    LineDataSet(entriesQuantity, "Predicted Sales").apply {
                                        color = android.graphics.Color.parseColor("#E69F00")
//                            valueTextColor =
                                        setDrawCircles(false)
                                        android.graphics.Color.parseColor("#CC79A7") // Set warna text
                                        lineWidth = 2f // Set ketebalan garis
                                        setDrawValues(false)  // Enable drawing values on each point
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
                                invalidate()

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        e?.let {
                                            val index = it.x.toInt() // Index of the entry
                                            val month =
                                                salesByMonth[index].Month // Access the month
                                            val quantity =
                                                salesByMonth[index].Total_Quantity.toInt()
                                            val originalFormat =
                                                SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat =
                                                SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                            val formattedMonth =
                                                originalFormat.parse(month)?.let { date ->
                                                    targetFormat.format(date)
                                                } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth Quantity Sold: $quantity",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected() {
                                        // No action required when nothing is selected
                                    }
                                })
                            }
                        }
                    )
                }
            }

            // ORDERS
            Card(
                modifier = Modifier
                    .weight(1f) // Membagi lebar secara proporsional
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label "Sales"
                        Text(
                            text = "ORDERS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueJC
                        )


                        Text(
                            text = "${totalOrder}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.Gray,
                        thickness = 1.dp
                    )

                    // Chart bagian bawah
                    val entriesOrder = salesByMonth.mapIndexed { index, result ->
                        val date =
                            SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month)
                        val formattedDate = date?.let { dateFormat.format(it) }
                        Entry(index.toFloat(), result.Total_Order.toFloat())
                    }

                    AndroidView(
                        modifier = Modifier.height(50.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                val dataSet =
                                    LineDataSet(entriesOrder, "Predicted Sales").apply {
                                        color = android.graphics.Color.parseColor("#E69F00")
                                        lineWidth = 2f
                                        setDrawValues(false)
                                        setDrawCircles(false)
                                        setValueTextColor(android.graphics.Color.BLACK)
                                        mode = LineDataSet.Mode.CUBIC_BEZIER
                                    }

                                val lineData = LineData(dataSet)
                                this.data = lineData
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                xAxis.setLabelCount(5, true)
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
                                invalidate()

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        e?.let {
                                            val index = it.x.toInt() // Index of the entry
                                            val month =
                                                salesByMonth[index].Month // Access the month
                                            val order = salesByMonth[index].Total_Order
                                            val originalFormat =
                                                SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat =
                                                SimpleDateFormat(
                                                    "MMMM yyyy",
                                                    Locale.getDefault()
                                                )
                                            val formattedMonth =
                                                originalFormat.parse(month)?.let { date ->
                                                    targetFormat.format(date)
                                                } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth Orders: $order",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected() {
                                        // No action required when nothing is selected
                                    }
                                })
                            }
                        }
                    )
                }
            }

            // CUSTOMER
            Card(
                modifier = Modifier
                    .weight(1f) // Membagi lebar secara proporsional
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label "Sales"
                        Text(
                            text = "CUSTOMERS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueJC
                        )


                        Text(
                            text = "${totalCustomer}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color.Gray,
                        thickness = 1.dp
                    )

                    // Chart bagian bawah
                    val entriesCustomer = salesByMonth.mapIndexed { index, result ->
                        val date =
                            SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(result.Month)
                        val formattedDate = date?.let { dateFormat.format(it) }
                        Entry(index.toFloat(), result.Total_Customer.toFloat())
                    }

                    AndroidView(
                        modifier = Modifier.height(50.dp),
                        factory = { context ->
                            LineChart(context).apply {
                                val dataSet =
                                    LineDataSet(entriesCustomer, "Predicted Sales").apply {
                                        color = android.graphics.Color.parseColor("#E69F00")
                                        lineWidth = 2f
                                        setDrawValues(false)
                                        setDrawCircles(false)
                                        setValueTextColor(android.graphics.Color.BLACK)
                                        mode = LineDataSet.Mode.CUBIC_BEZIER
                                    }

                                val lineData = LineData(dataSet)
                                this.data = lineData
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                xAxis.setLabelCount(5, true)
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
                                invalidate()

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        e?.let {
                                            val index = it.x.toInt() // Index of the entry
                                            val month =
                                                salesByMonth[index].Month // Access the month
                                            val customer = salesByMonth[index].Total_Customer
                                            val originalFormat =
                                                SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat =
                                                SimpleDateFormat(
                                                    "MMMM yyyy",
                                                    Locale.getDefault()
                                                )
                                            val formattedMonth =
                                                originalFormat.parse(month)?.let { date ->
                                                    targetFormat.format(date)
                                                } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth: $customer New Customer",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onNothingSelected() {
                                        // No action required when nothing is selected
                                    }
                                })
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SalesByRegion(salesDataRegion: List<SalesDataRegion>, totalSales: Int) {
    val context = LocalContext.current
    val quickSandTypeface = Typeface.createFromAsset(context.assets, "font/quicksand_regular.ttf")
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(it).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)
                setCenterTextSize(20f)
                val formatter = DecimalFormat("#,###")
                val formattedSales = "$" + formatter.format(totalSales)

                setCenterText("Total Sales: $formattedSales")
                setCenterTextTypeface(quickSandTypeface)
                setEntryLabelTypeface(quickSandTypeface)
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(
                        e: com.github.mikephil.charting.data.Entry?,
                        h: Highlight?
                    ) {
                        if (e is PieEntry && e.label != null) {
                            val salesAmount = e.value.toInt()
                            val selectedSegment = e.label
                            val formattedSalesAmount = "$" + formatter.format(salesAmount)
                            Toast.makeText(
                                it,
                                "$selectedSegment Sales: $formattedSalesAmount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesRegion = salesDataRegion.sumOf { it.Total_Sales.toDouble() }
            val entriesRegion = salesDataRegion.map {
                PieEntry(it.Total_Sales, it.Region)
            }

            val dataSetRegion = PieDataSet(entriesRegion, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#488f31"), // Oranye Kuning (warna utama)
                    android.graphics.Color.parseColor("#a8c162"), // Oranye Merah (lebih kuat, kontras dengan oranye)
                    android.graphics.Color.parseColor("#f9a160"), // Merah Muda Tua (aksen kuat dan berani)
                    android.graphics.Color.parseColor("#de425b"),  // Biru Laut (aksen kontras untuk keseimbangan)
                )
                sliceSpace = 5f
                selectionShift = 5f
                valueTextSize = 14f
                valueFormatter = PercentValueFormatter(totalSalesRegion.toFloat())
            }
            chart.data = PieData(dataSetRegion)
            chart.animateY(1000, Easing.EaseInOutQuart)
            chart.invalidate()
        }
    )
}

@Composable
fun SalesByCategory(salesDataCategory: List<SalesDataCategory>, totalProfit: Int) {
    val context = LocalContext.current
    val quickSandTypeface = Typeface.createFromAsset(context.assets, "font/quicksand_regular.ttf")
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(it).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)

                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)
                val formatter = DecimalFormat("#,###")
                val formattedProfit = "$" + formatter.format(totalProfit)

                setCenterText("Total Profit: $formattedProfit")
                setCenterTextTypeface(quickSandTypeface)
                setEntryLabelTypeface(quickSandTypeface)

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(
                        e: com.github.mikephil.charting.data.Entry?,
                        h: Highlight?
                    ) {
                        if (e is PieEntry && e.label != null) {
                            val salesAmount = e.value.toInt()
                            val selectedSegment = e.label
                            val formattedSalesAmount = "$" + formatter.format(salesAmount)
                            Toast.makeText(
                                it,
                                "$selectedSegment Sales: $formattedSalesAmount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesCategory = salesDataCategory.sumOf { it.Total_Sales.toDouble() }
            val entriesCategory = salesDataCategory.map {
                PieEntry(it.Total_Sales, it.Category)
            }

            val dataSetCategory = PieDataSet(entriesCategory, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#488f31"), // Oranye Kuning (warna utama)
                    android.graphics.Color.parseColor("#f9a160"), // Oranye Merah (lebih kuat, kontras dengan oranye)
                    android.graphics.Color.parseColor("#de425b") // Biru Laut (aksen kontras untuk keseimbangan)
                )
                sliceSpace = 5f
                selectionShift = 5f
                valueTextSize = 14f
                valueFormatter = PercentValueFormatter(totalSalesCategory.toFloat())
            }
            chart.data = PieData(dataSetCategory)
            chart.animateY(1000, Easing.EaseInOutQuart)
            chart.invalidate()
        }
    )
}

@Composable
fun SalesBySegment(salesDataSegment: List<SalesDataSegment>, totalQuantity: Int) {
    val context = LocalContext.current
    val quickSandTypeface = Typeface.createFromAsset(context.assets, "font/quicksand_regular.ttf")
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(it).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)

                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)
                val formatter = DecimalFormat("#,###")
                setCenterText("Total Quantity: $totalQuantity")
                setCenterTextTypeface(quickSandTypeface)
                setEntryLabelTypeface(quickSandTypeface)

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(
                        e: com.github.mikephil.charting.data.Entry?,
                        h: Highlight?
                    ) {
                        if (e is PieEntry && e.label != null) {
                            val salesAmount = e.value.toInt()
                            val selectedSegment = e.label
                            val formattedSalesAmount = "$" + formatter.format(salesAmount)
                            Toast.makeText(
                                it,
                                "$selectedSegment Sales: $formattedSalesAmount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesSegment = salesDataSegment.sumOf { it.Total_Sales.toDouble() }
            val entriesSegment = salesDataSegment.map {
                PieEntry(it.Total_Sales, it.Segment)
            }

            val dataSetSegment = PieDataSet(entriesSegment, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#488f31"), // Oranye Kuning (warna utama)
                    android.graphics.Color.parseColor("#f9a160"), // Oranye Merah (lebih kuat, kontras dengan oranye)
                    android.graphics.Color.parseColor("#de425b") // Biru Laut (aksen kontras untuk keseimbangan)
                )
                sliceSpace = 5f
                selectionShift = 5f
                valueTextSize = 14f
                valueFormatter = PercentValueFormatter(totalSalesSegment.toFloat())
            }
            chart.data = PieData(dataSetSegment)
            chart.animateY(1000, Easing.EaseInOutQuart)
            chart.invalidate()
        }
    )
}

@Composable
fun SalesBySubCategory(salesDataSubCategory: List<SalesDataSubCategory>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val contextTypeface = LocalContext.current
            val quickSandTypeface =
                Typeface.createFromAsset(contextTypeface.assets, "font/quicksand_regular.ttf")

            Text(
                text = "Sub-Category Sales",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))
            AndroidView(
                modifier = Modifier
                    .height(350.dp)
                    .width(300.dp),
                factory = { context ->
                    HorizontalBarChart(context).apply {
                        description.isEnabled = false
                        setDrawGridBackground(true)
                        setMaxVisibleValueCount(5)
                        setPinchZoom(false)
                        setDrawBarShadow(false)
                        setFitBars(true)
                        val formatter = DecimalFormat("#,###")
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawLabels(true)
                            setTypeface(quickSandTypeface)
                        }

                        axisLeft.apply {
                            setTypeface(quickSandTypeface)
                        }

                        axisRight.apply {
                            setTypeface(quickSandTypeface)
                        }

                        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                if (e is BarEntry) {
                                    val salesAmount = e.y.toInt()
                                    val selectedSubCategory = e.data as? String
                                    val formattedSalesAmount = "$" + formatter.format(salesAmount)
                                    if (selectedSubCategory != null) {
                                        val selectedDataSubCategory =
                                            salesDataSubCategory.find { it.SubCategory == selectedSubCategory }
                                        if (selectedDataSubCategory != null) {
                                            Toast.makeText(
                                                context,
                                                "${selectedSubCategory} Sales: ${formattedSalesAmount}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Data not found for $selectedSubCategory",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Invalid SubCategory Data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onNothingSelected() {}
                        })
                    }
                },
                update = { chart ->
                    val totalSalesSubCategory =
                        salesDataSubCategory.sumOf { it.Total_Sales.toDouble() }
                    val entriesSubCategory = salesDataSubCategory.mapIndexed { index, it ->
                        BarEntry(index.toFloat(), it.Total_Sales.toFloat(), it.SubCategory)
                    }

                    val dataSet = BarDataSet(entriesSubCategory, "Sales by Sub-Category").apply {
                        colors = listOf(
                            android.graphics.Color.parseColor("#488f31"), // Biru Tua (Deep Blue)
                            android.graphics.Color.parseColor("#70a343"), // Biru Menengah (Medium Blue)
                            android.graphics.Color.parseColor("#95b757"), // Ungu Biru (Bluish Purple)
                            android.graphics.Color.parseColor("#b9cb6d"), // Ungu Muda (Light Purple)
                            android.graphics.Color.parseColor("#dde086"), // Merah Muda (Pink)
                            android.graphics.Color.parseColor("#fff4a0"), // Merah Cerah (Bright Red)
                            android.graphics.Color.parseColor("#fed47f"), // Oranye Terang (Bright Orange)
                            android.graphics.Color.parseColor("#fbb268"),
                            android.graphics.Color.parseColor("#ed6a57"),
                            android.graphics.Color.parseColor("#de425b")// Kuning Oranye (Yellow Orange)
                        )

                    }

                    val barData = BarData(dataSet).apply {
                        barWidth = 0.6f
                    }

                    chart.data = barData

                    val labels: ArrayList<String> =
                        ArrayList(salesDataSubCategory.map { it.SubCategory })
                    val xAxis = chart.xAxis
                    xAxis.setLabelCount(labels.size, false)
                    xAxis.setDrawGridLines(false)
                    chart.xAxis.spaceMax = 0.5f
                    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    chart.animateY(1000, Easing.EaseInOutQuart)
                    chart.axisRight.setDrawGridLines(false)
                    chart.legend.isEnabled = false
                    chart.axisLeft.isInverted
                    chart.invalidate()
                }
            )
        }
    }
}

@Composable
fun OrderShipMode(orderShipMode: List<OrderShipMode>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val quickSandTypeface =
                Typeface.createFromAsset(context.assets, "font/quicksand_regular.ttf")

            Text(
                text = "Order By ShipMode",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))
            AndroidView(
                modifier = Modifier
                    .size(350.dp),
                factory = {
                    PieChart(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setTransparentCircleAlpha(110)
                        setEntryLabelColor(android.graphics.Color.BLACK)
                        description.isEnabled = false
                        isDrawHoleEnabled = false // Tidak ada lubang
                        setUsePercentValues(true)
                        setEntryLabelTextSize(12f)
                        setNoDataText("No data available")
                        setEntryLabelTypeface(quickSandTypeface)
                        legend.isEnabled = true
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        legend.orientation = Legend.LegendOrientation.HORIZONTAL
                        legend.setDrawInside(false)

                        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(
                                e: com.github.mikephil.charting.data.Entry?,
                                h: Highlight?
                            ) {
                                if (e is PieEntry && e.label != null) {
                                    val orderAmount = e.value.toInt()
                                    val selectedShipMode = e.label
                                    Toast.makeText(
                                        it,
                                        "$selectedShipMode Orders: $orderAmount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onNothingSelected() {}
                        })
                    }
                },
                update = { pieChart ->
                    val entries = orderShipMode.map { (shipMode, totalOrder) ->
                        PieEntry(totalOrder.toFloat(), shipMode)
                    }
                    val dataSet = PieDataSet(entries, "").apply {
                        colors = listOf(
                            android.graphics.Color.parseColor("#488f31"), // Oranye Kuning (warna utama)
                            android.graphics.Color.parseColor("#a8c162"), // Oranye Merah (lebih kuat, kontras dengan oranye)
                            android.graphics.Color.parseColor("#f9a160"), // Merah Muda Tua (aksen kuat dan berani)
                            android.graphics.Color.parseColor("#de425b"),  // Biru Laut (aksen kontras untuk keseimbangan)
                        )
                        sliceSpace = 5f
                        selectionShift = 5f
                        valueTextSize = 14f

                    }
                    val data = PieData(dataSet).apply {
                        setValueFormatter(PercentFormatter(pieChart))
                    }
                    pieChart.data = data
                    pieChart.animateY(1000, Easing.EaseInOutQuart)
                    pieChart.invalidate() // Refresh the chart with new data
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegionLineChart(profitByRegion: List<ProfitByRegion>, fromMonth: LocalDate, toMonth: LocalDate) {
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    val filteredData = profitByRegion.filter {
        val regionMonth = YearMonth.parse(it.Month, monthFormatter).atDay(1) // Convert to LocalDate by assuming the 1st day
        !regionMonth.isBefore(fromMonth) && !regionMonth.isAfter(toMonth) // Ensure it's within the range
    }
    val months = filteredData.map { YearMonth.parse(it.Month, monthFormatter).atDay(1) }.distinct()
    val categories = filteredData.map { it.Region }.distinct()

    val lineDataSets = categories.map { region ->
        val entries = filteredData
            .filter { it.Region == region }
            .mapIndexed { index, data ->
                Entry(index.toFloat(), data.Total_Profit)
            }

        LineDataSet(entries, region).apply {
            color = when (region) {
                "West" -> android.graphics.Color.parseColor("#488f31")
                "East" -> android.graphics.Color.parseColor("#a8c162")
                "Central" -> android.graphics.Color.parseColor("#f9a160")
                "South" -> android.graphics.Color.parseColor("#de425b")
                else -> android.graphics.Color.parseColor("#000000")
            }
            valueTextColor = android.graphics.Color.parseColor("#000000")
            lineWidth = 2f
            setDrawCircles(false)
        }
    }

    val lineData = LineData(lineDataSets)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { entry ->
                            h?.let { highlight ->
                                val dataSetIndex = highlight.dataSetIndex
                                val category = when (dataSetIndex) {
                                    0 -> "Central"
                                    1 -> "East"
                                    2 -> "South"
                                    3 -> "West"
                                    else -> "Unknown"
                                }

                                val filteredByCategory = filteredData.filter { it.Region == category }
                                val selectedMonthIndex = months.indexOf(YearMonth.parse(filteredByCategory[entry.x.toInt()].Month, monthFormatter).atDay(1))

                                if (selectedMonthIndex >= 0 && selectedMonthIndex < filteredByCategory.size) {
                                    val selectedData = filteredByCategory[selectedMonthIndex]
                                    val month = selectedData.Month
                                    val profit = selectedData.Total_Profit

                                    val formatter = DecimalFormat("#,###")
                                    val formattedProfit = "$" + formatter.format(profit)

                                    try {
                                        val originalFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                        val targetFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                        val formattedMonth = originalFormat.parse(month)?.let { date -> targetFormat.format(date) } ?: month

                                        Toast.makeText(context, "$formattedMonth $category Profit: $formattedProfit", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "Error formatting date", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Data not found for selected point", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onNothingSelected() {}
                })


            }
        },
        update = { lineChart ->
            lineChart.data = lineData
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(months.map { it.format(DateTimeFormatter.ofPattern("yyyy-MM")) })
            lineChart.xAxis.granularity = 1f
            lineChart.invalidate()
            lineChart.animateY(1000, Easing.EaseInOutQuart)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
    val maxProfit = filteredData.maxOfOrNull { it.Total_Profit } ?: 0.0f
    println("Highest Profit: $maxProfit")
    val minProfit = filteredData.minOfOrNull { it.Total_Profit } ?: 0.0f
    println("Lowest Profit: $minProfit")
    val averageProfit = filteredData.map { it.Total_Profit }.average()
    println("Average Profit: $averageProfit")
    val totalProfit = filteredData.fold(0.0f) { sum, prediction ->
        sum + prediction.Total_Profit
    }
    println("Total Profit: $totalProfit")
    GetStats(maxProfit, minProfit, averageProfit.toFloat(), totalProfit)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryLineChart(profitByCategory: List<ProfitByCategory>, fromMonth: LocalDate, toMonth: LocalDate) {
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    val filteredData = profitByCategory.filter {
        val categoryMonth = YearMonth.parse(it.Month, monthFormatter).atDay(1) // Convert to LocalDate by assuming the 1st day
        !categoryMonth.isBefore(fromMonth) && !categoryMonth.isAfter(toMonth) // Ensure it's within the range
    }
    val months = filteredData.map { YearMonth.parse(it.Month, monthFormatter).atDay(1) }.distinct()
    val categories = filteredData.map { it.Category }.distinct()

    val context = LocalContext.current
    val lineDataSets = categories.map { category ->
        val entries = filteredData
            .filter { it.Category == category }
            .mapIndexed { index, data -> Entry(index.toFloat(), data.Total_Profit) }

        LineDataSet(entries, category).apply {
            color = when (category) {
                "Technology" -> android.graphics.Color.parseColor("#488f31")
                "Furniture" -> android.graphics.Color.parseColor("#f9a160")
                "Office Supplies" -> android.graphics.Color.parseColor("#de425b")
                else -> android.graphics.Color.parseColor("#000000")
            }
            valueTextColor = android.graphics.Color.parseColor("#000000")
            lineWidth = 2f
            setDrawCircles(false)
        }
    }

    val lineData = LineData(lineDataSets)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { entry ->
                            h?.let { highlight ->
                                val dataSetIndex = highlight.dataSetIndex

                                val category = when (dataSetIndex) {
                                    0 -> "Furniture"
                                    1 -> "Office Supplies"
                                    2 -> "Technology"
                                    else -> "Unknown"
                                }

                                // Filter data based on category
                                val categoryData = filteredData.filter { it.Category == category }

                                // Check if the categoryData is not empty and index is within bounds
                                if (categoryData.isNotEmpty() && entry.x.toInt() in categoryData.indices) {
                                    val index = entry.x.toInt()
                                    val selectedData = categoryData[index]

                                    selectedData?.let { data ->
                                        val month = data.Month
                                        val profit = data.Total_Profit
                                        val formatter = DecimalFormat("#,###")
                                        val formattedProfit = "$" + formatter.format(profit)

                                        try {
                                            val originalFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                            val formattedMonth = originalFormat.parse(month)?.let { date ->
                                                targetFormat.format(date)
                                            } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth $category Profit: $formattedProfit",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Data not found for the selected entry.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onNothingSelected() {
                        // No action required when nothing is selected
                    }
                })

            }
        },
        update = { lineChart ->
            lineChart.data = lineData
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(months.map { it.format(DateTimeFormatter.ofPattern("yyyy-MM")) })
            lineChart.xAxis.granularity = 1f
            lineChart.invalidate() // Force re-render the chart
            lineChart.animateY(1000, Easing.EaseInOutQuart)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
    val maxProfit = filteredData.maxOfOrNull { it.Total_Profit } ?: 0.0f
    println("Highest Profit: $maxProfit")
    val minProfit = filteredData.minOfOrNull { it.Total_Profit } ?: 0.0f
    println("Lowest Profit: $minProfit")
    val averageProfit = filteredData.map { it.Total_Profit }.average()
    println("Average Profit: $averageProfit")
    val totalProfit = filteredData.fold(0.0f) { sum, prediction ->
        sum + prediction.Total_Profit
    }
    println("Total Profit: $totalProfit")
    GetStats(maxProfit, minProfit, averageProfit.toFloat(), totalProfit)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SegmentLineChart(profitBySegment: List<ProfitBySegment>, fromMonth: LocalDate, toMonth: LocalDate) {
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    val filteredData = profitBySegment.filter {
        val segmentMonth = YearMonth.parse(it.Month, monthFormatter).atDay(1) // Convert to LocalDate by assuming the 1st day
        !segmentMonth.isBefore(fromMonth) && !segmentMonth.isAfter(toMonth) // Ensure it's within the range
    }
    val months = filteredData.map { YearMonth.parse(it.Month, monthFormatter).atDay(1) }.distinct()
    val segments = filteredData.map { it.Segment }.distinct()

    val context = LocalContext.current
    val lineDataSets = segments.map { segment ->
        val entries = filteredData
            .filter { it.Segment == segment }
            .mapIndexed { index, data -> Entry(index.toFloat(), data.Total_Profit) }

        LineDataSet(entries, segment).apply {
            color = when (segment) {
                "Consumer" -> android.graphics.Color.parseColor("#488f31")
                "Corporate" -> android.graphics.Color.parseColor("#f9a160")
                "Home Office" -> android.graphics.Color.parseColor("#de425b")
                else -> android.graphics.Color.parseColor("#000000")
            }
            valueTextColor = android.graphics.Color.parseColor("#000000")
            lineWidth = 2f
            setDrawCircles(false)
        }
    }

    val lineData = LineData(lineDataSets)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let { entry ->
                            h?.let { highlight ->
                                val dataSetIndex = highlight.dataSetIndex

                                val segment = when (dataSetIndex) {
                                    0 -> "Consumer"
                                    1 -> "Corporate"
                                    2 -> "Home Office"
                                    else -> "Unknown"
                                }

                                // Filter data based on category
                                val segmentData = filteredData.filter { it.Segment == segment }

                                // Check if the categoryData is not empty and index is within bounds
                                if (segmentData.isNotEmpty() && entry.x.toInt() in segmentData.indices) {
                                    val index = entry.x.toInt()
                                    val selectedData = segmentData[index]

                                    selectedData?.let { data ->
                                        val month = data.Month
                                        val profit = data.Total_Profit
                                        val formatter = DecimalFormat("#,###")
                                        val formattedProfit = "$" + formatter.format(profit)

                                        try {
                                            val originalFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                            val targetFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                            val formattedMonth = originalFormat.parse(month)?.let { date ->
                                                targetFormat.format(date)
                                            } ?: month

                                            Toast.makeText(
                                                context,
                                                "$formattedMonth $segment Profit: $formattedProfit",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Data not found for the selected entry.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onNothingSelected() {
                        // No action required when nothing is selected
                    }
                })
            }
        },
        update = { lineChart ->
            lineChart.data = lineData
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(months.map { it.format(DateTimeFormatter.ofPattern("yyyy-MM")) })
            lineChart.xAxis.granularity = 1f
            lineChart.invalidate() // Force re-render the chart
            lineChart.animateY(1000, Easing.EaseInOutQuart)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
    val maxProfit = filteredData.maxOfOrNull { it.Total_Profit } ?: 0.0f
    println("Highest Profit: $maxProfit")
    val minProfit = filteredData.minOfOrNull { it.Total_Profit } ?: 0.0f
    println("Lowest Profit: $minProfit")
    val averageProfit = filteredData.map { it.Total_Profit }.average()
    println("Average Profit: $averageProfit")
    val totalProfit = filteredData.fold(0.0f) { sum, prediction ->
        sum + prediction.Total_Profit
    }
    println("Total Profit: $totalProfit")
    GetStats(maxProfit, minProfit, averageProfit.toFloat(), totalProfit)
}

@Composable
fun GetStats(max: Float, min: Float, avg: Float, total: Float){
    val currencyFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val formattedValue = "%,.0f".format(value) // Format value with dot as thousand separator and no decimals
            return "$$formattedValue" // Prefix with "Rp" for Indonesian currency format
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Profit Statistics",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Highest Profit:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(max),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lowest Profit:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(min),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Average Profit:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(avg.toFloat()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Profit:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currencyFormatter.getFormattedValue(total),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SalesByStateTable(salesByState: List<SalesByState>) {
    var showAll by remember { mutableStateOf(false) }
    var sortedData by remember { mutableStateOf(salesByState) }
    var sortBy by remember { mutableStateOf("Sales") } // Menyimpan kolom yang sedang dipilih untuk diurutkan

    val dataToDisplay = if (showAll) sortedData else sortedData.take(5)

    fun sortData(by: String) {
        sortedData = when (by) {
            "Sales" -> salesByState.sortedByDescending { it.Total_Sales }
            "Profit" -> salesByState.sortedByDescending { it.Total_Profit }
            "Quantity" -> salesByState.sortedByDescending { it.Total_Quantity }
            else -> salesByState
        }
        sortBy = by // Simpan kolom yang sedang dipilih
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sales by State",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (showAll) {

                Text(
                    text = "Sort by",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { sortData("Sales") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (sortBy == "Sales") BlueJC else Color.Gray)
                    ) {
                        Text(text = "Sales", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { sortData("Profit") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (sortBy == "Profit") BlueJC else Color.Gray)
                    ) {
                        Text(text = "Profit", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { sortData("Quantity") },
                        colors = ButtonDefaults.buttonColors(containerColor = if (sortBy == "Quantity") BlueJC else Color.Gray)
                    ) {
                        Text(text = "Quantity", color = Color.White)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "State",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Sales",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "Profit",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "Quantity",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Data Tabel
                val formatter = DecimalFormat("#,###")
                items(dataToDisplay) { item ->
                    val formattedSales = "$" + formatter.format(item.Total_Sales)
                    val formattedProfit = "$" + formatter.format(item.Total_Profit)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.StateProvince,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formattedSales,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = formattedProfit,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "${item.Total_Quantity.toInt()}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    Divider() // Memisahkan baris data
                }

                // Tombol "View More" untuk menampilkan semua data
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAll = !showAll },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueJC)
                    ) {
                        Text(
                            text = if (showAll) "View Less" else "View More",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun ChartWithButtons(
    salesDataRegion: List<SalesDataRegion>,
    salesDataCategory: List<SalesDataCategory>,
    salesDataSegment: List<SalesDataSegment>,
    totalSales: Int,
    totalProfit: Int,
    totalQuantity: Int
) {
    var selectedChart by remember { mutableStateOf("Region") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp), // Rounded corners for the card
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
                        shape = RoundedCornerShape(20.dp), // Rounded for the container
                        color = Color(0xFFEEEEEE), // Light gray for the container
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { selectedChart = "Region" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Region") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_place_24),
                                    contentDescription = "Sales by Region",
                                    tint = if (selectedChart == "Region") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Category" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Category") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_category_24),
                                    contentDescription = "Sales by Category",
                                    tint = if (selectedChart == "Category") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Segment" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Segment") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_segment_24),
                                    contentDescription = "Sales by Segment",
                                    tint = if (selectedChart == "Segment") Color.Black else Color.Gray
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (selectedChart) {
                            "Region" -> {
                                Text(
                                    text = "Sales by Region",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSand
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                SalesByRegion(salesDataRegion, totalSales)
                            }

                            "Category" -> {
                                Text(
                                    text = "Sales by Category",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                SalesByCategory(salesDataCategory, totalProfit)
                            }

                            "Segment" -> {
                                Text(
                                    text = "Sales by Segment",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                SalesBySegment(salesDataSegment, totalQuantity)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun LineChartWithButtons(
    profitByCategory: List<ProfitByCategory>,
    profitByRegion: List<ProfitByRegion>,
    profitBySegment: List<ProfitBySegment>,
    viewModel: SalesViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedChart by remember { mutableStateOf("Region") }

    fun String.toLocalDate(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse("$this-01", formatter)
    }

    fun <T> getMinMaxMonths(data: List<T>, getMonth: (T) -> String): Pair<LocalDate, LocalDate> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val months = data.map { item ->
            val monthString = getMonth(item)
            LocalDate.parse("$monthString-01", formatter)
        }
        val minMonth = months.minOrNull() ?: LocalDate.now()
        val maxMonth = months.maxOrNull() ?: LocalDate.now()
        return Pair(minMonth, maxMonth)
    }

    @Composable
    fun <T> MonthRangeSlider(
        data: List<T>,
        getMonth: (T) -> String,
        onSubmit: (LocalDate, LocalDate) -> Unit
    ) {
        val (minMonth, maxMonth) = getMinMaxMonths(data, getMonth)
        val minEpoch = minMonth.toEpochDay().toFloat()
        val maxEpoch = maxMonth.toEpochDay().toFloat()
        var sliderValues by remember { mutableStateOf(minEpoch..maxEpoch) }
        val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE)),
            shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${LocalDate.ofEpochDay(sliderValues.start.toLong()).format(monthFormatter)} " +
                            "- ${LocalDate.ofEpochDay(sliderValues.endInclusive.toLong()).format(monthFormatter)}"
                )

                RangeSlider(
                    value = sliderValues,
                    onValueChange = { value -> sliderValues = value },
                    valueRange = minEpoch..maxEpoch,
                    steps = 12,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF2196F3),
                        activeTrackColor = Color(0xFF2196F3),
                        inactiveTrackColor = Color.Gray
                    )
                )

                Button(
                    onClick = {
                        val fromMonth = LocalDate.ofEpochDay(sliderValues.start.toLong())
                        val toMonth = LocalDate.ofEpochDay(sliderValues.endInclusive.toLong())
                        onSubmit(fromMonth, toMonth)
                        Log.d("MonthRangeSlider", "onSubmit called with: $fromMonth to $toMonth")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
                            IconButton(
                                onClick = { selectedChart = "Region" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Region") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_place_24),
                                    contentDescription = "Profit by Region",
                                    tint = if (selectedChart == "Region") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Category" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Category") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_category_24),
                                    contentDescription = "Profit by Category",
                                    tint = if (selectedChart == "Category") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Segment" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Segment") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_segment_24),
                                    contentDescription = "Profit by Segment",
                                    tint = if (selectedChart == "Segment") Color.Black else Color.Gray
                                )
                            }
                        }
                    }
                }



                when (selectedChart) {
                    "Region" -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        var fromMonth by remember { mutableStateOf(LocalDate.MIN) }
                        var toMonth by remember { mutableStateOf(LocalDate.MAX) }

                        MonthRangeSlider(
                            data = profitByRegion,
                            getMonth = { it.Month }, // Akses kolom 'Month' dari ProfitBySegment
                            onSubmit = { from, to ->
                                fromMonth = from
                                toMonth = to
                                Log.d("Result", "Selected range: $from to $to")
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Profit by Region",
                            fontSize = 30.sp,
                            color = BlueJC,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        RegionLineChart(profitByRegion = profitByRegion, fromMonth = fromMonth, toMonth = toMonth)
                    }

                    "Category" -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        var fromMonth by remember { mutableStateOf(LocalDate.MIN) }
                        var toMonth by remember { mutableStateOf(LocalDate.MAX) }

                        MonthRangeSlider(
                            data = profitByCategory,
                            getMonth = { it.Month }, // Akses kolom 'Month' dari ProfitBySegment
                            onSubmit = { from, to ->
                                fromMonth = from
                                toMonth = to
                                Log.d("Result", "Selected range: $from to $to")
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Profit by Category",
                            fontSize = 30.sp,
                            color = BlueJC,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        CategoryLineChart(profitByCategory = profitByCategory, fromMonth = fromMonth, toMonth = toMonth)
                    }

                    "Segment" -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        var fromMonth by remember { mutableStateOf(LocalDate.MIN) }
                        var toMonth by remember { mutableStateOf(LocalDate.MAX) }

                        MonthRangeSlider(
                            data = profitBySegment,
                            getMonth = { it.Month }, // Akses kolom 'Month' dari ProfitBySegment
                            onSubmit = { from, to ->
                                fromMonth = from
                                toMonth = to
                                Log.d("Result", "Selected range: $from to $to")
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Profit by Segment",
                            fontSize = 30.sp,
                            color = BlueJC,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SegmentLineChart(profitBySegment = profitBySegment, fromMonth = fromMonth, toMonth = toMonth)
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = 0.5f
                            }
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

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
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Dashboard(viewModel: SalesViewModel = viewModel()) {
    val salesDataByRegion by viewModel.salesDataByRegion.collectAsState()
    val salesDataByCategory by viewModel.salesDataByCategory.collectAsState()
    val salesDataBySegment by viewModel.salesDataBySegment.collectAsState()
    val salesDataBySubCategory by viewModel.salesDataBySubCategory.collectAsState()
    val salesByMonth by viewModel.salesByMonth.collectAsState()
    val salesByState by viewModel.salesByState.collectAsState()
    val orderShipMode by viewModel.orderByShipMode.collectAsState()
    val profitByCategory by viewModel.profitByCategory.collectAsState()
    val profitByRegion by viewModel.profitByRegion.collectAsState()
    val profitBySegment by viewModel.profitBySegment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val totalSales = salesDataByRegion.sumOf { it.Total_Sales.toInt() ?: 0 }
    val totalProfit = salesDataByRegion.sumOf { it.Total_Profit.toInt() }
    val totalQuantity = salesDataBySegment.sumOf { it.Total_Quantity.toInt() }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                viewModel.refreshData()
                delay(2000)
                refreshing = false
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = { refreshing = true },
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainHightlight(salesByMonth)
                    ChartWithButtons(
                        salesDataByRegion,
                        salesDataByCategory,
                        salesDataBySegment,
                        totalSales,
                        totalProfit,
                        totalQuantity
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LineChartWithButtons(profitByCategory, profitByRegion, profitBySegment)
                    Spacer(modifier = Modifier.height(8.dp))
                    SalesBySubCategory(salesDataBySubCategory)
                    Spacer(modifier = Modifier.height(8.dp))
                    SalesByStateTable(salesByState)
                    Spacer(modifier = Modifier.height(8.dp))
                    OrderShipMode(orderShipMode)
                    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
                }
            }
        }
    }
}
