package com.danlanur.salesvision

sealed class Screens (val screen: String){
    data object Dashboard: Screens("dashboard")
    data object Profile: Screens("profile")
    data object Settings: Screens("settings")
}