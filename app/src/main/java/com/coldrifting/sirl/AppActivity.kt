package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.screens.Cart
import com.coldrifting.sirl.screens.Ingredients
import com.coldrifting.sirl.screens.Recipes
import com.coldrifting.sirl.screens.StoreAisleList
import com.coldrifting.sirl.screens.StoreList
import com.coldrifting.sirl.ui.theme.SIRLTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIRLTheme {
                val viewModel: AppViewModel by viewModels()
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: AppViewModel) {
    val navController = rememberNavController()
    var title by remember { mutableStateOf("") }
    NavHost(
        navController = navController,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        startDestination = Stores)
    {
        navigation<Stores>(startDestination = StoreList) {
            composable<StoreList> {
                title = getRouteName(StoreList, viewModel)
                StoreList(navController, viewModel, title)
            }
            composable<StoreAisleList> { backStackEntry ->
                val aisleList: StoreAisleList = backStackEntry.toRoute()
                title = getRouteName(aisleList, viewModel)
                StoreAisleList(aisleList.id, navController, viewModel, title)
            }
        }
        composable<Ingredients> {
            title = getRouteName(Ingredients, viewModel)
            Ingredients(navController, title)
        }
        composable<Recipes> {
            title = getRouteName(Recipes, viewModel)
            Recipes(navController, title)
        }
        composable<Cart> {
            title = getRouteName(Cart, viewModel)
            Cart(navController, title)
        }
    }
}