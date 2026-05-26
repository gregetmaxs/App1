package com.kasirku.app.ui.screen.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.kasirku.app.ui.theme.DarkBackground
import com.kasirku.app.ui.theme.DarkSurface
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.core.model.License
import com.kasirku.core.model.Role
import com.kasirku.core.model.User

data class NavItem(
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
    val storeId = user?.storeId ?: ""
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var currentSubScreen by rememberSaveable { mutableStateOf<String?>(null) }

    val navItems = listOf(
        NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("POS", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale),
        NavItem("Riwayat", Icons.Filled.History, Icons.Outlined.History),
        NavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    // Sub-screen rendering (full screen, no bottom nav)
    currentSubScreen?.let { screen ->
        when (screen) {
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
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .shadow(8.dp)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            unselectedIconColor = Color(0xFF8888AA),
                            unselectedTextColor = Color(0xFF8888AA),
                            indicatorColor = TealPrimary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(user = user, modifier = Modifier.fillMaxSize())
                1 -> PosScreen(
                    storeId = storeId,
                    userId = user?.id ?: "",
                    userName = user?.fullName?.ifEmpty { "Kasir" } ?: "Kasir",
                    modifier = Modifier.fillMaxSize()
                )
                2 -> HistoryScreen(storeId = storeId, modifier = Modifier.fillMaxSize())
                3 -> ProfileScreen(
                    user = user,
                    license = license,
                    onLogout = onLogout,
                    onNavigateToProducts = { currentSubScreen = "products" },
                    onNavigateToCategories = { currentSubScreen = "categories" },
                    onNavigateToEmployees = { currentSubScreen = "employees" },
                    onNavigateToSuppliers = { currentSubScreen = "suppliers" },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
