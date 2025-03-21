package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.screens.Cart
import com.coldrifting.sirl.screens.IngredientDetails
import com.coldrifting.sirl.screens.IngredientList
import com.coldrifting.sirl.screens.Recipes
import com.coldrifting.sirl.screens.StoreAisleList
import com.coldrifting.sirl.screens.StoreList
import com.coldrifting.sirl.ui.theme.SIRLTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            SIRLTheme {
                MainContent()
            }
        }
    }

    @Composable
    fun MainContent() {
        val viewModel: AppViewModel by viewModels { AppViewModel.Factory }

         LaunchedEffect(Unit) {
             viewModel.trySelectStore()
         }

        val navController = rememberNavController()
        var title by remember { mutableStateOf("") }
        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = Ingredients
        )
        {
            navigation<Stores>(startDestination = StoreList) {
                composable<StoreList> {
                    title = getRouteName(StoreList, viewModel)

                    val selectedStore by viewModel.selectedStore.collectAsState()
                    val storeList by viewModel.stores.collectAsState()

                    StoreList(
                        navHostController = navController,
                        title = title,
                        addStore = viewModel::addStore,
                        renameStore = viewModel::renameStore,
                        deleteStore = viewModel::deleteStore,
                        selectStore = viewModel::selectStore,
                        getStoreName = viewModel::getStoreName,
                        selectedStore = selectedStore,
                        storeList = storeList
                    )
                }
                composable<StoreAisleList> { backStackEntry ->
                    val aisleList: StoreAisleList = backStackEntry.toRoute()
                    title = getRouteName(aisleList, viewModel)

                    val aisles by viewModel.getAislesAtStore(aisleList.id).collectAsState()

                    StoreAisleList(
                        navHostController = navController,
                        title = title,
                        id = aisleList.id,
                        addAisle = viewModel::addAisle,
                        renameAisle = viewModel::renameAisle,
                        deleteAisle = viewModel::deleteAisle,
                        getAisleName = viewModel::getAisleName,
                        syncAisles = viewModel::syncAisles,
                        aisles = aisles
                    )
                }
            }
            navigation<Ingredients>(startDestination = IngredientList) {
                composable<IngredientList> {
                    title = getRouteName(Ingredients, viewModel)
                    IngredientList(
                        navHostController = navController,
                        title = title,
                        addItem = viewModel::addItem,
                        deleteItem = viewModel::deleteItem,
                        getItems = viewModel::getItemWithFilter)
                }
                composable<IngredientDetails> { backStackEntry ->
                    val ingredientDetails: IngredientDetails = backStackEntry.toRoute()
                    title = getRouteName(ingredientDetails, viewModel)
                    IngredientDetails(
                        navHostController = navController,
                        title = title,
                        itemId = ingredientDetails.id,
                        getItemName = viewModel::getItemName)
                }
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
}