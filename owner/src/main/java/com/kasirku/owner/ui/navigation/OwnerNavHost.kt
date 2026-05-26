package com.kasirku.owner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kasirku.owner.ui.screen.login.OwnerLoginScreen
import com.kasirku.owner.ui.screen.dashboard.OwnerMainScaffold
import com.kasirku.owner.viewmodel.OwnerAuthViewModel

@Composable
fun OwnerNavHost(
    authViewModel: OwnerAuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) "main" else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            OwnerLoginScreen(
                uiState = authState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable("main") {
            OwnerMainScaffold(
                user = authState.user,
                onLogout = { authViewModel.logout() }
            )
        }
    }

    if (authState.isLoggedIn && navController.currentDestination?.route == Screen.Login.route) {
        navController.navigate("main") {
            popUpTo(Screen.Login.route) { inclusive = true }
        }
    }
    if (!authState.isLoggedIn && navController.currentDestination?.route == "main") {
        navController.navigate(Screen.Login.route) {
            popUpTo("main") { inclusive = true }
        }
    }
}
