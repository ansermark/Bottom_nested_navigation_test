package com.example.bottomnestednavigationtest.utils

import android.annotation.*
import android.os.*
import android.util.*
import android.view.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.ui.*
import com.google.android.material.navigation.*
import java.lang.ref.*
import androidx.navigation.ui.R as NavigationUiR

/**
 * Класс, содержащий методы, используемые для настройки методов навигации в приложении,
 * таких как [NavigationBarView] или Toolbar при помощи [NavController].
 */
object NavigationUi {

    /**
     * Id последнего [NavDestination] в бэк стеке, который можно отнести к стартовой вкладке меню
     */
    private var lastStartMenuItemEntryIndex: Int? = null

    private fun onNavDestinationSelected(item: MenuItem, navController: NavController) {
        val graphStartDestinationId = navController.graph.startDestinationId

        if (item.itemId != graphStartDestinationId && lastStartMenuItemEntryIndex == null) {
            lastStartMenuItemEntryIndex = navController.backStack.indexOf(navController.currentBackStackEntry)
        }

        if (item.itemId == graphStartDestinationId && lastStartMenuItemEntryIndex != null) {
            val destinationNext = navController.backStack.getOrNull(lastStartMenuItemEntryIndex!! + 1)?.destination

            if (destinationNext != null) {
                lastStartMenuItemEntryIndex = null
                navController.popBackStack(destinationNext.id, true, true)
            }
        } else {
            val navOptions = navOptions {
                launchSingleTop = true
                restoreState = true

                if (navController.currentDestination!!.parent!!.findNode(item.itemId) is ActivityNavigator.Destination) {
                    anim {
                        enter = NavigationUiR.anim.nav_default_enter_anim
                        exit = NavigationUiR.anim.nav_default_exit_anim
                        popEnter = NavigationUiR.anim.nav_default_pop_enter_anim
                        popExit = NavigationUiR.anim.nav_default_pop_exit_anim
                    }
                } else {
                    anim {
                        enter = NavigationUiR.animator.nav_default_enter_anim
                        exit = NavigationUiR.animator.nav_default_exit_anim
                        popEnter = NavigationUiR.animator.nav_default_pop_enter_anim
                        popExit = NavigationUiR.animator.nav_default_pop_exit_anim
                    }
                }

                val destination = if (lastStartMenuItemEntryIndex != null) {
                    navController.backStack[lastStartMenuItemEntryIndex!!].destination
                } else {
                    navController.graph.findStartDestination()
                }

                popUpTo(destination.id) {
                    inclusive = false
                    saveState = true
                }
            }

            try {
                navController.navigate(item.itemId, null, navOptions)
            } catch (e: IllegalArgumentException) {
                Log.e(NavigationUi.javaClass.simpleName, "Error while navigating", e)
            }
        }
    }

    private fun onNavDestinationReselected(item: MenuItem, navController: NavController) {
        val menuGraph = navController.graph[item.itemId] as NavGraph

        navController.navigate(
            menuGraph.id,
            null,
            navOptions {
                if (navController.currentDestination!!.parent!!.findNode(item.itemId) is ActivityNavigator.Destination) {
                    anim {
                        enter = NavigationUiR.anim.nav_default_enter_anim
                        exit = NavigationUiR.anim.nav_default_exit_anim
                        popEnter = NavigationUiR.anim.nav_default_pop_enter_anim
                        popExit = NavigationUiR.anim.nav_default_pop_exit_anim
                    }
                } else {
                    anim {
                        enter = NavigationUiR.animator.nav_default_enter_anim
                        exit = NavigationUiR.animator.nav_default_exit_anim
                        popEnter = NavigationUiR.animator.nav_default_pop_enter_anim
                        popExit = NavigationUiR.animator.nav_default_pop_exit_anim
                    }
                }

                popUpTo(menuGraph.id)
            }
        )
    }

    /**
     * Синхронизирует работу [NavigationBarView] с [NavController]. При выборе вкладки нижней навигации
     * будет вызван [onNavDestinationSelected], при повторном выборе вкладки будет вызван [onNavDestinationReselected]
     *
     * После навигации будет выбрана подходящая вкладка
     */
    fun setupWithNavController(navigationBarView: NavigationBarView, navController: NavController) {
        navigationBarView.setOnItemSelectedListener { item ->
            onNavDestinationSelected(item, navController)
            false
        }
        navigationBarView.setOnItemReselectedListener { item ->
            onNavDestinationReselected(item, navController)
        }

        val viewReference = WeakReference(navigationBarView)

        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = viewReference.get()

                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }

                    if (destination is FloatingWindow) {
                        return
                    }

                    val currentDestinationMenuItem = view.menu.findItem(destination.parent!!.id)
                    val startMenuItem = view.menu.findItem(controller.graph.startDestinationId)
                    val previousDestination = controller.previousBackStackEntry?.destination
                    val previousDestinationMenuItem = previousDestination?.let { view.menu.findItem(it.parent!!.id) }

                    if (
                        lastStartMenuItemEntryIndex == null
                        && currentDestinationMenuItem != null
                        && currentDestinationMenuItem != startMenuItem
                        && currentDestinationMenuItem != previousDestinationMenuItem
                    ) {
                        lastStartMenuItemEntryIndex = controller.backStack.indexOf(controller.previousBackStackEntry)
                    } else if (
                        lastStartMenuItemEntryIndex != null
                        && currentDestinationMenuItem == startMenuItem
                        && currentDestinationMenuItem != previousDestinationMenuItem
                    ) {
                        lastStartMenuItemEntryIndex = null
                    }

                    if (currentDestinationMenuItem != null) {
                        currentDestinationMenuItem.isChecked = true
                    } else if (previousDestinationMenuItem != null) {
                        previousDestinationMenuItem.isChecked = true
                    }
                }
            }
        )
    }

    private val NavController.backStack: List<NavBackStackEntry>
        @SuppressLint("RestrictedApi")
        get() = currentBackStack.value
}