package com.danlanur.salesvision

import android.graphics.Typeface
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CustomersByMonth(customersByMonth: List<CustomersByMonth>, totalCustomers: Int) {
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val contextTypeface = LocalContext.current
            val quickSandTypeface =
                Typeface.createFromAsset(contextTypeface.assets, "font/quicksand_regular.ttf")

            Text(
                text = "Customers",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Customers count is based by month.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val entriesProfit = customersByMonth.mapIndexed { index, result ->
                val date =
                    SimpleDateFormat(
                        "yyyy-MM",
                        Locale.getDefault()
                    ).parse(result.Month) // Parse "2024-10"
                val formattedDate =
                    date?.let { dateFormat.format(it) } // Format menjadi "Oct 2024"
                Entry(index.toFloat(), result.Total_Customers.toFloat())
            }
            AndroidView(
                modifier = Modifier.size(350.dp),
                factory = { context ->
                    LineChart(context).apply {
                        val dataSet = LineDataSet(entriesProfit, "Customers").apply {
                            color = android.graphics.Color.parseColor("#E69F00")
                            setDrawCircles(false)
                            android.graphics.Color.parseColor("#CC79A7")
                            lineWidth = 2f
                            setDrawValues(true)
                            valueTextSize = 10f
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
//            xAxis.setLabelCount(predictionResults.size, true)
                        xAxis.setLabelCount(8, true)  // Show 5 labels by default
                        xAxis.setGranularity(1f)
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            customersByMonth.map {
                                val date = SimpleDateFormat(
                                    "yyyy-MM",
                                    Locale.getDefault()
                                ).parse(it.Month)
                                dateFormat.format(date)
                            }
                        )
                        axisLeft.isEnabled = true
                        axisRight.isEnabled = false
                        xAxis.isEnabled = true
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
                                        customersByMonth[index].Month // Access the month
                                    val customers = customersByMonth[index].Total_Customers
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
                                        "$formattedMonth Customers: $customers",
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
            val maxCustomers = customersByMonth.maxOfOrNull { it.Total_Customers } ?: 0.0f
            val minCustomers = customersByMonth.minOfOrNull { it.Total_Customers } ?: 0.0f
            val averageCustomers = customersByMonth.map { it.Total_Customers }.average()
            val totalCustomers = customersByMonth.fold(0.0f) { sum, prediction ->
                sum + prediction.Total_Customers
            }
            GetCustomersStats(
                maxCustomers.toInt(),
                minCustomers.toInt(),
                averageCustomers.toInt(),
                totalCustomers.toInt()
            )
        }
    }
}

@Composable
fun TopCustomersTable(topCustomers: List<TopCustomers>, navController: NavController) {
    var showAll by remember { mutableStateOf(false) }
    val dataToDisplay = if (showAll) topCustomers else topCustomers.take(5)
    val topCustomersJson = Gson().toJson(topCustomers)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, top = 16.dp),
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
                text = "Top Customers",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(445.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rank",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Name",
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
                            text = "Sales",
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
                            text = item.Rank.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.Name,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = formattedProfit,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = formattedSales,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    Divider()
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("customerDetails?topCustomers=$topCustomersJson") },  //showAll = !showAll },
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


@Composable
fun GetCustomersStats(max: Int, min: Int, avg: Int, total: Int) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Customers Statistics",
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
                text = "Highest Customers:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$max",
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
                text = "Lowest Customers:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$min",
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
                text = "Average Customers:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$avg",
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
                text = "Total Customers:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun OrderCountDistribution(orderDistribution: List<OrderDistribution>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, top = 16.dp, bottom = 12.dp),
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
                text = "Order Distribution",
                fontSize = 30.sp,
                color = BlueJC,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSand
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This histogram displays the distribution of customers based on the number of orders they have made. The X-axis represents the number of orders, while the Y-axis shows the number of unique customers who have placed that many orders.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val entries = orderDistribution.mapIndexed { index, order ->
                BarEntry(order.Order_Count.toFloat(), order.Unique_Customers.toFloat())
            }

            val dataSet = BarDataSet(entries, "Unique Customers per Order Count")
            dataSet.color = android.graphics.Color.parseColor("#E69F00")

            val barData = BarData(dataSet)
            AndroidView(
                modifier = Modifier.size(350.dp),
                factory = { context ->
                    BarChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        data = barData
                        description.isEnabled = false
                        setFitBars(true)
                        legend.isEnabled = false
                        xAxis.granularity = 1f
                        xAxis.setCenterAxisLabels(true)
                        xAxis.setAxisMinimum(0f)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        axisRight.isEnabled = false
                        setNoDataText("No Data Available")
                        animateXY(1000, 1000, Easing.EaseInBounce, Easing.EaseOutBounce)

                        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                // Show Toast with the selected bar's data
                                e?.let {
                                    val orderCount = it.x.toInt()
                                    val customerCount = it.y.toInt()
                                    Toast.makeText(
                                        context,
                                        "Order Count: $orderCount, Unique Customers: $customerCount",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onNothingSelected() {
                                // Handle case where nothing is selected (optional)
                            }
                        })
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Customer(navController: NavController, viewModel: SalesViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()

    val customersByMonth by viewModel.customersByMonth.collectAsState()
    val orderDistribution by viewModel.orderDistribution.collectAsState()
    val topCustomers by viewModel.topCustomers.collectAsState()
    val totalCustomers = customersByMonth.sumOf { it.Total_Customers.toInt() ?: 0 }
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
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomersByMonth(customersByMonth, totalCustomers)
                    TopCustomersTable(topCustomers, navController = navController)
                    OrderCountDistribution(orderDistribution)
                }
            }
        }
    }
}