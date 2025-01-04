package com.danlanur.salesvision

import java.time.LocalDate

data class SalesDataRegion(
    val Region: String,
    val Total_Sales: Float,
    val Total_Profit: Float
)

data class SalesDataCategory(
    val Category: String,
    val Total_Sales: Float
)

data class SalesDataSegment(
    val Segment: String,
    val Total_Sales: Float,
    val Total_Quantity: Float
)

data class SalesDataSubCategory(
    val SubCategory: String,
    val Total_Sales: Float
)

data class OrderSalesPerDay(
    val Order_Date: String,
    val Total_Sales: Float
)

data class PredictionResult(
    val ds: String,
    val yhat: Float
)

data class OrderDates(
    val earliest_order: String,
    val latest_order: String
)

data class RecomPrediction(
    val prediction: List<Double>
)

data class SalesByMonth(
    val Month: String,
    val Total_Sales: Float,
    val Total_Profit: Float,
    val Total_Quantity: Float,
    val Total_Order: Int,
    val Total_Customer: Int
)

data class SalesByState(
    val StateProvince: String,
    val Total_Sales: Float,
    val Total_Profit: Float,
    val Total_Quantity: Float
)

data class OrderShipMode(
    val Ship_Mode: String,
    val Total_Order: Int
)

data class ProfitByCategory(
    val Month: String,
    val Category: String,
    val Total_Profit: Float
)

data class ProfitByRegion(
    val Month: String,
    val Region: String,
    val Total_Profit: Float,
    var indexInOriginal: Int = -1
)

data class ProfitBySegment(
    val Month: String,
    val Segment: String,
    val Total_Profit: Float
)

data class ProfitByManager(
    val Manager_Name: String,
    val Total_Sales: Float,
    val Total_Profit: Float,
    val Total_Orders: Int
)

data class ProfitByManagerYear(
    val Manager_Name: String,
    val Total_Sales: Float,
    val Total_Profit: Float,
    val Total_Orders: Int,
    val Year: String,
    val Category: String
)

data class CustomersByMonth(
    val Month: String,
    val Total_Customers: Int
)

data class TopCustomers(
    val Last_Order: String,
    val Name: String,
    val Rank: Int,
    val Total_Orders: Int,
    val Total_Profit: Float,
    val Total_Sales: Float
)

data class TotalCustomer(
    val Total_Customers: Int,
    val Total_Orders: Int
)

data class OrderDistribution(
    val Order_Count: Int,
    val Unique_Customers: Int
)