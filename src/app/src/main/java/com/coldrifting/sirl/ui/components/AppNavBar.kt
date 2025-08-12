package com.coldrifting.sirl.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.coldrifting.sirl.data.TopLevelRoute
import com.coldrifting.sirl.data.TopLevelRoute.Companion.topLevelRoutes

@Composable
fun AppNavBar(navController: NavHostController, selectedRoute: TopLevelRoute<out Any>) {
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            val routeName: String = topLevelRoute.route.javaClass.simpleName.replace("Route", "")
            NavigationBarItem(
                label = { Text(routeName) },
                icon = { Icon(topLevelRoute.icon, contentDescription = routeName) },
                selected = topLevelRoute == selectedRoute,
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