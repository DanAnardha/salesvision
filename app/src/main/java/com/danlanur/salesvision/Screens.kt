package com.danlanur.salesvision

sealed class Screens (val screen: String){
    data object Dashboard: Screens("dashboard")
    data object Sales: Screens("sales")
    data object Settings: Screens("settings")
}