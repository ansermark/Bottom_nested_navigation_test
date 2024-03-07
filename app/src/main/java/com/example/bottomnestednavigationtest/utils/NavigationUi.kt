package com.example.bottomnestednavigationtest.utils

import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.ui.*
import com.google.android.material.navigation.*
import java.lang.ref.*
import androidx.navigation.ui.R as NavigationUiR

/**
 * Класс, содержащий методы, используемые для настройки методов навигации в приложении,
 * таких как navigation drawer или bottom nav bar при помощи [NavController].
 */
object NavigationUi {

    private var lastStartMenuItemDestination: NavDestination? = null

    /**
     * Пытается навигироваться на [NavDestination], связанный с переданным MenuItem. Этому
     * MenuItem следует быть добавленным одним из вспомогательных методов в этом классе
     *
     * Важно, что полагается, что [menu item id][MenuItem.getItemId] совпадает с валидным
     * [action id][NavDestination.getAction] или [destination id][NavDestination.id], на которые
     * необходимо навигироваться
     *
     * По умолчанию, back stack откатится к start destination навигационного графа.
     * Пункты меню, в которых указан аргумент `android:menuCategory="secondary"` не откатят back stack
     * к start destination
     *
     * @param item Выбранный MenuItem.
     * @param navController NavController, содержащий destination
     * @return True, если [NavController] смог навигироваться на destination, связанный с переданным MenuItem
     */
    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val itemId = item.itemId
        val startDestination = navController.graph.startDestinationId
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

            if (itemId != startDestination && lastStartMenuItemDestination == null) {
                lastStartMenuItemDestination = navController.currentDestination
            }

            popUpTo(lastStartMenuItemDestination?.id ?: navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
        }

        return try {
            if (itemId == startDestination && lastStartMenuItemDestination != null) {
                val back = navController.currentBackStack.value.map { it.destination }
                val index = back.indexOfLast { it.id == lastStartMenuItemDestination?.id }.takeIf { it >= 0 }
                val backDropped = index?.let { back.drop(it + 1) } ?: emptyList()
                val destination = backDropped.firstOrNull()

                if (destination != null) {
                    navController.popBackStack(destination.id, true, true)
                } else {
                    navController.popBackStack(lastStartMenuItemDestination!!.id, false, true)
                }
                lastStartMenuItemDestination = null
            } else {
                navController.navigate(item.itemId, null, navOptions)
            }
            false
//            navController.currentDestination?.matchDestination(item.itemId) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun onNavDestinationReselected(item: MenuItem, navController: NavController) {
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

//        navController.navigate(
//            menuGraph.startDestinationId,
//            null,
//            navOptions { popUpTo(menuGraph.id) }
//        )
//        val s = (navController.graph[item.itemId] as? NavGraph)
//        navController.popBackStack(item.itemId, false)
//        val navOptions = navController.currentDestination?.let {
//            navOptions { popUpTo(it.id) { inclusive = true } }
//        }

//        navController.navigate(
//            item.itemId,
//            null,
//            navOptions { popUpTo(item.itemId) }
//        )
//        navController.navigate(item.itemId, null, navOptions)
    }

    /**
     * Настраивает [NavigationBarView] для использования с [NavController]. При выборе пункта бокового меню
     * будет вызван [onNavDestinationSelected]
     *
     * Выбранный пункт будет изменен после сразу после навигации
     *
     * @param navigationBarView NavigationBarView, синхронизированный с NavController
     * @param navController NavController содержащий главные и второстепенные пункты меню.
     * Навигация в этом NavController будет отражена в NavigationView.
     */
    fun setupWithNavController(
        navigationBarView: NavigationBarView,
        navController: NavController
    ) {
        navigationBarView.setOnItemSelectedListener { item ->
            onNavDestinationSelected(item, navController)
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
                        lastStartMenuItemDestination == null
                        && currentDestinationMenuItem != null
                        && currentDestinationMenuItem != startMenuItem
                        && currentDestinationMenuItem != previousDestinationMenuItem
                    ) {
                        lastStartMenuItemDestination = previousDestination
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

    internal fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}