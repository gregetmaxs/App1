package com.kasirku.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kasirku.app.ui.screen.login.LoginScreen
import com.kasirku.app.ui.screen.home.MainScaffold
import com.kasirku.app.viewmodel.AuthViewModel

@Composable
fun KasirKuNavHost(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            navController.navigate("main") {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            if (navController.currentBackStackEntry?.destination?.route == "main") {
                navController.navigate(Screen.Login.route) {
                    popUpTo("main") { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) "main" else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable("main") {
            MainScaffold(
                user = authState.user,
                role = authState.role,
                license = authState.license,
                onLogout = { authViewModel.logout() }
            )
        }
    }
}
