package com.kasirku.owner.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object StoreList : Screen("store_list")
    data object StoreDetail : Screen("store_detail/{storeId}") {
        fun createRoute(storeId: String) = "store_detail/$storeId"
    }
    data object LicenseCreate : Screen("license_create")
    data object LicenseList : Screen("license_list")
    data object Pricing : Screen("pricing")
    data object Profile : Screen("profile")
}
