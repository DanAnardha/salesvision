package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat.FocusDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.SalesVisionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SalesVisionTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .paint(
//                            painterResource(id = R.drawable.jclogin),
//                            contentScale = ContentScale.FillBounds
//                        )
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController)
                    // NavDrawer()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var navigationController = rememberNavController()
    var coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var context = LocalContext.current.applicationContext

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Box(modifier = Modifier
                    .background(BlueJC)
                    .fillMaxWidth()
                    .height(200.dp))
                {
                        Image(
                            painter = painterResource(id = R.drawable.forecasting),
                            contentDescription = "Sample Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                }
                Spacer(modifier = Modifier.height(10.dp))
                NavigationDrawerItem(label = { Text(text = "Dashboard", color = BlueJC)},
                    selected = false,
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "dashboard", tint = BlueJC)},
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Dashboard.screen){
                            popUpTo(0)
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Sales", color = BlueJC)},
                    selected = false,
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "dashboard", tint = BlueJC)},
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Sales.screen){
                            popUpTo(0)
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Settings", color = BlueJC)},
                    selected = false,
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "dashboard", tint = BlueJC)},
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Settings.screen){
                            popUpTo(0)
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Logout", color = BlueJC)},
                    selected = false,
                    icon = { Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "logout", tint = BlueJC)},
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true } // Ini akan membersihkan stack navigasi, sehingga pengguna tidak bisa kembali ke dashboard
                        }
                    })
            }
        },
        ) {
        Scaffold (
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                TopAppBar(title = { Text(text = "SalesVision")},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BlueJC,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Rounded.Menu, contentDescription = "MenuButton"
                            )
                        }
                    },
                    )
            }
        ) {
            NavHost(navController = navigationController,
                startDestination = Screens.Dashboard.screen){
                composable(Screens.Dashboard.screen) { Dashboard()}
                composable(Screens.Sales.screen) { Sales() }
                composable(Screens.Settings.screen) { Settings()}
            }
        }

    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current.applicationContext

    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.ime)
        .padding(horizontal = 50.dp, vertical = 140.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(value = username, onValueChange = {username = it},
            label = { Text(text = "Username")},
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedLeadingIconColor = BlueJC,
                unfocusedLeadingIconColor = BlueJC,
                focusedLabelColor = BlueJC,
                unfocusedLabelColor = BlueJC,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = BlueJC,
                unfocusedIndicatorColor = BlueJC,
                unfocusedPlaceholderColor = BlueJC
            ), leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        OutlinedTextField(value = password, onValueChange = {password = it},
            label = { Text(text = "Password")},
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedLeadingIconColor = BlueJC,
                unfocusedLeadingIconColor = BlueJC,
                focusedLabelColor = BlueJC,
                unfocusedLabelColor = BlueJC,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = BlueJC,
                unfocusedIndicatorColor = BlueJC,
                unfocusedPlaceholderColor = BlueJC
            ), leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            if (authenticate(username, password)){
                onLoginSuccess()
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
            }
        }, colors = ButtonDefaults.buttonColors(BlueJC),
            contentPadding = PaddingValues(start = 60.dp, end = 60.dp, top = 8.dp, bottom = 8.dp),
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Text(text = "Login", fontSize = 22.sp)
        }
    }
}

private fun authenticate(username: String, password: String): Boolean {
    val validUsername = ""
    val validPassword = ""
    return username == validUsername && password == validPassword
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo(0)
                }
            })
        }
        composable("main") {
            MainScreen(navController)
        }
    }
}