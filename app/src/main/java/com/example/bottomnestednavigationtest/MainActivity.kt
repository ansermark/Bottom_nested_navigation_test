package com.example.bottomnestednavigationtest

import android.os.*
import androidx.activity.*
import androidx.appcompat.app.*
import androidx.navigation.*
import androidx.navigation.fragment.*
import androidx.navigation.ui.*
import com.example.bottomnestednavigationtest.databinding.*
import com.example.bottomnestednavigationtest.utils.*
import com.google.android.material.bottomnavigation.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }

    val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            topLevelDestinationIds = emptySet(),
            fallbackOnNavigateUpListener = {
                onBackPressedDispatcher.onBackPressed()
                true
            }
        )
    }

    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        swapNavHost()

        setupActionBarWithNavController(navController, appBarConfiguration)

        backPressedCallback = onBackPressedDispatcher.addCallback(
            owner = this,
            enabled = false,
            onBackPressed = { navView.restoreStartDestination(navController) }
        )

        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                backPressedCallback?.isEnabled = binding.navView.needRestoreStartDestination(navController)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback = null
    }

    override fun onSupportNavigateUp(): Boolean =
        binding.navView.restoreStartDestination(navController) ||
            navController.navigateUp(appBarConfiguration)

    private var isOmitted = false

    fun swapNavHost() {
        if (isOmitted) {
            val navGraph = navController.navInflater.inflate(R.navigation.global_host)
                .apply { setStartDestination(R.id.home_nav_graph) }

            navController.graph = navGraph
            binding.navView.menu.clear()
            binding.navView.inflateMenu(R.menu.bottom_nav_menu)
            binding.navView.setupWithNavController(navController)
            isOmitted = false
        } else {
            val navGraph = navController.navInflater.inflate(R.navigation.global_host)
                .apply { setStartDestination(R.id.dashboard_nav_graph) }

            navController.graph = navGraph
            binding.navView.menu.clear()
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_omitted)
            binding.navView.setupWithNavController(navController)
            isOmitted = true
        }
    }
}