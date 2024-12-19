package com.danlanur.salesvision

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.http.GET
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.SalesVisionTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.KeyStore.Entry


class SalesViewModels : ViewModel() {
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

@Composable
fun ChartWithButtons(salesDataRegion: List<SalesDataRegion>, totalQuantity: Float) {
    var selectedChart by remember { mutableStateOf("PieChart") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tombol-tombol di atas chart
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Wrapper untuk ketiga tombol dengan RoundedCornerShape
            Surface(
                modifier = Modifier.wrapContentWidth(),
                shape = RoundedCornerShape(20.dp), // Rounded untuk seluruh container
                color = Color(0xFFEEEEEE), // Warna abu-abu terang untuk container
//                elevation = 3.dp // Sedikit bayangan agar terlihat modern
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), // Padding dalam container
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Tombol 1 (Sales by Region)
                    IconButton(
                        onClick = { selectedChart = "PieChart" },
                        modifier = Modifier
                            .background(
                                if (selectedChart == "PieChart") Color(0xFFBDBDBD) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp) // Rounded untuk tombol
                            )
                            .padding(6.dp) // Padding dalam tombol agar lebih rapi
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place, // Ikon untuk "Sales by Region"
                            contentDescription = "Sales by Region",
                            tint = if (selectedChart == "PieChart") Color.Black else Color.Gray
                        )
                    }

                    // Spacer antar tombol
                    Spacer(modifier = Modifier.width(6.dp))

                    // Tombol 2 (Sales by Category)
                    IconButton(
                        onClick = { selectedChart = "BarChart" },
                        modifier = Modifier
                            .background(
                                if (selectedChart == "BarChart") Color(0xFFBDBDBD) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.List, // Ikon untuk "Sales by Category"
                            contentDescription = "Sales by Category",
                            tint = if (selectedChart == "BarChart") Color.Black else Color.Gray
                        )
                    }

                    // Spacer antar tombol
                    Spacer(modifier = Modifier.width(6.dp))

                    // Tombol 3 (Sales by Segment)
                    IconButton(
                        onClick = { selectedChart = "LineChart" },
                        modifier = Modifier
                            .background(
                                if (selectedChart == "LineChart") Color(0xFFBDBDBD) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,   // Ikon untuk "Sales by Segment"
                            contentDescription = "Sales by Segment",
                            tint = if (selectedChart == "LineChart") Color.Black else Color.Gray
                        )
                    }
                }
            }
        }




        // Teks judul


        // Kotak frame untuk chart
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                when (selectedChart) {
                    "PieChart" -> {
                        Text(text = "Sales by Segment", fontSize = 30.sp, color = BlueJC, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        SalesByRegion(salesDataRegion, totalQuantity)
                    }
                    "BarChart" -> {
                        // Tempatkan BarChart lainnya di sini
                        Text("Bar Chart Placeholder") // Ganti dengan chart yang sesuai
                    }
                    "LineChart" -> {
                        // Tempatkan LineChart lainnya di sini
                        Text("Line Chart Placeholder") // Ganti dengan chart yang sesuai
                    }
                }
            }
            // Menampilkan chart sesuai dengan chart yang dipilih


        }
    }
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
}

@Composable
fun SalesByRegion(salesDataRegion: List<SalesDataRegion>, totalQuantity: Float) {
    AndroidView(
        modifier = Modifier.size(350.dp),
        factory = {
            PieChart(it).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setEntryLabelColor(android.graphics.Color.BLACK)

                setCenterTextSize(20f)
                setCenterTextColor(android.graphics.Color.BLACK)
                setCenterText("Total Quantity: $totalQuantity")

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(
                        e: com.github.mikephil.charting.data.Entry?,
                        h: Highlight?
                    ) {
                        if (e is PieEntry && e.label != null) {
                            val salesAmount = e.value
                            val selectedSegment = e.label
                            Toast.makeText(it, "$selectedSegment Sales: $${salesAmount.toInt()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            val totalSalesSegment = salesDataRegion.sumOf { it.Total_Sales.toDouble() }
            val entriesSegment = salesDataRegion.map {
                PieEntry(it.Total_Sales, it.Region)
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
}

@Composable
fun Dashboard(viewModel: SalesViewModel = viewModel()) {
    val salesDataByRegion by viewModel.salesDataByRegion.collectAsState()

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
            ChartWithButtons(salesDataByRegion, 4000.toFloat())

            Spacer(modifier = Modifier.height(32.dp)) // Ruang antara chart

//            Text(text = "Sales by Category", fontSize = 30.sp, color = Color.Blue)
//            Spacer(modifier = Modifier.height(16.dp))
//            PieChartView(salesDataByCategory)
        }
    }
}