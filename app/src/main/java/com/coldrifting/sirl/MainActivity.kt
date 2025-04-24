package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.routes.top.RouteCart
import com.coldrifting.sirl.routes.RouteIngredientDetails
import com.coldrifting.sirl.routes.RouteIngredientList
import com.coldrifting.sirl.routes.top.RouteIngredients
import com.coldrifting.sirl.routes.RouteRecipeDetails
import com.coldrifting.sirl.routes.RouteRecipeEdit
import com.coldrifting.sirl.routes.RouteRecipeEditSteps
import com.coldrifting.sirl.routes.RouteRecipeList
import com.coldrifting.sirl.routes.top.RouteRecipes
import com.coldrifting.sirl.routes.RouteStoreAisleList
import com.coldrifting.sirl.routes.RouteStoreList
import com.coldrifting.sirl.routes.top.RouteStores
import com.coldrifting.sirl.ui.screens.Cart
import com.coldrifting.sirl.ui.screens.IngredientDetails
import com.coldrifting.sirl.ui.screens.IngredientList
import com.coldrifting.sirl.ui.screens.RecipeDetails
import com.coldrifting.sirl.ui.screens.RecipeEdit
import com.coldrifting.sirl.ui.screens.RecipeList
import com.coldrifting.sirl.ui.screens.StoreAisleList
import com.coldrifting.sirl.ui.screens.StoreList
import com.coldrifting.sirl.ui.screens.RecipeEditSteps
import com.coldrifting.sirl.ui.theme.SIRLTheme
import com.coldrifting.sirl.view.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            SIRLTheme(dynamicColor = false) {
                MainContent()
            }
        }
    }

    @Composable
    fun MainContent() {
        val viewModel: AppViewModel by viewModels { AppViewModel.Factory }

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = RouteStores
        ) {
            navigation<RouteStores>(startDestination = RouteStoreList) {
                composable<RouteStoreList> {
                    val selectedStore by viewModel.stores.selected.collectAsState()
                    val storeList by viewModel.stores.all.collectAsState()

                    StoreList(
                        navHostController = navController,
                        addStore = viewModel.stores::add,
                        renameStore = viewModel.stores::rename,
                        deleteStore = viewModel.stores::delete,
                        selectStore = viewModel.stores::select,
                        getStoreName = viewModel.stores::getName,
                        selectedStore = selectedStore,
                        storeList = storeList.map{s -> Store(s.storeId, s.storeName, s.selected)}
                    )
                }
                composable<RouteStoreAisleList> { backStackEntry ->
                    val aisleList = backStackEntry.toRoute<RouteStoreAisleList>()

                    val storeList by viewModel.stores.all.collectAsState()
                    val store = storeList.first {s -> s.storeId == aisleList.id }

                    val aisles by viewModel.stores.getAisles(aisleList.id).collectAsState()

                    val scrollTo by viewModel.stores.firstItemIndexState.collectAsState()

                    StoreAisleList(
                        navHostController = navController,
                        store = Store(store.storeId, store.storeName, store.selected),
                        addAisle = viewModel.stores::addAisle,
                        renameAisle = viewModel.stores::renameAisle,
                        deleteAisle = viewModel.stores::deleteAisle,
                        getAisleName = viewModel.stores::getAisleName,
                        syncAisles = viewModel.stores::syncAisles,
                        aisles = aisles.map{ Aisle(it.aisleId, it.storeId, it.aisleName, it.sortingPrefix)},
                        scrollTo = scrollTo
                    )
                }
            }
            navigation<RouteIngredients>(startDestination = RouteIngredientList) {
                composable<RouteIngredientList> {
                    val sortingMode by viewModel.items.sortingModeState.collectAsState()
                    val items by viewModel.items.filtered.collectAsState()
                    val searchText by viewModel.items.filterTextState.collectAsState()
                    IngredientList(
                        navHostController = navController,
                        addItem = viewModel.items::add,
                        deleteItem = viewModel.items::delete,
                        checkDeleteItem = viewModel.items::getUsedItems,
                        items = items,
                        onFilterTextChanged = viewModel.items::updateFilter,
                        setItemSort = viewModel.items::toggleItemSorting,
                        sortMode = sortingMode.name,
                        searchText = searchText)
                }
                composable<RouteIngredientDetails> { backStackEntry ->
                    val routeIngredientDetails = backStackEntry.toRoute<RouteIngredientDetails>()
                    val item by viewModel.items.get(routeIngredientDetails.id).collectAsState()
                    val itemAisle by viewModel.items.getAisle(item.itemId).collectAsState()
                    val currentStore by viewModel.stores.selected.collectAsState()
                    val stores by viewModel.stores.all.collectAsState()
                    val store = stores.first {s -> s.storeId == currentStore}
                    val aisles by viewModel.stores.getAisles(currentStore).collectAsState()
                    val prep by viewModel.items.getPreps(item.itemId).collectAsState()
                    IngredientDetails(
                        navHostController = navController,
                        item = item,
                        itemAisle = itemAisle,
                        aisles = aisles.map{ Aisle(it.aisleId, it.storeId, it.aisleName, it.sortingPrefix)},
                        prep = prep,
                        currentStore = Store(store.storeId, store.storeName, store.selected),
                        setStore = viewModel.stores::select,
                        updatePrep = viewModel.items::renamePrep,
                        addPrep = viewModel.items::addPrep,
                        deletePrep = viewModel.items::deletePrep,
                        checkDeletePrep = viewModel.items::getUsedItemPreps,
                        setItemName = viewModel.items::rename,
                        setItemAisle = viewModel.items::setAisle,
                        setItemTemp = viewModel.items::setTemp,
                        setItemDefaultUnits = viewModel.items::setDefaultUnits,
                        stores = stores.map{s -> Store(s.storeId, s.storeName, s.selected)})
                }
            }
            navigation<RouteRecipes>(startDestination = RouteRecipeList) {
                composable<RouteRecipeList> {
                    val recipes by viewModel.recipes.all.collectAsState()
                    RecipeList(
                        navHostController = navController,
                        recipes = recipes,
                        toggleRecipePin = viewModel.recipes::pin,
                        addRecipe = viewModel.recipes::add,
                        deleteRecipe = viewModel.recipes::delete
                    )
                }
                composable<RouteRecipeDetails> { backStackEntry ->
                    val routeRecipeEdit = backStackEntry.toRoute<RouteRecipeEdit>()
                    val recipe by viewModel.recipes.get(routeRecipeEdit.recipeId).collectAsState()
                    RecipeDetails(
                        navHostController = navController,
                        recipe = recipe
                    )
                }
                composable<RouteRecipeEdit> { backStackEntry ->
                    val itemsWithPrep by viewModel.items.allWithPrep.collectAsState()
                    val routeRecipeEdit = backStackEntry.toRoute<RouteRecipeEdit>()
                    val recipe by viewModel.recipes.get(routeRecipeEdit.recipeId).collectAsState()
                    RecipeEdit(
                        navHostController = navController,
                        itemsWithPrep = itemsWithPrep,
                        recipe = recipe,
                        addSection = viewModel.recipes::addSection,
                        deleteSection = viewModel.recipes::deleteSection,
                        setRecipeSectionName = viewModel.recipes::renameSection,
                        setRecipeItemAmount = viewModel.recipes::setItemAmount,
                        addRecipeEntry = viewModel.recipes::addItem,
                        deleteRecipeEntry = viewModel.recipes::deleteItem
                    )
                }
                composable<RouteRecipeEditSteps> { backStackEntry ->
                    val routeRecipeEditSteps = backStackEntry.toRoute<RouteRecipeEditSteps>()
                    val recipe by viewModel.recipes.get(routeRecipeEditSteps.recipeId).collectAsState()
                    RecipeEditSteps(
                        navHostController = navController,
                        recipe = recipe,
                        setRecipeName = viewModel.recipes::rename,
                        setRecipeSteps = viewModel.recipes::editSteps
                    )
                }
            }
            composable<RouteCart> {
                val list by viewModel.cart.list.collectAsState()
                Cart(
                    navHostController = navController,
                    list = list,
                    getShoppingList = viewModel.cart::getList,
                    onHeaderClicked = viewModel.cart::cartHeaderClicked,
                    onItemClicked = viewModel.cart::cartItemClicked
                )
            }
        }
    }
}