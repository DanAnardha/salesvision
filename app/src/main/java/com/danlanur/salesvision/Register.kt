package com.danlanur.salesvision

import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.danlanur.salesvision.ui.theme.BlueJC
import org.json.JSONObject
import retrofit2.Call

fun registerUser(
    username: String,
    password: String,
    email: String,
    onResult: (Boolean, String) -> Unit
) {
    val apiService = RetrofitInstance.api
    val requestBody = JSONObject().apply {
        put("username", username)
        put("email", email)
        put("password", password)
    }
    val request = RegisterRequest(username, password, email)

    apiService.register(request).enqueue(object : retrofit2.Callback<RegisterResponse> {
        override fun onResponse(
            call: Call<RegisterResponse>,
            response: retrofit2.Response<RegisterResponse>
        ) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("Register", "Response: ${responseBody?.status} - ${responseBody?.message}")
                if (responseBody?.status == "success") {
                    onResult(true, "Registration successful")
                } else {
                    onResult(false, responseBody?.message ?: "Registration failed")
                }
            } else {
                onResult(false, "Server error: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
            onResult(false, t.message ?: "Network error")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

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
                painter = painterResource(id = R.drawable.baseline_app_registration_24), // Replace with your icon
                contentDescription = "Register Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Create Account",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Please fill in the details to register",
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
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
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
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it },
                label = { Text("Confirm Password") },
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
                    .padding(bottom = 16.dp)
            )

            // Error message for invalid inputs
            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            val context = LocalContext.current
            Button(
                onClick = {
                    // Validate fields before submitting
                    if (username.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty() || confirmPassword.value.isEmpty()) {
                        errorMessage.value = "All fields must be filled out."
                    } else if (password.value != confirmPassword.value) {
                        errorMessage.value = "Passwords do not match."
                    } else {
                        // Proceed with registration logic
                        errorMessage.value = ""
                        registerUser(
                            username.value,
                            password.value,
                            email.value
                        ) { success, message ->
                            if (success) {
                                Log.d("Register", "Navigating to login page after registration success")
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
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
                Text(text = "Register", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    navController.navigate("login") // Pindah ke halaman register
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Have an account? Login here",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
