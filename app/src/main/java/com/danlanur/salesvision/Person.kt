package com.danlanur.salesvision

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

// Composable untuk Profile
@Composable
fun Sales(viewModel: SalesViewModel = viewModel()) {
    val salesDataByRegion by viewModel.salesDataByRegion.collectAsState()
    val salesDataByCategory by viewModel.salesDataByCategory.collectAsState()
    val salesDataBySegment by viewModel.salesDataBySegment.collectAsState()
    val salesDataBySubCategory by viewModel.salesDataBySubCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val totalSales = salesDataByRegion.sumOf { it.Total_Sales.toInt() ?: 0 }
    val totalProfit = salesDataByRegion.sumOf { it.Total_Profit.toInt() }
    val totalQuantity = salesDataBySegment.sumOf { it.Total_Quantity.toInt() }
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
                ChartWithButtons(
                    salesDataByRegion,
                    salesDataByCategory,
                    salesDataBySegment,
                    totalSales,
                    totalProfit,
                    totalQuantity
                )
                Spacer(modifier = Modifier.height(8.dp)) // Ruang antara chart
                SalesBySubCategory(salesDataBySubCategory)
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Gray)
//            Text(text = "Sales by Category", fontSize = 30.sp, color = Color.Blue)
//            Spacer(modifier = Modifier.height(16.dp))
//            PieChartView(salesDataByCategory)
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