package com.danlanur.salesvision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.SalesVisionTheme

@Preview(showBackground = true)
@Composable
fun Dashboard() {
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "KONTOL!", fontSize = 30.sp, color = BlueJC)
        }
    }
}

@Preview
@Composable
fun DashboardPreview() {
    SalesVisionTheme {
        Dashboard()
    }
}