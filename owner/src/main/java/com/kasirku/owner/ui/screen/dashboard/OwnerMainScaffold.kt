package com.kasirku.owner.ui.screen.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Store
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kasirku.owner.ui.screen.license.LicenseScreen
import com.kasirku.owner.ui.screen.pricing.PricingScreen
import com.kasirku.owner.ui.screen.store.StoreScreen
import com.kasirku.owner.ui.theme.BluePrimary
import com.kasirku.owner.ui.theme.DarkSurface
import com.kasirku.core.model.User

data class OwnerNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun OwnerMainScaffold(
    user: User?,
    onLogout: () -> Unit
) {
    val navItems = listOf(
        OwnerNavItem("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        OwnerNavItem("Toko", Icons.Filled.Store, Icons.Outlined.Store),
        OwnerNavItem("License", Icons.Filled.Sell, Icons.Outlined.Sell),
        OwnerNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

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
                            selectedIconColor = BluePrimary,
                            selectedTextColor = BluePrimary,
                            indicatorColor = BluePrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> OwnerDashboardScreen(modifier = Modifier.padding(paddingValues))
            1 -> StoreScreen(modifier = Modifier.padding(paddingValues))
            2 -> LicenseScreen(userId = user?.id ?: "", modifier = Modifier.padding(paddingValues))
            3 -> PricingScreen(
                user = user,
                onLogout = onLogout,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
