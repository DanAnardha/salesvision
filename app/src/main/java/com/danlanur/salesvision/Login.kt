package com.danlanur.salesvision

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.danlanur.salesvision.ui.theme.BlueJC
import retrofit2.Call

fun saveTokenToPreferences(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("auth_token", token)
    editor.apply() //
}

fun clearTokenFromPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("auth_token")
    editor.apply() // Hapus token
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("auth_token", null)
    return token != null
}

fun loginUser(context: Context, username: String, password: String, onResult: (Boolean, String) -> Unit) {
    val apiService = RetrofitInstance.api
    val request = LoginRequest(username, password)

    apiService.login(request).enqueue(object : retrofit2.Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
            if (response.isSuccessful && response.body()?.status == "success") {
                val token = response.body()?.token ?: ""
                val userId = response.body()?.user_id ?: -1
                // Use the userId and token
                saveTokenToPreferences(context, token)
                onResult(true, "Login successful: Token: $token, User ID: $userId")
            } else {
                onResult(false, response.body()?.message ?: "Login failed")
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            onResult(false, t.message ?: "Network error")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    if (isUserLoggedIn(context)) {
        navController.navigate("dashboard")
        return
    }

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val primaryColor = BlueJC // BlueJC Primary
    val secondaryColor = Color(0xFFBBDEFB) // BlueJC Light
    val accentColor = Color(0xFF1976D2) // BlueJC Dark

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor)
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_login_24), // Replace with your icon
                contentDescription = "Login Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Welcome Back!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Please login to continue",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                trailingIcon = {
                    if (username.value.isNotEmpty()) {
                        IconButton(onClick = { username.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color(0xFF64B5F6),
                    focusedLabelColor = Color(0xFF64B5F6),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    // trailingIconColor = Color.White // Warna ikon penghapus
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )


            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            Button(
                onClick = {
                    loginUser(context, username.value, password.value) { success, message ->
                        if (success) {
                            navController.navigate("dashboard")
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = primaryColor
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 8.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(text = "Login", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    navController.navigate("register") // Pindah ke halaman register
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Don't have an account? Register here",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}