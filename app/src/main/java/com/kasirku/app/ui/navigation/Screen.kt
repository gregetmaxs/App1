package com.kasirku.app.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Pos : Screen("pos")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object ProductList : Screen("product_list")
    data object ProductAdd : Screen("product_add")
    data object ProductEdit : Screen("product_edit/{productId}") {
        fun createRoute(productId: String) = "product_edit/$productId"
    }
    data object CategoryList : Screen("category_list")
    data object EmployeeList : Screen("employee_list")
    data object EmployeeAdd : Screen("employee_add")
    data object BarcodeScanner : Screen("barcode_scanner")
    data object PriceCheck : Screen("price_check")
    data object PrinterSettings : Screen("printer_settings")
    data object ReceiptSettings : Screen("receipt_settings")
}
