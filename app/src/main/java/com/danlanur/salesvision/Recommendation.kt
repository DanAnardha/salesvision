package com.danlanur.salesvision

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand

@SuppressLint("RememberReturnType")
@Composable
fun RecomAndHistory() {
    var selectedMenu by remember { mutableStateOf("Prediction") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Surface(
                modifier = Modifier.wrapContentWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEEEEEE),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), // Padding dalam container
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Prediction Button - Ganti dengan teks
                    Text(
                        text = "Prediction",
                        modifier = Modifier
                            .background(
                                if (selectedMenu == "Prediction") Color(0xFFBDBDBD) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                            .clickable { selectedMenu = "Prediction" },
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMenu == "Prediction") Color.Black else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // History Button - Ganti dengan teks
                    Text(
                        text = "History",
                        modifier = Modifier
                            .background(
                                if (selectedMenu == "History") Color(0xFFBDBDBD) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                            .clickable { selectedMenu = "History" },
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMenu == "History") Color.Black else Color.Gray
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                    contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (selectedMenu) {
                    "Prediction" -> {
                        Text(
                            text = "Sales Prediction", fontSize = 30.sp, color = BlueJC,
                            fontWeight = FontWeight.Bold, fontFamily = QuickSand
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // SalesByPrediction() or relevant content for prediction
                    }
                    "History" -> {
                        Text(
                            text = "History", fontSize = 30.sp, color = BlueJC,
                            fontWeight = FontWeight.Bold, fontFamily = QuickSand
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // SalesHistory() or relevant content for history
                    }
                }
            }
        }
    }
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
}


@Composable
fun Recommendation(viewModel: SalesViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
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
                RecomAndHistory()
            }
        }
    }
}