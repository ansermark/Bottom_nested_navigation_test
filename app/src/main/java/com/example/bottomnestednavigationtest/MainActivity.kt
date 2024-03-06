package com.example.bottomnestednavigationtest

import android.os.*
import androidx.appcompat.app.*
import androidx.navigation.*
import androidx.navigation.fragment.*
import androidx.navigation.ui.*
import com.example.bottomnestednavigationtest.databinding.*
import com.example.bottomnestednavigationtest.utils.*
import com.google.android.material.bottomnavigation.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.home_nav_graph),
            fallbackOnNavigateUpListener = {
                onBackPressedDispatcher.onBackPressed()
                true
            }
        )
    }

    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(appBarConfiguration)
}