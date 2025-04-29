package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.GetAvailableItemsForCart
import com.coldrifting.sirl.GetAvailableRecipesForCart
import com.coldrifting.sirl.data.RouteCartList
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeCart
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.objects.Amount
import com.coldrifting.sirl.data.objects.CartItemSelect
import com.coldrifting.sirl.data.objects.CartRecipeSelect
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.dialogs.IngredientAmountEdit
import com.coldrifting.sirl.ui.components.dialogs.ItemSearchDialog
import com.coldrifting.sirl.ui.components.dialogs.RecipeSearchDialog
import com.coldrifting.sirl.ui.components.swipe.SwipeList
import com.coldrifting.sirl.ui.components.swipe.swipeDeleteAction

@Composable
fun CartSelect(
    navHostController: NavHostController,
    generateCartList: () -> Unit,
    cartItemsDone: Int,
    cartItemsTotal: Int,
    cartRecipes: List<CartRecipeSelect>,
    cartItems: List<CartItemSelect>,
    availableEntries: List<GetAvailableRecipesForCart>,
    availableItemEntries: List<GetAvailableItemsForCart>,
    onAddRecipeToCart: (Int) -> Unit,
    onDeleteCartRecipe: (Int) -> Unit,
    updateRecipeInCart: (Int, Int) -> Unit,
    clearCartSelection: () -> Unit,
    onAddItemToCart: (Int, Amount) -> Unit,
    onDeleteCartItem: (Int) -> Unit,
    getDefaultItemType: (Int) -> UnitType,
    updateItemInCart: (Int, Amount) -> Unit
) {
    var selectedItemId by remember { mutableIntStateOf(-1) }
    var selectedItemAmount by remember { mutableStateOf<Amount>(Amount(1)) }
    var showAmountEditDialog by remember { mutableStateOf(false) }
    if (showAmountEditDialog) {
        IngredientAmountEdit(
            placeholderAmount = selectedItemAmount,
            onSuccess = { amount -> updateItemInCart.invoke(selectedItemId, amount) },
            onDismiss = { showAmountEditDialog = false })
    }


    var showRecipeSelect by remember { mutableStateOf(false) }
    if (showRecipeSelect) {
        RecipeSearchDialog(
            availableEntries,
            {onAddRecipeToCart.invoke(it.recipeId)},
            {showRecipeSelect = false}
        )
    }

    var showItemSelect by remember { mutableStateOf(false) }
    if (showItemSelect) {
        ItemSearchDialog(
            availableItemEntries,
            {
                onAddItemToCart.invoke(it.itemId, Amount(1, getDefaultItemType(it.itemId)))
            },
            {showItemSelect = false}
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                navHostController,
                "Create New List",
                topAction = {IconButton(onClick = clearCartSelection) { Icon(Icons.Default.Clear, "Clear Selection") }}
            )
        },
        bottomBar = { AppNavBar(navHostController, routeCart) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                generateCartList.invoke()
                navHostController.navigate(RouteCartList)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Go")
            }
        }

    ) { innerPadding ->
        SwipeList(
            modifier = Modifier.padding(innerPadding),
            listItems = listOf("Recipes") + cartRecipes + listOf("Items") + cartItems,
            rightActionMap = mapOf(
                Pair(CartRecipeSelect::class.simpleName!!, swipeDeleteAction { onDeleteCartRecipe.invoke(it - 100_000) }),
                Pair(CartItemSelect::class.simpleName!!, swipeDeleteAction { onDeleteCartItem.invoke(it) })
            ),
            getKey = {
                when (it) {
                    is CartRecipeSelect -> it.recipeId + 100_000
                    is CartItemSelect -> it.itemId
                    else -> it.hashCode()
                }},
            top = if (cartItemsDone > 0) {{

            Surface(tonalElevation = 1.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            navHostController.navigate(RouteCartList)
                        })
                        .padding(16.dp)
                ) {
                    Text("Continue Existing Cart")
                    Spacer(Modifier.weight(1f))
                    Text("($cartItemsDone/$cartItemsTotal)")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Continue Cart")
                }
            }
            }} else null
        ) {
            when (it) {
                is CartRecipeSelect -> {
                    Spacer(Modifier.width(8.dp))
                    Text(it.recipeName)
                    Spacer(Modifier.weight(1f))
                    if (it.recipeQuantity > 1) {
                        IconButton(onClick = {
                            updateRecipeInCart.invoke(it.recipeId, it.recipeQuantity - 1)
                        }) { Icon(Icons.Default.KeyboardArrowDown, "Decrease") }
                    }
                    Text(it.recipeQuantity.toString())
                    IconButton(onClick = {
                        updateRecipeInCart.invoke(it.recipeId, it.recipeQuantity + 1)
                    }) { Icon(Icons.Default.KeyboardArrowUp, "Increase") }
                }
                is CartItemSelect -> {
                    Spacer(Modifier.width(8.dp))
                    Text(it.itemName)
                    Spacer(Modifier.weight(1f))

                    Surface(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .padding(vertical = 8.dp),
                        tonalElevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            selectedItemId = it.itemId
                            selectedItemAmount = it.amount

                            showAmountEditDialog = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(it.amount.toString())
                        }
                    }
                }
                is String -> {
                    Text(it)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = if (it == "Recipes") {{
                        showRecipeSelect = true
                    }} else {{
                        showItemSelect = true
                    }}
                    ) {
                        Icon(Icons.Default.Add, "Add")
                    }
                }
            }
        }
    }
}