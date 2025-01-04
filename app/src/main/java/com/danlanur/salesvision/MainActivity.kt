package com.danlanur.salesvision

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.danlanur.salesvision.ui.theme.BlueJC
import com.danlanur.salesvision.ui.theme.QuickSand
import com.danlanur.salesvision.ui.theme.SalesVisionTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // setting up the individual tabs
            val dashboardTab = TabBarItem(
                title = "Dashboard",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val managerTab = TabBarItem(
                title = "Manager",
                selectedIcon = Icons.Filled.AccountBox,
                unselectedIcon = Icons.Outlined.AccountBox
            )
            val customerTab = TabBarItem(
                title = "Customer",
                selectedIcon = Icons.Filled.AccountCircle,
                unselectedIcon = Icons.Outlined.AccountCircle
            )
            val utilsTab = TabBarItem(
                title = "More",
                selectedIcon = Icons.Filled.List,
                unselectedIcon = Icons.Outlined.List
            )
            val topCustomersTab = TabBarItem(
                title = "TopCustomers",
                selectedIcon = Icons.Filled.List,
                unselectedIcon = Icons.Outlined.List
            )

            // creating a list of all the tabs
            val tabBarItems = listOf(dashboardTab, managerTab, customerTab, utilsTab)
            var expanded by remember { mutableStateOf(false) }
            val context = LocalContext.current
            // creating our navController
            val navController = rememberNavController()
            val isLoginPage = remember { mutableStateOf(false) }

            SalesVisionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlueJC,
                    contentColor = BlueJC
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "SalesVision",
                                        style = TextStyle(
                                            fontSize = 20.sp, // Mengubah ukuran font
                                            fontWeight = FontWeight.Bold, // Mengubah berat font (tebal)
                                            fontFamily = QuickSand, // Mengubah jenis font, opsional
                                            color = Color.White // Mengubah warna teks
                                        )
                                    )
                                },
                                actions = {
                                    // Three dots button (overflow menu)
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "More options",
                                            tint = Color.White
                                        )
                                    }
                                    // Dropdown menu
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Logout") },
                                            onClick = {
                                                expanded = false
                                                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") {
                                                    popUpTo("login") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = BlueJC,
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                ),
                            )
                        },
                        bottomBar = {
                            if (!isLoginPage.value) {
                                TabView(tabBarItems, navController) // Menampilkan navbar jika bukan login
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "login",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            composable("login") {
                                isLoginPage.value = true
                                LoginScreen(navController = navController)
                            }
                            composable(dashboardTab.title) {
                                isLoginPage.value = false
                                Dashboard()
                            }
                            composable(managerTab.title) {
                                isLoginPage.value = false
                                Sales()
                            }
                            composable(customerTab.title) {
                                isLoginPage.value = false
                                Customer(navController = navController)
                            }
                            composable(utilsTab.title) {
                                isLoginPage.value = false
                                Recommendation()
                            }
                            composable("customerDetails?topCustomers={topCustomers}") { backStackEntry ->
                                val topCustomersJson =
                                    backStackEntry.arguments?.getString("topCustomers")
                                val topCustomersList = Gson().fromJson<List<TopCustomers>>(
                                    topCustomersJson,
                                    object : TypeToken<List<TopCustomers>>() {}.type
                                )
                                CustomerDetailScreen(topCustomers = topCustomersList)
                            }
                        }
                        // Print current destination to log
                        val currentRoute = navController.currentDestination?.route
                        Log.d("CurrentDestination", "Current route: $currentRoute")
                    }
                }
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedTabIndex =
        tabBarItems.indexOfFirst { it.title == currentBackStackEntry?.destination?.route }


    NavigationBar(
        containerColor = BlueJC,
        contentColor = Color.White
    ) {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    navController.navigate(tabBarItem.title) {
                        // Menghapus semua back stack yang tidak perlu, untuk menghindari duplikasi navigasi
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {
                    Text(
                        tabBarItem.title,
                        style = TextStyle(
                            fontSize = 13.sp, // Ganti ukuran font di sini
                            fontWeight = FontWeight.Bold, // Menambahkan tebal pada font, opsional
                            fontFamily = QuickSand// Menyelaraskan font dengan tema
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.DarkGray, // Warna ikon terpilih
                    unselectedIconColor = Color.White.copy(alpha = 0.8f), // Warna ikon tidak terpilih
                    selectedTextColor = Color.White, // Warna teks terpilih
                    unselectedTextColor = Color.White.copy(alpha = 0.8f), // Warna teks tidak terpilih
                    indicatorColor = Color.LightGray
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

@Composable
fun MoreView() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Thing 1")
        Text("Thing 2")
        Text("Thing 3")
        Text("Thing 4")
        Text("Thing 5")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SalesVisionTheme() {
        MoreView()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
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
                Box(
                    modifier = Modifier
                        .background(BlueJC)
                        .fillMaxWidth()
                        .height(200.dp)
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.forecasting),
                        contentDescription = "Sample Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                val backPressedOnce = remember { mutableStateOf(false) }

                BackHandler {
                    if (backPressedOnce.value) {
                        (context as Activity).finish() // Menutup aplikasi
                    } else {
                        backPressedOnce.value = true
                        Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT)
                            .show()

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000) // 2 detik untuk memberi waktu
                            backPressedOnce.value = false
                        }
                    }
                }

                NavigationDrawerItem(label = { Text(text = "Dashboard", color = BlueJC) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "dashboard",
                            tint = BlueJC
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Dashboard.screen) {
                            popUpTo(0)
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Manager & Customer", color = BlueJC) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "dashboard",
                            tint = BlueJC
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Sales.screen) {
                            popUpTo(Screens.Sales.screen) { inclusive = true }
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Utilities", color = BlueJC) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "dashboard",
                            tint = BlueJC
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Settings.screen) {
                            popUpTo(Screens.Settings.screen) { inclusive = true }
                        }
                    })
                NavigationDrawerItem(label = { Text(text = "Logout", color = BlueJC) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "logout",
                            tint = BlueJC
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("login") {
                                inclusive = true
                            } // Ini akan membersihkan stack navigasi, sehingga pengguna tidak bisa kembali ke dashboard
                        }
                    })
            }
        },
    ) {
        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                TopAppBar(
                    title = { Text(text = "SalesVision") },
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
            NavHost(
                navController = navigationController,
                startDestination = Screens.Dashboard.screen
            ) {
                composable(Screens.Dashboard.screen) { Dashboard() }
                composable(Screens.Sales.screen) { Sales() }
                composable(Screens.Settings.screen) { Recommendation() }
            }
        }

    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
            .padding(horizontal = 50.dp, vertical = 140.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text(text = "Username") },
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
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text(text = "Password") },
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

        Button(
            onClick = {
                if (authenticate(username, password)) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "main") {
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