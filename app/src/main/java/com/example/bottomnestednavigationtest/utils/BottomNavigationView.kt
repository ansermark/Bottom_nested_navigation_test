package com.example.bottomnestednavigationtest.utils

import androidx.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setupWithNavController(navController: NavController) {
    NavigationUi.setupWithNavController(this, navController)
}