package com.kasirku.app.ui.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kasirku.app.ui.screen.history.HistoryScreen
import com.kasirku.app.ui.screen.master.AddEmployeeScreen
import com.kasirku.app.ui.screen.master.AddProductScreen
import com.kasirku.app.ui.screen.master.CategoryScreen
import com.kasirku.app.ui.screen.master.EmployeeScreen
import com.kasirku.app.ui.screen.master.ProductListScreen
import com.kasirku.app.ui.screen.master.SupplierScreen
import com.kasirku.app.ui.screen.pos.PosScreen
import com.kasirku.app.ui.screen.profile.ProfileScreen
import com.kasirku.app.ui.theme.DarkSurface
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.core.model.License
import com.kasirku.core.model.Role
import com.kasirku.core.model.User

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainScaffold(
    user: User?,
    role: Role?,
    license: License?,
    onLogout: () -> Unit
) {
    val navItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("POS", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale),
        BottomNavItem("Riwayat", Icons.Filled.Receipt, Icons.Outlined.Receipt),
        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var currentSubScreen by rememberSaveable { mutableStateOf<String?>(null) }
    val storeId = user?.storeId ?: ""

    // If in a sub-screen, show it full-screen (no bottom nav)
    if (currentSubScreen != null) {
        when (currentSubScreen) {
            "products" -> ProductListScreen(
                storeId = storeId,
                onBack = { currentSubScreen = null },
                onAddProduct = { currentSubScreen = "add_product" }
            )
            "add_product" -> AddProductScreen(
                storeId = storeId,
                onBack = { currentSubScreen = "products" }
            )
            "categories" -> CategoryScreen(
                storeId = storeId,
                onBack = { currentSubScreen = null }
            )
            "employees" -> EmployeeScreen(
                storeId = storeId,
                onBack = { currentSubScreen = null },
                onAddEmployee = { currentSubScreen = "add_employee" }
            )
            "add_employee" -> AddEmployeeScreen(
                storeId = storeId,
                onBack = { currentSubScreen = "employees" }
            )
            "suppliers" -> SupplierScreen(
                storeId = storeId,
                onBack = { currentSubScreen = null }
            )
        }
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = TealPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> HomeScreen(
                user = user,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> PosScreen(
                storeId = storeId,
                userId = user?.id ?: "",
                userName = user?.fullName?.ifEmpty { "Kasir" } ?: "Kasir",
                modifier = Modifier.padding(paddingValues)
            )
            2 -> HistoryScreen(
                storeId = storeId,
                modifier = Modifier.padding(paddingValues)
            )
            3 -> ProfileScreen(
                user = user,
                role = role,
                license = license,
                onLogout = onLogout,
                onNavigateToProducts = { currentSubScreen = "products" },
                onNavigateToCategories = { currentSubScreen = "categories" },
                onNavigateToEmployees = { currentSubScreen = "employees" },
                onNavigateToSuppliers = { currentSubScreen = "suppliers" },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
