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
    val Total_Quantity: Float
)