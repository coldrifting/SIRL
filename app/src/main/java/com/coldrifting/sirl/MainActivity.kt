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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.routes.Cart
import com.coldrifting.sirl.routes.IngredientDetails
import com.coldrifting.sirl.routes.IngredientList
import com.coldrifting.sirl.routes.Ingredients
import com.coldrifting.sirl.routes.RecipeDetails
import com.coldrifting.sirl.routes.RecipeEdit
import com.coldrifting.sirl.routes.RecipeEditSteps
import com.coldrifting.sirl.routes.RecipeList
import com.coldrifting.sirl.routes.Recipes
import com.coldrifting.sirl.routes.StoreAisleList
import com.coldrifting.sirl.routes.StoreList
import com.coldrifting.sirl.routes.Stores
import com.coldrifting.sirl.screens.Cart
import com.coldrifting.sirl.screens.IngredientDetails
import com.coldrifting.sirl.screens.IngredientList
import com.coldrifting.sirl.screens.RecipeDetails
import com.coldrifting.sirl.screens.RecipeEdit
import com.coldrifting.sirl.screens.RecipeList
import com.coldrifting.sirl.screens.StoreAisleList
import com.coldrifting.sirl.screens.StoreList
import com.coldrifting.sirl.ui.theme.SIRLTheme

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

         LaunchedEffect(Unit) {
             viewModel.trySelectStore()
         }

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = Recipes
        )
        {
            // TODO - Move to header of ingredient screen?
            navigation<Stores>(startDestination = StoreList) {
                composable<StoreList> {

                    val selectedStore by viewModel.selectedStore.collectAsState()
                    val storeList by viewModel.stores.collectAsState()

                    StoreList(
                        navHostController = navController,
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

                    // TODO - Add proper method for this in view model
                    val storeList by viewModel.stores.collectAsState()
                    val store = storeList.first {s -> s.storeId == aisleList.id }

                    val aisles by viewModel.getAislesAtStore(aisleList.id).collectAsState()

                    val scrollTo by viewModel.firstItemIndexState.collectAsState()

                    StoreAisleList(
                        navHostController = navController,
                        store = store,
                        addAisle = viewModel::addAisle,
                        renameAisle = viewModel::renameAisle,
                        deleteAisle = viewModel::deleteAisle,
                        getAisleName = viewModel::getAisleName,
                        syncAisles = viewModel::syncAisles,
                        aisles = aisles,
                        scrollTo = scrollTo
                    )
                }
            }
            navigation<Ingredients>(startDestination = IngredientList) {
                composable<IngredientList> {
                    val sortingMode by viewModel.itemsSortingModeState.collectAsState()
                    val items by viewModel.itemsWithFilter.collectAsState()
                    val searchText by viewModel.itemsFilterTextState.collectAsState()
                    IngredientList(
                        navHostController = navController,
                        addItem = viewModel::addItem,
                        deleteItem = viewModel::deleteItem,
                        checkDeleteItem = viewModel::getUsedItems,
                        items = items,
                        onFilterTextChanged = viewModel::updateItemFilter,
                        setItemSort = viewModel::toggleItemSorting,
                        sortMode = sortingMode.name,
                        searchText = searchText)
                }
                composable<IngredientDetails> { backStackEntry ->
                    val ingredientDetails: IngredientDetails = backStackEntry.toRoute()
                    val item by viewModel.getItem(ingredientDetails.id).collectAsState()
                    val itemAisle by viewModel.getItemAisle(item.itemId).collectAsState()
                    val currentStore by viewModel.selectedStore.collectAsState()
                    val stores by viewModel.stores.collectAsState()
                    val store = stores.first {s -> s.storeId == currentStore}
                    val aisles by viewModel.getAislesAtStore(currentStore).collectAsState()
                    val prep by viewModel.getItemPreparations(item.itemId).collectAsState()
                    IngredientDetails(
                        navHostController = navController,
                        item = item,
                        itemAisle = itemAisle,
                        aisles = aisles,
                        prep = prep,
                        currentStore = store,
                        setStore = viewModel::selectStore,
                        updatePrep = viewModel::updateItemPrep,
                        addPrep = viewModel::addItemPrep,
                        deletePrep = viewModel::deleteItemPrep,
                        checkDeletePrep = viewModel::getUsedItemPreps,
                        setItemName = viewModel::setItemName,
                        setItemAisle = viewModel::updateItemAisle,
                        setItemTemp = viewModel::setItemTemp,
                        setItemDefaultUnits = viewModel::setItemDefaultUnits,
                        stores = stores)
                }
            }
            navigation<Recipes>(startDestination = RecipeList) {
                composable<RecipeList> {
                    val recipes by viewModel.allRecipes.collectAsState()
                    RecipeList(
                        navHostController = navController,
                        recipes = recipes,
                        toggleRecipePin = viewModel::toggleRecipePin,
                        addRecipe = viewModel::addRecipe,
                        deleteRecipe = viewModel::deleteRecipe
                    )
                }
                composable<RecipeDetails> { backStackEntry ->
                    val recipeEdit: RecipeEdit = backStackEntry.toRoute()
                    val recipe by viewModel.getRecipes(recipeEdit.recipeId).collectAsState()
                    RecipeDetails(
                        navHostController = navController,
                        recipe = recipe
                    )
                }
                composable<RecipeEdit> { backStackEntry ->
                    val itemsWithPrep by viewModel.allItemsWithPrep.collectAsState()
                    val recipeEdit: RecipeEdit = backStackEntry.toRoute()
                    val recipe by viewModel.getRecipes(recipeEdit.recipeId).collectAsState()
                    RecipeEdit(
                        navHostController = navController,
                        itemsWithPrep = itemsWithPrep,
                        recipe = recipe,
                        addSection = viewModel::addRecipeSection,
                        deleteSection = viewModel::deleteRecipeSection,
                        setRecipeSectionName = viewModel::setRecipeSectionName,
                        setRecipeItemAmount = viewModel::setRecipeItemAmount,
                        addRecipeEntry = viewModel::addRecipeSectionItem,
                        deleteRecipeEntry = viewModel::deleteRecipeSectionEntry
                    )
                }
                composable<RecipeEditSteps> { backStackEntry ->
                    val recipeEditSteps: RecipeEditSteps = backStackEntry.toRoute()
                    val recipe by viewModel.getRecipes(recipeEditSteps.recipeId).collectAsState()
                    com.coldrifting.sirl.screens.RecipeEditSteps(
                        navHostController = navController,
                        recipe = recipe,
                        setRecipeName = viewModel::setRecipeName,
                        setRecipeSteps = viewModel::setRecipeSteps
                    )
                }
            }
            composable<Cart> {
                Cart(
                    navHostController = navController,
                    getShoppingList = viewModel::getShoppingList
                )
            }
        }
    }
}