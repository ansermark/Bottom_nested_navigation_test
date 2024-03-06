package com.example.bottomnestednavigationtest.utils

import android.view.*
import androidx.annotation.*
import androidx.navigation.*

/*
fun NavController.findDestination(@IdRes destinationId: Int): NavDestination? {
    if (graph.id == destinationId) {
        return graph
    }
    val currentNode = backQueue.lastOrNull()?.destination ?: _graph!!
    return currentNode.findDestination(destinationId)
}*/

/**
 * True, если родитель предыдущего NavDestination является стартовым для графа
 */
val NavController.previousEntryInStartGraph get() = previousBackStackEntry?.destination?.parent?.id == graph.startDestinationId

/**
 * Определяет, нужно ли восстановить состояние back stack при навигации назад.
 * Находит последний NavDestination в текущем back stack, содержащийся в переданном Menu и:
 *
 * 1. если текущий NavDestination является стартовым для последнего в текущем back stack
 *
 * 2. если в соответствующем пункте меню  не указан аргумент `android:menuCategory="secondary"`
 *
 * - возвращает true
 *
 * @return True, если нужно восстановить состояние back stack при навигации назад
 */
//fun NavController.needRestoreStateOnPop(menu: Menu): Boolean {
//    val lastMenuDestination = getLastMenuItemDestination(menu)
//    val isCurrentDestinationLastInMenu = (lastMenuDestination as? NavGraph)?.startDestinationId == currentBackStackEntry?.destination?.id
//    val isNotSecondary = runCatching { menu.findItem(lastMenuDestination?.id ?: 0).order and Menu.CATEGORY_SECONDARY == 0 }
//        .getOrElse { false }
//
//    return isCurrentDestinationLastInMenu and isNotSecondary
//}
