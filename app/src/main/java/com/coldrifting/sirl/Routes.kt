package com.coldrifting.sirl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Stores", Stores, Icons.Filled.Place),
    TopLevelRoute("Ingredients", Ingredients, Icons.Filled.Menu),
    TopLevelRoute("Recipes", Recipes, Icons.Filled.Star),
    TopLevelRoute("Cart", Cart, Icons.Filled.ShoppingCart),
)

fun getRouteName(obj: Any, viewModel: AppViewModel): String {
    return when(obj) {
        is Stores -> "Stores"
        is StoreList -> "Stores - All"
        is StoreAisleList -> "Aisles - ${viewModel.getStoreName(obj.id)}"
        is Ingredients -> "Ingredients"
        is Recipes -> "Recipes"
        is Cart -> "Cart"
        else -> ""
    }
}

@Serializable
object Stores

@Serializable
object StoreList

@Serializable
data class StoreAisleList(val id: Int)

@Serializable
object Ingredients

@Serializable
object Recipes

@Serializable
object Cart