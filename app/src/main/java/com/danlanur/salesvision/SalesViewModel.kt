package com.danlanur.salesvision

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    @GET("predict_forecast")
    suspend fun predictSales(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<PredictionResult>>
}

object RetrofitInstance {
    private const val BASE_URL = "https://451f-103-178-12-228.ngrok-free.app/"
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

                val responseSalesPerDay = RetrofitInstance.api.getOrderSalesPerDay()
                _orderSalesDay.value = responseSalesPerDay
                Log.d("SalesViewModel", "Sales Data by Sub-Category: $responseSubCategory")
            } catch (e: Exception) {
                Log.e("SalesViewModel", "Error loading sales data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}