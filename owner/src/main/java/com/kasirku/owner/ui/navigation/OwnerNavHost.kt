package com.kasirku.owner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            navController.navigate("main") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == "main") {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
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
}
