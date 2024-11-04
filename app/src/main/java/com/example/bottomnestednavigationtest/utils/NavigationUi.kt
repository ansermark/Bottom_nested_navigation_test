package com.example.bottomnestednavigationtest.utils

import android.content.*
import android.content.res.*
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

    private const val TAG = "NavigationUi"

    /**
     * Синхронизирует работу [NavigationBarView] с [NavController]. При выборе вкладки нижней навигации
     * будет вызван [onNavDestinationSelected], при повторном выборе вкладки будет вызван [onNavDestinationReselected]
     *
     * После навигации будет выбрана подходящая вкладка
     */
    fun setupWithNavController(navigationBarView: NavigationBarView, navController: NavController) {
        navigationBarView.setOnItemSelectedListener { item ->
            onNavDestinationSelected(navController, item)
        }
        navigationBarView.setOnItemReselectedListener { item ->
            onNavDestinationReselected(navController, item)
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
                    val previousDestination = controller.previousBackStackEntry?.destination
                    val previousDestinationMenuItem = previousDestination?.let { view.menu.findItem(it.parent!!.id) }


                    // установить выделение подходящего пункта меню
                    when {
                        currentDestinationMenuItem != null -> currentDestinationMenuItem.isChecked = true
                        previousDestinationMenuItem != null -> previousDestinationMenuItem.isChecked = true
                    }
                }
            }
        )
    }

    private fun onNavDestinationSelected(navController: NavController, item: MenuItem): Boolean {
        val navOptions = navOptions {
            launchSingleTop = true
            restoreState = true

            anim {
                if (
                    navController.currentDestination!!.parent!!.findNode(item.itemId)
                        is ActivityNavigator.Destination
                ) {
                    enter = NavigationUiR.anim.nav_default_enter_anim
                    exit = NavigationUiR.anim.nav_default_exit_anim
                    popEnter = NavigationUiR.anim.nav_default_pop_enter_anim
                    popExit = NavigationUiR.anim.nav_default_pop_exit_anim
                } else {
                    enter = NavigationUiR.animator.nav_default_enter_anim
                    exit = NavigationUiR.animator.nav_default_exit_anim
                    popEnter = NavigationUiR.animator.nav_default_pop_enter_anim
                    popExit = NavigationUiR.animator.nav_default_pop_exit_anim
                }
            }

            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        }

        return try {
            navController.navigate(item.itemId, null, navOptions)
            true
        } catch (e: IllegalArgumentException) {
            val name = getDisplayName(navController.context, item.itemId)
            Log.i(
                TAG,
                "Ignoring onNavDestinationSelected for MenuItem $name as it cannot be found " +
                    "from the current destination ${navController.currentDestination}",
                e
            )
            false
        }
    }

    private fun onNavDestinationReselected(navController: NavController, item: MenuItem) {
        val menuGraph = navController.graph[item.itemId] as NavGraph

        if (navController.currentDestination == menuGraph.findStartDestination()) {
            return
        }
        // навигация на граф соответствующего пункта меню с очищением стека
        navController.navigate(
            item.itemId,
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

                popUpTo(item.itemId)
            }
        )
    }

    fun restoreStartDestination(navigationBarView: NavigationBarView, navController: NavController): Boolean =
        needRestoreStartDestination(navigationBarView, navController)
            .also { needRestore ->
                if (needRestore) {
                    navigationBarView.selectedItemId = navController.graph.findStartDestination().parent!!.id
                }
            }

    fun needRestoreStartDestination(navigationBarView: NavigationBarView, navController: NavController): Boolean {
        val previousDestination = navController.previousBackStackEntry?.destination
        val startDestination = navController.graph.findStartDestination()

        return previousDestination?.id == startDestination.id &&
            navigationBarView.selectedItemId != startDestination.parent!!.id
    }

    fun getDisplayName(context: Context, id: Int): String =
        try {
            context.resources.getResourceName(id)
        } catch (e: Resources.NotFoundException) {
            id.toString()
        }
}