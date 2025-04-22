package com.coldrifting.sirl.components

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.routes.top.TopLevelRoute
import com.coldrifting.sirl.routes.top.TopLevelRoute.Companion.routeIngredients
import com.coldrifting.sirl.routes.top.TopLevelRoute.Companion.topLevelRoutes
import com.coldrifting.sirl.ui.theme.SIRLTheme

@Composable
fun AppNavBar(navController: NavHostController, selectedRoute: TopLevelRoute<out Any>) {
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            val routeName: String = topLevelRoute.route.javaClass.simpleName.replace("Route", "")
            NavigationBarItem(label = { Text(routeName) },
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppNavBarPreview() {
    SIRLTheme {
        AppNavBar(rememberNavController(), routeIngredients)
    }
}