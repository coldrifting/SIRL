package com.coldrifting.sirl.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.coldrifting.sirl.topLevelRoutes

@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(label = { Text(topLevelRoute.name) },
                icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                selected = navBackStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(topLevelRoute.route::class)
                } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = false
                            saveState = true
                        }
                        restoreState = true
                    }
                })
        }
    }
}