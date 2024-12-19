package com.danlanur.salesvision

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