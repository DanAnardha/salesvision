package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface ApiService {
    @GET("api/sales_by_region")
    suspend fun getSalesByRegion(): List<SalesDataRegion>

    @GET("api/sales_by_category")
    suspend fun getSalesByCategory(): List<SalesDataCategory>

    @GET("api/sales_by_segment")
    suspend fun getSalesBySegment(): List<SalesDataSegment>

    @GET("api/sales_by_subcategory")
    suspend fun getSalesBySubCategory(): List<SalesDataSubCategory>
}

object RetrofitInstance {
    private const val BASE_URL = "https://a591-103-178-12-228.ngrok-free.app/"
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class SalesViewModel : ViewModel() {
    private val _salesDataByRegion = MutableStateFlow<List<SalesDataRegion>>(emptyList())
    val salesDataByRegion = _salesDataByRegion.asStateFlow()
    private val _salesDataByCategory = MutableStateFlow<List<SalesDataCategory>>(emptyList())
    val salesDataByCategory = _salesDataByCategory.asStateFlow()
    private val _salesDataBySegment = MutableStateFlow<List<SalesDataSegment>>(emptyList())
    val salesDataBySegment = _salesDataBySegment.asStateFlow()
    private val _salesDataBySubCategory = MutableStateFlow<List<SalesDataSubCategory>>(emptyList())
    val salesDataBySubCategory = _salesDataBySubCategory.asStateFlow()

    init {
        loadSalesData()
    }

    private fun loadSalesData() {
        viewModelScope.launch {
            try {
                val responseRegion = RetrofitInstance.api.getSalesByRegion()
                _salesDataByRegion.value = responseRegion
                Log.d("SalesViewModel", "Sales Data Region: $responseRegion")

                val responseCategory = RetrofitInstance.api.getSalesByCategory()
                _salesDataByCategory.value = responseCategory
                Log.d("SalesViewModel", "Sales Data by Category: $responseCategory")

                val responseSegment = RetrofitInstance.api.getSalesBySegment()
                _salesDataBySegment.value = responseSegment
                Log.d("SalesViewModel", "Sales Data by Segment: $responseSegment")

                val responseSubCategory = RetrofitInstance.api.getSalesBySubCategory()
                _salesDataBySubCategory.value = responseSubCategory
                Log.d("SalesViewModel", "Sales Data by Sub-Category: $responseSubCategory")

            } catch (e: Exception) {
                Log.e("SalesViewModel", "Error loading sales data", e)
            }
        }
    }
}

// Composable untuk Profile
@Composable
fun Sales(viewModel: SalesViewModel = viewModel()) {
    val salesDataByRegion by viewModel.salesDataByRegion.collectAsState()
    val salesDataByCategory by viewModel.salesDataByCategory.collectAsState()
    val salesDataBySegment by viewModel.salesDataBySegment.collectAsState()
    val salesDataBySubCategory by viewModel.salesDataBySubCategory.collectAsState()

    Box(modifier = Modifier
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
            PieChartView(salesDataByRegion, salesDataByCategory, salesDataBySegment, salesDataBySubCategory)

            Spacer(modifier = Modifier.height(32.dp)) // Ruang antara chart

//            Text(text = "Sales by Category", fontSize = 30.sp, color = Color.Blue)
//            Spacer(modifier = Modifier.height(16.dp))
//            PieChartView(salesDataByCategory)
        }
    }
}



