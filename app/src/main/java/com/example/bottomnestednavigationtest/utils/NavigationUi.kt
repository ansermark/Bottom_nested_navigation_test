package com.example.bottomnestednavigationtest.utils

import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.core.view.*
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.ui.*
import androidx.navigation.ui.R
import com.google.android.material.navigation.*
import java.lang.ref.*

/**
 * Класс, содержащий методы, используемые для настройки методов навигации в приложении,
 * таких как navigation drawer или bottom nav bar при помощи [NavController].
 */
object NavigationUi {

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
    fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val navOptions = navOptions {
            launchSingleTop = true
            restoreState = true

            if (navController.currentDestination!!.parent!!.findNode(item.itemId) is ActivityNavigator.Destination) {
                anim {
                    enter = R.anim.nav_default_enter_anim
                    exit = R.anim.nav_default_exit_anim
                    popEnter = R.anim.nav_default_pop_enter_anim
                    popExit = R.anim.nav_default_pop_exit_anim
                }
            } else {
                anim {
                    enter = R.animator.nav_default_enter_anim
                    exit = R.animator.nav_default_exit_anim
                    popEnter = R.animator.nav_default_pop_enter_anim
                    popExit = R.animator.nav_default_pop_exit_anim
                }
            }

            if (item.order and Menu.CATEGORY_SECONDARY == 0) {
                navController.previousBackStackEntry
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = false
                    saveState = true
                }
            }
        }

        return try {
            navController.navigate(item.itemId, null, navOptions)
            navController.currentDestination?.matchDestination(item.itemId) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun onNavDestinationReselected(item: MenuItem, navController: NavController) {
        val menuGraph = navController.graph[item.itemId] as NavGraph

        navController.navigate(
            menuGraph.startDestinationId,
            null,
            navOptions { popUpTo(menuGraph.id) }
        )
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
//        navigationBarView.setOnItemReselectedListener { item ->
//            onNavDestinationReselected(item, navController)
//        }

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

                    view.menu.forEach { item ->
                        if (destination.matchDestination(item.itemId)) {
                            item.isChecked = true
                        }
                    }
//                    view.menu.children
//                        .map { it.itemId }
//                        .toList()
//                        .intersect(destination.hierarchy.map { it.id }.toSet())
//                        .firstOrNull()
//                        ?.let { view.menu.findItem(it)?.isChecked = true }
                }
            }
        )
    }

    internal fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}