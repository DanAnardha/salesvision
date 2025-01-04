package com.danlanur.salesvision

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomerDetailScreen(topCustomers: List<TopCustomers>){
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Customer Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(topCustomers) { customer ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "Rank: ${customer.Rank}")
                Text(text = "Name: ${customer.Name}")
                Text(text = "Last Order: ${customer.Last_Order}")
                Text(text = "Total Orders: ${customer.Total_Orders}")
                Text(text = "Total Profit: $${customer.Total_Profit}")
                Text(text = "Total Sales: $${customer.Total_Sales}")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
