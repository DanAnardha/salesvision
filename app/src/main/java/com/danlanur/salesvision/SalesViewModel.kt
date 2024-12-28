package com.danlanur.salesvision

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/sales_by_region")
    suspend fun getSalesByRegion(): List<SalesDataRegion>

    @GET("api/sales_by_category")
    suspend fun getSalesByCategory(): List<SalesDataCategory>

    @GET("api/sales_by_segment")
    suspend fun getSalesBySegment(): List<SalesDataSegment>

    @GET("api/sales_by_subcategory")
    suspend fun getSalesBySubCategory(): List<SalesDataSubCategory>

    @GET("api/order_sales")
    suspend fun getOrderSalesPerDay(): List<OrderSalesPerDay>

    @GET("api/get_order_dates")
    suspend fun getOrderDates(): List<OrderDates>

    @GET("/api/sales_by_month")
    suspend fun getMonthlySalesData(): List<SalesByMonth>

    @GET("/api/sales_by_state")
    suspend fun getSalesByState(): List<SalesByState>

    @GET("api/order_by_shipmode")
    suspend fun getOrderByShipMode(): List<OrderShipMode>

    @GET("api/profit_by_region_per_month")
    suspend fun getProfitByRegion(): List<ProfitByRegion>

    @GET("api/profit_by_category_per_month")
    suspend fun getProfitByCategory(): List<ProfitByCategory>

    @GET("api/profit_by_segment_per_month")
    suspend fun getProfitBySegment(): List<ProfitBySegment>

    @GET("predict_forecast")
    suspend fun predictSales(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<PredictionResult>>

    @GET("predict_recom")
    suspend fun predictRecom(
        @Query("qty") qty: Float,
        @Query("unit_price") unitPrice: Float,
        @Query("freight_price") freightPrice: Float,
        @Query("comp_1") comp1: Float,
        @Query("product_score") productScore: Float,
    ): Response<RecomPrediction>
}

object RetrofitInstance {
    private const val BASE_URL = "https://3cf9-103-178-12-228.ngrok-free.app/"

    //    private const val BASE_URL = "https://10.0.2.2:5000/"
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
    private val _orderSalesDay = MutableStateFlow<List<OrderSalesPerDay>>(emptyList())
    val orderSalesPerDay = _orderSalesDay.asStateFlow()
    private val _salesByMonth = MutableStateFlow<List<SalesByMonth>>(emptyList())
    val salesByMonth = _salesByMonth.asStateFlow()
    private val _orderDates = MutableStateFlow<List<OrderDates>>(emptyList())
    val orderDates = _orderDates.asStateFlow()
    private val _salesByState = MutableStateFlow<List<SalesByState>>(emptyList())
    val salesByState = _salesByState.asStateFlow()
    private val _orderByShipMode = MutableStateFlow<List<OrderShipMode>>(emptyList())
    val orderByShipMode = _orderByShipMode.asStateFlow()
    private val _profitByCategory = MutableStateFlow<List<ProfitByCategory>>(emptyList())
    val profitByCategory = _profitByCategory.asStateFlow()
    private val _profitByRegion = MutableStateFlow<List<ProfitByRegion>>(emptyList())
    val profitByRegion = _profitByRegion.asStateFlow()
    private val _profitBySegment = MutableStateFlow<List<ProfitBySegment>>(emptyList())
    val profitBySegment = _profitBySegment.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadSalesData()
    }

    private fun loadSalesData() {
        viewModelScope.launch {
            _isLoading.value = true
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

                val responseSalesByMonth = RetrofitInstance.api.getMonthlySalesData()
                _salesByMonth.value = responseSalesByMonth
                Log.d("SalesViewModel", "Monthly Sales Data: $responseSalesByMonth")

                val responseSalesPerDay = RetrofitInstance.api.getOrderSalesPerDay()
                _orderSalesDay.value = responseSalesPerDay
                Log.d("SalesViewModel", "Sales Data: $responseSubCategory")

                val responseOrderDates = RetrofitInstance.api.getOrderDates()
                _orderDates.value = responseOrderDates
                Log.d("SalesViewModel", "Dates: $responseOrderDates")

                val responseSalesState = RetrofitInstance.api.getSalesByState()
                _salesByState.value = responseSalesState
                Log.d("SalesViewModel", "States: $responseSalesState")

                val responseShipMode = RetrofitInstance.api.getOrderByShipMode()
                _orderByShipMode.value = responseShipMode
                Log.d("SalesViewModel", "Ship Mode: $responseShipMode")

                val responseProfitRegion = RetrofitInstance.api.getProfitByRegion()
                _profitByRegion.value = responseProfitRegion

                val responseProfitCategory = RetrofitInstance.api.getProfitByCategory()
                _profitByCategory.value = responseProfitCategory
                Log.d("SalesViewModel", "Category Profit: $responseProfitCategory")

                val responseProfitSegment = RetrofitInstance.api.getProfitBySegment()
                _profitBySegment.value = responseProfitSegment

            } catch (e: Exception) {
                Log.e("SalesViewModel", "Error loading sales data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000)
            loadSalesData()
            _isLoading.value = false
        }
    }
}