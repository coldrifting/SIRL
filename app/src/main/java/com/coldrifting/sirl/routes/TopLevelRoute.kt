package com.coldrifting.sirl.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector) {

    companion object {
        val routeStores = TopLevelRoute(Stores, Icons.Filled.Place)
        val routeIngredients = TopLevelRoute(Ingredients, Icons.Filled.Menu)
        val routeRecipes = TopLevelRoute(Recipes, Icons.Filled.Star)
        val routeCart = TopLevelRoute(Cart, Icons.Filled.ShoppingCart)

        val topLevelRoutes = listOf(
            routeStores,
            routeIngredients,
            routeRecipes,
            routeCart
        )
    }
}
