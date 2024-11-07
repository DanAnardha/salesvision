package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class SalesDataRegion(
    val Region: String,
    val Total_Sales: Float,
    val Total_Profit: Float
)

data class SalesDataCategory(
    val Category: String,
    val Total_Sales: Float
)

interface ApiService {
    @GET("api/sales_by_region")
    suspend fun getSalesByRegion(): List<SalesDataRegion>

    @GET("api/sales_by_category") // Ganti dengan endpoint yang benar
    suspend fun getSalesByCategory(): List<SalesDataCategory>
}

object RetrofitInstance {
    private const val BASE_URL = " https://4af7-114-5-110-167.ngrok-free.app/"
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
            PieChartView(salesDataByRegion, salesDataByCategory)

            Spacer(modifier = Modifier.height(32.dp)) // Ruang antara chart

//            Text(text = "Sales by Category", fontSize = 30.sp, color = Color.Blue)
//            Spacer(modifier = Modifier.height(16.dp))
//            PieChartView(salesDataByCategory)
        }
    }
}

@Composable
fun PieChartView(salesDataRegion: List<SalesDataRegion>, salesDataCategory: List<SalesDataCategory>) {
    if (salesDataRegion.isEmpty() && salesDataCategory.isEmpty()) {
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

                            val selectedData = salesDataRegion.find { it.Region == selectedRegion }
                            val profitAmount = selectedData?.Total_Profit ?: 0f

                            Toast.makeText(context, "Region Sales: $${salesAmount.toInt()}, Region Profit: $${profitAmount.toInt()}", Toast.LENGTH_SHORT).show()
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
                colors = ColorTemplate.MATERIAL_COLORS.toList()
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
                setCenterText("Total Sales: $$totalSales")

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry) {

//                            val salesAmount = e.value
//                            val selectedCategory = e.label
//
//                            val selectedData = salesDataCategory.find { it.Category == selectedCategory }
//                            val profitAmount = selectedData?.Total_Profit ?: 0f
//
//                            Toast.makeText(context, "Category Sales: $${salesAmount.toInt()}, Total Profit: $${profitAmount.toInt()}", Toast.LENGTH_SHORT).show()
                            val salesAmount = e.value
                            Toast.makeText(context, "Category Sales: ${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
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
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                sliceSpace = 3f
                selectionShift = 5f
                valueFormatter = PercentValueFormatter(totalSalesCategory.toFloat())
            }
            chart.data = PieData(dataSetCategory)
            chart.invalidate()
        }
    )
}

class PercentValueFormatter(private val total: Float) : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val percentage = (value / total) * 100
        return String.format("%.1f%%", percentage)
    }
}