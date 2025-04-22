package com.coldrifting.sirl.routes.top

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.coldrifting.sirl.routes.top.RouteStores

data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector) {

    companion object {
        val routeStores = TopLevelRoute(RouteStores, Icons.Filled.Place)
        val routeIngredients = TopLevelRoute(RouteIngredients, Icons.Filled.Menu)
        val routeRecipes = TopLevelRoute(RouteRecipes, Icons.Filled.Star)
        val routeCart = TopLevelRoute(RouteCart, Icons.Filled.ShoppingCart)

        val topLevelRoutes = listOf(
            routeStores,
            routeIngredients,
            routeRecipes,
            routeCart
        )
    }
}