package com.coldrifting.sirl.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Top Level Routes
@Serializable object RouteItems
@Serializable object RouteRecipes
@Serializable object RouteCart

data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector) {
    companion object {
        val routeItems = TopLevelRoute(RouteItems, Icons.AutoMirrored.Filled.List)
        val routeRecipes = TopLevelRoute(RouteRecipes, Icons.Filled.Star)
        val routeCart = TopLevelRoute(RouteCart, Icons.Filled.ShoppingCart)

        val topLevelRoutes = listOf(
            routeItems,
            routeRecipes,
            routeCart
        )
    }
}

// Nested Routes
@Serializable class  RouteItemDetails(val id: Int)
@Serializable object RouteItemList

@Serializable object RouteStoreList
@Serializable class  RouteStoreAisleList(val id: Int)

@Serializable object RouteRecipeList
@Serializable class  RouteRecipeDetails(val recipeId: Int)
@Serializable class  RouteRecipeEdit(val recipeId: Int)
@Serializable class  RouteRecipeEditSteps(val recipeId: Int)

@Serializable object RouteCartSelect
@Serializable object RouteCartList