@Composable
fun PieChartView(salesDataRegion: List<SalesDataRegion>, salesDataCategory: List<SalesDataCategory>, salesDataSegment: List<SalesDataSegment>, salesDataSubCategory: List<SalesDataSubCategory>) {
    if (salesDataRegion.isEmpty() && salesDataCategory.isEmpty() && salesDataSegment.isEmpty() && salesDataSubCategory.isEmpty()) {
        Text(text = "No data available")
        return
    }
//    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Sales by Region", fontSize = 30.sp, color = BlueJC)
    Spacer(modifier = Modifier.height(16.dp))
    val context = LocalContext.current
    val totalProfit = salesDataRegion.sumOf { it.Total_Profit.toInt() }
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)

                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)
                setCenterText("Total Profit: $$totalProfit")

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry) {
                            val salesAmount = e.value
                            val selectedRegion = e.label

                            val selectedDataRegion = salesDataRegion.find { it.Region == selectedRegion }
                            val profitAmount = selectedDataRegion?.Total_Profit ?: 0f

                            Toast.makeText(context, "${selectedDataRegion?.Region} Sales: $${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onNothingSelected() {
                    }
                })
            }
        },
        update = { chart ->
            val totalSalesRegion = salesDataRegion.sumOf { it.Total_Sales.toDouble() }
            val entriesRegion = salesDataRegion.map {
                PieEntry(it.Total_Sales, it.Region)
            }

            val dataSet = PieDataSet(entriesRegion, "Sales by Region").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#E69F00"), // Kuning oranye
                    android.graphics.Color.parseColor("#56B4E9"), // Biru muda
                    android.graphics.Color.parseColor("#009E73"), // Hijau teal
                    android.graphics.Color.parseColor("#F0E442"), // Kuning cerah
                    android.graphics.Color.parseColor("#0072B2"), // Biru
                    android.graphics.Color.parseColor("#D55E00"), // Oranye gelap
                    android.graphics.Color.parseColor("#CC79A7")  // Merah muda
                )
                sliceSpace = 3f
                selectionShift = 5f
                valueFormatter = PercentValueFormatter(totalSalesRegion.toFloat())
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(30.dp))
    Text(text = "Sales by Category", fontSize = 30.sp, color = BlueJC)
    Spacer(modifier = Modifier.height(16.dp))

    val totalSales = salesDataRegion.sumOf { it.Total_Sales.toInt() }
    val totalQuantity = salesDataSegment.sumOf { it.Total_Quantity.toInt() }
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)
                setCenterText("Total Sales: $$totalSales")
                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry) {
                            val salesAmount = e.value
                            val selectedCategory = e.label
//
                            val selectedDataCategory = salesDataCategory.find { it.Category == selectedCategory }
//                            val profitAmount = selectedDataCategory?.Total_Profit ?: 0f
//
//                            Toast.makeText(context, "Category Sales: $${salesAmount.toInt()}, Total Profit: $${profitAmount.toInt()}", Toast.LENGTH_SHORT).show()
                            Log.d("Chart", "Selected Category: $selectedCategory")
                            salesDataCategory.forEach {
                                Log.d("Chart", "Category in Data: ${it.Category}")
                            }

                            Toast.makeText(context, "${selectedCategory.toString()} Sales: $${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
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

            val dataSetCategory = PieDataSet(entriesCategory, "Sales by Category").apply {
                // Skema warna orange-blue diverging
                colors = listOf(
                    android.graphics.Color.parseColor("#E69F00"), // Kuning oranye
                    android.graphics.Color.parseColor("#56B4E9"), // Biru muda
                    android.graphics.Color.parseColor("#009E73"), // Hijau teal
                    android.graphics.Color.parseColor("#F0E442"), // Kuning cerah
                    android.graphics.Color.parseColor("#0072B2"), // Biru
                    android.graphics.Color.parseColor("#D55E00"), // Oranye gelap
                    android.graphics.Color.parseColor("#CC79A7")  // Merah muda

                )

                sliceSpace = 3f
                selectionShift = 5f
                valueFormatter = PercentValueFormatter(totalSalesCategory.toFloat())
            }


            chart.data = PieData(dataSetCategory)
            chart.invalidate()
        }

    )
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(30.dp))
    Text(text = "Sales by Segment", fontSize = 30.sp, color = BlueJC)
    Spacer(modifier = Modifier.height(16.dp))

    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)

                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)
                setCenterText("Total Quantity: $totalQuantity")

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry && e.label != null) {

                            val salesAmount = e.value
                            val selectedSegment = e.label
//
                            val selectedDataSegment = salesDataSegment.find { it.Segment == selectedSegment }
//                            val profitAmount = selectedData?.Total_Profit ?: 0f
                            Log.d("Chart", "Selected Segment: $selectedSegment")
                            salesDataSegment.forEach {
                                Log.d("Chart", "Segment in Data: ${it.Segment}")
                            }
//                            Toast.makeText(context, "Category Sales: $${salesAmount.toInt()}, Total Profit: $${profitAmount.toInt()}", Toast.LENGTH_SHORT).show()
                            Toast.makeText(context, "${selectedSegment.toString()} Sales: $${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
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

            val dataSetSegment = PieDataSet(entriesSegment, "Sales by Segment").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#E69F00"), // Kuning oranye
                    android.graphics.Color.parseColor("#56B4E9"), // Biru muda
                    android.graphics.Color.parseColor("#009E73"), // Hijau teal
                    android.graphics.Color.parseColor("#F0E442"), // Kuning cerah
                    android.graphics.Color.parseColor("#0072B2"), // Biru
                    android.graphics.Color.parseColor("#D55E00"), // Oranye gelap
                    android.graphics.Color.parseColor("#CC79A7")  // Merah muda
                )
                sliceSpace = 3f
                selectionShift = 5f
                valueFormatter = PercentValueFormatter(totalSalesSegment.toFloat())
            }
            chart.data = PieData(dataSetSegment)
            chart.invalidate()
        }

    )
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(30.dp))
    Text(text = "Sales by Sub-Category", fontSize = 30.sp, color = BlueJC)
    Spacer(modifier = Modifier.height(16.dp))
    AndroidView(
        modifier = Modifier.height(500.dp).width(300.dp),
        factory = { context ->
            HorizontalBarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(true)
                setMaxVisibleValueCount(5)
                setPinchZoom(false)
                setDrawBarShadow(false)
                setFitBars(true)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawLabels(true)
                }
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is BarEntry) {
                            val salesAmount = e.y
                            val selectedSubCategory = e.data as String

                            val selectedDataSubCategory = salesDataSubCategory.find { it.SubCategory == selectedSubCategory }
                            Toast.makeText(context, "${selectedSubCategory} Sales: $${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesSubCategory = salesDataSubCategory.sumOf { it.Total_Sales.toDouble() }
            val entriesSubCategory = salesDataSubCategory.mapIndexed { index, it ->
                BarEntry(index.toFloat(), it.Total_Sales.toFloat(), it.SubCategory)
            }.reversed()

            val dataSet = BarDataSet(entriesSubCategory, "Sales by Sub-Category").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#E69F00"), // Kuning oranye
                    android.graphics.Color.parseColor("#56B4E9"), // Biru muda
                    android.graphics.Color.parseColor("#009E73"), // Hijau teal
                    android.graphics.Color.parseColor("#F0E442"), // Kuning cerah
                    android.graphics.Color.parseColor("#0072B2"), // Biru
                    android.graphics.Color.parseColor("#D55E00"), // Oranye gelap
                    android.graphics.Color.parseColor("#CC79A7")  // Merah muda
                )
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.9f
            }

            chart.data = barData

            val labels: ArrayList<String> = ArrayList(salesDataSubCategory.map { it.SubCategory })
            val xAxis = chart.xAxis
            xAxis.setLabelCount(labels.size, false)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.legend.isEnabled = false
            chart.axisLeft.isInverted()
            chart.invalidate()
        }
    )

    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}

class PercentValueFormatter(private val total: Float) : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val percentage = (value / total) * 100
        return String.format("%.1f%%", percentage)
    }
}