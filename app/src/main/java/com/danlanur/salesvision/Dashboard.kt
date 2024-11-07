package com.danlanur.salesvision

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.http.GET
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.SalesVisionTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

//data class User(
//    val id_pinjam: String,
//    val id_petugas: String,
//    val total_pinjam: Int
//)
//
//interface ApiService {
//    @GET("/api/users")
//    suspend fun getUsers(): List<User>
//}
//
//@Composable
//fun Dashboard() {
//    var users by remember { mutableStateOf(emptyList<User>()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    val coroutineScope = rememberCoroutineScope()
//
//    val retrofit = Retrofit.Builder()
//        .baseUrl("http://10.0.2.2:5000/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val apiService = retrofit.create(ApiService::class.java)
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            try {
//                users = apiService.getUsers()
//                Log.d("API Response", "Users: $users")
//            } catch (e: Exception) {
//                errorMessage = e.message
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .align(Alignment.Center),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "TES DASHBOARD", fontSize = 30.sp, color = BlueJC)
//
//            if (isLoading) {
//                CircularProgressIndicator()
//            } else if (errorMessage != null) {
//                Text(text = "Error: $errorMessage")
//            } else if (users.isEmpty()) {
//                Text(text = "Data tidak ditemukan")
//            } else {
//                Spacer(modifier = Modifier.height(10.dp))
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.White)
//                        .padding(16.dp)
//                ) {
//                    // Header
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = "ID Petugas",
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.weight(3f),
//                            color = BlueJC
//                        )
//                        Text(
//                            text = "ID Pinjam",
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.weight(3f),
//                            color = BlueJC
//                        )
//                        Text(
//                            text = "Total Pinjam",
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.weight(3f),
//                            color = BlueJC
//                        )
//                    }
//                    Divider(color = Color.Gray, thickness = 1.dp)
//
//                    // Data Rows
//                    LazyColumn {
//                        items(users) { user ->
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 4.dp)
//                                    .shadow(1.dp, shape = RoundedCornerShape(8.dp))
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .padding(8.dp)
//                                        .fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = user.id_petugas,
//                                        modifier = Modifier.weight(3f),
//                                        color = Color.DarkGray,
//                                        fontSize = 14.sp
//                                    )
//                                    Text(
//                                        text = user.id_pinjam,
//                                        modifier = Modifier.weight(3f),
//                                        color = Color.DarkGray,
//                                        fontSize = 14.sp
//                                    )
//                                    Text(
//                                        text = user.total_pinjam.toString(),
//                                        modifier = Modifier.weight(3f),
//                                        color = Color.DarkGray,
//                                        fontSize = 14.sp
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun Dashboard() {
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Center),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Dashboard", fontSize = 30.sp, color = BlueJC)
        }
    }
}