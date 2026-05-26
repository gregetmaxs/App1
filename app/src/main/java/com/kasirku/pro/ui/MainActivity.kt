package com.kasirku.pro.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kasirku.pro.R
import com.kasirku.pro.databinding.ActivityMainBinding
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.util.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    updateBottomNavVisibility()
                }
            }
        }

        if (sessionManager.isLoggedIn()) {
            navController.navigate(R.id.nav_dashboard)
        }
    }

    private fun updateBottomNavVisibility() {
        val menu = binding.bottomNavigation.menu
        menu.findItem(R.id.nav_dashboard)?.isVisible = true
        menu.findItem(R.id.nav_pos)?.isVisible = sessionManager.canAccessPos()
        menu.findItem(R.id.nav_transactions)?.isVisible = sessionManager.canAccessPos() || sessionManager.canAccessReport()
        menu.findItem(R.id.nav_more)?.isVisible = true
    }
}
