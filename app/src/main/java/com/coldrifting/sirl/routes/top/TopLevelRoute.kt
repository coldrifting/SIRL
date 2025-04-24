package com.coldrifting.sirl.routes.top

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector) {

    companion object {
        val routeIngredients = TopLevelRoute(RouteIngredients, Icons.AutoMirrored.Filled.List)
        val routeRecipes = TopLevelRoute(RouteRecipes, Icons.Filled.Star)
        val routeCart = TopLevelRoute(RouteCart, Icons.Filled.ShoppingCart)

        val topLevelRoutes = listOf(
            routeIngredients,
            routeRecipes,
            routeCart
        )
    }
}