package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SalesByManager(profitByManager: List<ProfitByManager>, totalSales: Int) {
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
            val totalSalesRegion = profitByManager.sumOf { it.Total_Sales.toDouble() }
            val entriesRegion = profitByManager.map {
                PieEntry(it.Total_Sales, it.Manager_Name)
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
fun ProfitByManager(salesDataRegion: List<ProfitByManager>, totalProfit: Int) {
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
                val formattedSales = "$" + formatter.format(totalProfit)

                setCenterText("Total Profit: $formattedSales")
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
                                "$selectedSegment Profit: $formattedSalesAmount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesRegion = salesDataRegion.sumOf { it.Total_Profit.toDouble() }
            val entriesRegion = salesDataRegion.map {
                PieEntry(it.Total_Profit, it.Manager_Name)
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
fun OrdersByManager(salesDataRegion: List<ProfitByManager>, totalOrders: Int) {
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

                setCenterText("Total Orders: $totalOrders")
                setCenterTextTypeface(quickSandTypeface)
                setEntryLabelTypeface(quickSandTypeface)
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(
                        e: com.github.mikephil.charting.data.Entry?,
                        h: Highlight?
                    ) {
                        if (e is PieEntry && e.label != null) {
                            val ordersAmount = e.value.toInt()
                            val selectedSegment = e.label
                            Toast.makeText(
                                it,
                                "$selectedSegment Orders: $ordersAmount",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesRegion = salesDataRegion.sumOf { it.Total_Orders }
            val entriesRegion = salesDataRegion.map {
                PieEntry(it.Total_Orders.toFloat(), it.Manager_Name)
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

@SuppressLint("InvalidColorHexValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerSalesBarChart(profitByManagerYear: List<ProfitByManagerYear>) {
    val managerNames = profitByManagerYear
        .map { it.Manager_Name }
        .distinct()
        .sorted()
    var selectedManager by remember { mutableStateOf(managerNames.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = selectedManager ?: "",
            onValueChange = { },
            label = { Text("Select Manager") },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Black)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners and background color
                .padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFEEEEEE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        // Customize dropdown menu with rounded corners
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners for the menu
        ) {
            managerNames.forEach { managerName ->
                DropdownMenuItem(
                    text = { Text(managerName) },
                    onClick = {
                        selectedManager = managerName
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEEEEEE)) // Background color for each item
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Column(
        modifier = Modifier.padding(0.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Based on Category",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { barChart ->
                val categoriesByYear = profitByManagerYear
                    .filter { it.Manager_Name == selectedManager }
                    .groupBy { it.Category }
                    .mapValues { (_, items) ->
                        items.groupBy { it.Year }
                            .mapValues { (_, yearItems) ->
                                yearItems.sumOf { it.Total_Sales.toInt() }
                            }
                    }

                val barDataSets = mutableListOf<BarDataSet>()
                val years = categoriesByYear.values.flatMap { it.keys }.distinct().sorted()
                val categories = categoriesByYear.keys.toList()

                categories.forEachIndexed { categoryIndex, category ->
                    val entries = years.mapIndexed { yearIndex, year ->
                        BarEntry(
                            yearIndex.toFloat(),
                            categoriesByYear[category]?.get(year)?.toFloat() ?: 0f
                        )
                    }

                    val colors = listOf(
                        android.graphics.Color.parseColor("#488f31"), // Warna Hijau (warna utama)
                        android.graphics.Color.parseColor("#f9a160"), // Oranye (lebih kuat, kontras)
                        android.graphics.Color.parseColor("#de425b")  // Merah (aksen kontras)
                    )

                    val dataSet = BarDataSet(entries, category)
                    dataSet.color = colors[categoryIndex % colors.size]
                    dataSet.valueFormatter = CustomValueFormatter()
                    barDataSets.add(dataSet)
                }

                val barData = BarData(barDataSets as List<IBarDataSet>?)

                val groupSpace = 0.15f
                val barSpace = 0.01f
                val barWidth = 0.275f
                barData.barWidth = barWidth

                barChart.data = barData
                barChart.xAxis.axisMinimum = -0.5f
                barChart.xAxis.axisMaximum = years.size.toFloat()
                barChart.groupBars(0f, groupSpace, barSpace)

                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(years.map { it.toString() })
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.granularity = 1f
                barChart.xAxis.setCenterAxisLabels(true)
                barChart.xAxis.setAxisMinimum(0f)
                barChart.axisRight.isEnabled = false

                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.animateXY(1000, 1000, Easing.EaseInBounce, Easing.EaseOutBounce)

                barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        val year = years[h?.x?.toInt() ?: 0]
                        val category = categories[h?.dataSetIndex ?: 0]
                        val sales = e?.y ?: 0f
                        val formatter = DecimalFormat("#,###")
                        val formattedSalesAmount = "$" + formatter.format(sales)

                        Toast.makeText(barChart.context, "$selectedManager $category Sales in $year: $formattedSalesAmount",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onNothingSelected() {}
                })

                barChart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )
    }
}

@SuppressLint("InvalidColorHexValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerProfitBarChart(profitByManagerYear: List<ProfitByManagerYear>) {
    val managerNames = profitByManagerYear
        .map { it.Manager_Name }
        .distinct()
        .sorted()
    var selectedManager by remember { mutableStateOf(managerNames.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = selectedManager ?: "",
            onValueChange = { },
            label = { Text("Select Manager") },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Black)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners and background color
                .padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFEEEEEE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        // Customize dropdown menu with rounded corners
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners for the menu
        ) {
            managerNames.forEach { managerName ->
                DropdownMenuItem(
                    text = { Text(managerName) },
                    onClick = {
                        selectedManager = managerName
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEEEEEE)) // Background color for each item
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Column(
        modifier = Modifier.padding(0.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Based on Category",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { barChart ->
                val categoriesByYear = profitByManagerYear
                    .filter { it.Manager_Name == selectedManager }
                    .groupBy { it.Category }
                    .mapValues { (_, items) ->
                        items.groupBy { it.Year }
                            .mapValues { (_, yearItems) ->
                                yearItems.sumOf { it.Total_Profit.toInt() }
                            }
                    }

                val barDataSets = mutableListOf<BarDataSet>()
                val years = categoriesByYear.values.flatMap { it.keys }.distinct().sorted()
                val categories = categoriesByYear.keys.toList()

                categories.forEachIndexed { categoryIndex, category ->
                    val entries = years.mapIndexed { yearIndex, year ->
                        BarEntry(
                            yearIndex.toFloat(),
                            categoriesByYear[category]?.get(year)?.toFloat() ?: 0f
                        )
                    }

                    val colors = listOf(
                        android.graphics.Color.parseColor("#488f31"), // Warna Hijau (warna utama)
                        android.graphics.Color.parseColor("#f9a160"), // Oranye (lebih kuat, kontras)
                        android.graphics.Color.parseColor("#de425b")  // Merah (aksen kontras)
                    )

                    val dataSet = BarDataSet(entries, category)
                    dataSet.color = colors[categoryIndex % colors.size]
                    dataSet.valueFormatter = CustomValueFormatter()
                    barDataSets.add(dataSet)
                }

                val barData = BarData(barDataSets as List<IBarDataSet>?)

                val groupSpace = 0.15f
                val barSpace = 0.01f
                val barWidth = 0.275f
                barData.barWidth = barWidth

                barChart.data = barData
                barChart.xAxis.axisMinimum = -0.5f
                barChart.xAxis.axisMaximum = years.size.toFloat()
                barChart.groupBars(0f, groupSpace, barSpace)

                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(years.map { it.toString() })
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.granularity = 1f
                barChart.xAxis.setCenterAxisLabels(true)
                barChart.xAxis.setAxisMinimum(0f)
                barChart.axisRight.isEnabled = false

                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.animateXY(1000, 1000, Easing.EaseInBounce, Easing.EaseOutBounce)

                barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        val year = years[h?.x?.toInt() ?: 0]
                        val category = categories[h?.dataSetIndex ?: 0]
                        val profit = e?.y ?: 0f
                        val formatter = DecimalFormat("#,###")
                        val formattedProfitAmount = "$" + formatter.format(profit)

                        Toast.makeText(barChart.context, "$selectedManager $category Profit in $year: $formattedProfitAmount",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onNothingSelected() {}
                })

                barChart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )
    }
}

@SuppressLint("InvalidColorHexValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerOrdersBarChart(profitByManagerYear: List<ProfitByManagerYear>) {
    val managerNames = profitByManagerYear
        .map { it.Manager_Name }
        .distinct()
        .sorted()
    var selectedManager by remember { mutableStateOf(managerNames.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = selectedManager ?: "",
            onValueChange = { },
            label = { Text("Select Manager") },
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Black)
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners and background color
                .padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFEEEEEE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        // Customize dropdown menu with rounded corners
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFFFEEEEEE), shape = RoundedCornerShape(16.dp)) // Rounded corners for the menu
        ) {
            managerNames.forEach { managerName ->
                DropdownMenuItem(
                    text = { Text(managerName) },
                    onClick = {
                        selectedManager = managerName
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEEEEEE)) // Background color for each item
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
    Column(
        modifier = Modifier.padding(0.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Based on Category",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { barChart ->
                val categoriesByYear = profitByManagerYear
                    .filter { it.Manager_Name == selectedManager }
                    .groupBy { it.Category }
                    .mapValues { (_, items) ->
                        items.groupBy { it.Year }
                            .mapValues { (_, yearItems) ->
                                yearItems.sumOf { it.Total_Orders.toInt() }
                            }
                    }

                val barDataSets = mutableListOf<BarDataSet>()
                val years = categoriesByYear.values.flatMap { it.keys }.distinct().sorted()
                val categories = categoriesByYear.keys.toList()

                categories.forEachIndexed { categoryIndex, category ->
                    val entries = years.mapIndexed { yearIndex, year ->
                        BarEntry(
                            yearIndex.toFloat(),
                            categoriesByYear[category]?.get(year)?.toFloat() ?: 0f
                        )
                    }

                    val colors = listOf(
                        android.graphics.Color.parseColor("#488f31"), // Warna Hijau (warna utama)
                        android.graphics.Color.parseColor("#f9a160"), // Oranye (lebih kuat, kontras)
                        android.graphics.Color.parseColor("#de425b")  // Merah (aksen kontras)
                    )

                    val dataSet = BarDataSet(entries, category)
                    dataSet.color = colors[categoryIndex % colors.size]
                    barDataSets.add(dataSet)
                }

                val barData = BarData(barDataSets as List<IBarDataSet>?)

                val groupSpace = 0.15f
                val barSpace = 0.01f
                val barWidth = 0.275f
                barData.barWidth = barWidth

                barChart.data = barData
                barChart.xAxis.axisMinimum = -0.5f
                barChart.xAxis.axisMaximum = years.size.toFloat()
                barChart.groupBars(0f, groupSpace, barSpace)

                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(years.map { it.toString() })
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.granularity = 1f
                barChart.xAxis.setCenterAxisLabels(true)
                barChart.xAxis.setAxisMinimum(0f)
                barChart.axisRight.isEnabled = false

                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.animateXY(1000, 1000, Easing.EaseInBounce, Easing.EaseOutBounce)

                barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        val year = years[h?.x?.toInt() ?: 0]
                        val category = categories[h?.dataSetIndex ?: 0]
                        val orders = e?.y ?: 0f

                        Toast.makeText(barChart.context, "$selectedManager $category Orders in $year: $orders",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onNothingSelected() {}
                })

                barChart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )
    }
}

class CustomValueFormatter : ValueFormatter() {
    private val formatter = DecimalFormat("#,###")

    override fun getFormattedValue(value: Float): String {
        return "$" + formatter.format(value.toInt()) // Format angka menjadi "$1,000"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType")
@Composable
fun PieChartWithButtons(
    profitByManager: List<ProfitByManager>,
    profitByManagerYear: List<ProfitByManagerYear>,
    totalSales: Int,
    totalProfit: Int,
    totalOrders: Int
) {
    var selectedChart by remember { mutableStateOf("Sales") }

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
                                onClick = { selectedChart = "Sales" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Sales") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_point_of_sale_24),
                                    contentDescription = "Profit by Manager",
                                    tint = if (selectedChart == "Sales") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Profit" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Profit") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_attach_money_24),
                                    contentDescription = "Sales by Manager",
                                    tint = if (selectedChart == "Profit") Color.Black else Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { selectedChart = "Orders" },
                                modifier = Modifier
                                    .background(
                                        if (selectedChart == "Orders") Color(0xFFBDBDBD) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_shopping_cart_24),
                                    contentDescription = "Orders by Manager",
                                    tint = if (selectedChart == "Orders") Color.Black else Color.Gray
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
                            "Sales" -> {
                                Text(
                                    text = "Sales by Manager",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSand
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                SalesByManager(profitByManager, totalSales)

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sales Performance",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSand
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                ManagerSalesLineChart(profitByManagerYear)

                                Spacer(modifier = Modifier.height(32.dp))
                                ManagerSalesBarChart(profitByManagerYear)
                            }

                            "Profit" -> {
                                Text(
                                    text = "Profit by Manager",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                ProfitByManager(profitByManager, totalProfit)

                                Spacer(modifier = Modifier.height(32.dp))
                                ManagerProfitBarChart(profitByManagerYear)
                            }

                            "Orders" -> {
                                Text(
                                    text = "Orders by Manager",
                                    fontSize = 30.sp,
                                    color = BlueJC,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OrdersByManager(profitByManager, totalOrders)

                                Spacer(modifier = Modifier.height(32.dp))
                                ManagerOrdersBarChart(profitByManagerYear)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ManagerSalesLineChart(profitByManagerYear: List<ProfitByManagerYear>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
            }
        },
        update = { lineChart ->
            val managers = profitByManagerYear.map { it.Manager_Name }.distinct()
            val years = profitByManagerYear.map { it.Year }.distinct().sorted()
            val lineDataSets = mutableListOf<LineDataSet>()

            managers.forEachIndexed { index, manager ->
                val managerData = profitByManagerYear.filter { it.Manager_Name == manager }
                val totalSalesPerYear = years.map { year ->
                    val totalSalesForYear = managerData
                        .filter { it.Year == year } // Filter data berdasarkan tahun
                        .sumOf { it.Total_Sales.toInt() } // Jumlahkan total sales
                    Entry(year.toFloat(), totalSalesForYear.toFloat())
                }

                val dataSet = LineDataSet(totalSalesPerYear, manager)
                dataSet.color = when(index) {
                    0 -> android.graphics.Color.parseColor("#488f31")
                    1 -> android.graphics.Color.parseColor("#a8c162")
                    2 -> android.graphics.Color.parseColor("#f9a160")
                    3 -> android.graphics.Color.parseColor("#de425b")
                    else -> android.graphics.Color.parseColor("#000000") // Manager C - Merah
                }
                dataSet.valueFormatter = CustomValueFormatter()
                dataSet.valueTextColor = android.graphics.Color.BLACK
                dataSet.lineWidth = 2f
                dataSet.valueTextSize = 10f
                lineDataSets.add(dataSet)
            }

            val lineData = LineData(lineDataSets as List<ILineDataSet>?)

            lineChart.data = lineData
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(years.map { it.toString() })
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.granularity = 1f
            lineChart.description.isEnabled = false
            lineChart.animateY(1000, Easing.EaseInOutQuart)
            lineChart.invalidate() // Refresh chart
        },
        modifier = Modifier.fillMaxWidth().height(350.dp)
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Sales(viewModel: SalesViewModel = viewModel()) {
    val profitByManager by viewModel.profitByManager.collectAsState()
    val profitByManagerYear by viewModel.profitByManagerYear.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val totalSales = profitByManager.sumOf { it.Total_Sales.toInt() ?: 0 }
    val totalProfit = profitByManager.sumOf { it.Total_Profit.toInt() }
    val totalOrders = profitByManager.sumOf { it.Total_Orders.toInt() }
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
//            PieChartView(salesDataByRegion, salesDataByCategory, salesDataBySegment, salesDataBySubCategory)
                PieChartWithButtons(
                    profitByManager,
                    profitByManagerYear,
                    totalSales,
                    totalProfit,
                    totalOrders
                )
                Spacer(modifier = Modifier.height(8.dp)) // Ruang antara chart
                ManagerSalesLineChart(profitByManagerYear)
                Spacer(modifier = Modifier.height(4.dp))
                // Menggunakan LineChart di Android untuk menampilkan persentase perubahan
                AndroidView(
                    factory = { context ->
                        LineChart(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { lineChart ->
                        val years = listOf(2020, 2021, 2022, 2023) // Tahun
                        val categories = listOf("Manager A", "Manager B", "Manager C", "Manager D") // Nama Manajer
                        val salesData = listOf(
                            listOf(100f, 130f, 170f, 190f), // 2020 - 2023 untuk Manager A
                            listOf(110f, 140f, 160f, 180f), // 2020 - 2023 untuk Manager B
                            listOf(95f, 110f, 125f, 150f),  // 2020 - 2023 untuk Manager C
                            listOf(105f, 130f, 145f, 160f)  // 2020 - 2023 untuk Manager D
                        )

                        val lineDataSets = mutableListOf<LineDataSet>()

                        categories.forEachIndexed { index, category ->
                            val entries = years.mapIndexed { yearIndex, year ->
                                // Menghitung persentase perubahan dari tahun sebelumnya
                                val prevSales = if (yearIndex > 0) salesData[index][yearIndex - 1] else salesData[index][yearIndex]
                                val percentChange = if (prevSales > 0) (salesData[index][yearIndex] - prevSales) / prevSales * 100 else 0f
                                Entry(yearIndex.toFloat(), percentChange)
                            }

                            val dataSet = LineDataSet(entries, category)
                            dataSet.color = when(index) {
                                0 -> android.graphics.Color.parseColor("#488f31") // Manager A - Hijau
                                1 -> android.graphics.Color.parseColor("#f9a160") // Manager B - Oranye
                                2 -> android.graphics.Color.parseColor("#de425b") // Manager C - Merah
                                else -> android.graphics.Color.parseColor("#1e88e5") // Manager D - Biru
                            }
                            dataSet.valueTextColor = android.graphics.Color.BLACK
                            lineDataSets.add(dataSet)
                        }

                        val lineData = LineData(lineDataSets as List<ILineDataSet>?)

                        lineChart.data = lineData
                        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(years.map { it.toString() })
                        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                        lineChart.axisRight.isEnabled = false
                        lineChart.description.isEnabled = false
                        lineChart.animateY(1000, Easing.EaseInOutQuart)
                        lineChart.invalidate() // Refresh chart
                    },
                    modifier = Modifier.fillMaxWidth().height(350.dp)
                )

            }
        }
    }
}

class PercentValueFormatter(private val total: Float) : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val percentage = (value / total) * 100
        return String.format("%.1f%%", percentage)
    }
}