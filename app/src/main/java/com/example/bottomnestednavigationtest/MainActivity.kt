package com.example.bottomnestednavigationtest

import android.os.*
import androidx.appcompat.app.*
import androidx.navigation.fragment.*
import androidx.navigation.ui.*
import com.example.bottomnestednavigationtest.databinding.*
import com.google.android.material.bottomnavigation.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.navigation_graph_home)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        println("Up")
        return super.onSupportNavigateUp()
    }
}