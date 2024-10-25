package com.example.bottomnestednavigationtest.utils

import androidx.navigation.*
import com.google.android.material.bottomnavigation.*

fun BottomNavigationView.setupWithNavController(navController: NavController) {
    NavigationUi.setupWithNavController(this, navController)
}

fun BottomNavigationView.needRestoreStartDestination(navController: NavController): Boolean =
    NavigationUi.needRestoreStartDestination(this, navController)

fun BottomNavigationView.restoreStartDestination(navController: NavController): Boolean =
    NavigationUi.restoreStartDestination(this, navController)