package com.coldrifting.sirl.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.coldrifting.sirl.R
import com.coldrifting.sirl.Recipes
import com.coldrifting.sirl.data.RouteRecipeDetails
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeRecipes
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.dialogs.TextDialog
import com.coldrifting.sirl.ui.components.swipe.SwipeList
import com.coldrifting.sirl.ui.components.swipe.swipeDeleteAction
import com.coldrifting.sirl.ui.theme.PinColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipeList(
    navHostController: NavHostController,
    recipes: List<Recipes>,
    toggleRecipePin: (Int) -> Unit,
    addRecipe: (String) -> Unit,
    deleteRecipe: (Int) -> Unit
) {
    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(title = "Add New Recipe",
            placeholder = "Recipe Name",
            action = "Add",
            onSuccess = addRecipe,
            onDismiss = { showNewAlertDialog = false })
    }

    // Delay actual pining so pin icon has a change to animate before the list is re-composited
    var uiPinState by remember(recipes) { mutableStateOf(Pair(-1, false)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(uiPinState) {
        if (recipes.any { r -> r.recipeId == uiPinState.first }) {
            coroutineScope.launch {
                delay(350)
                toggleRecipePin(uiPinState.first)
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(navHostController, "Recipes") },
        bottomBar = { AppNavBar(navHostController, routeRecipes) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showNewAlertDialog = true
            }) {
                Icon(Icons.Filled.Add, "Add New Recipe")
            }
        },
        content = { innerPadding ->
            SwipeList(
                modifier = Modifier.padding(innerPadding),
                listItems = recipes,
                getKey = { it.recipeId },
                rightAction = swipeDeleteAction(deleteRecipe),
                tapAction = { navHostController.navigate(RouteRecipeDetails(it)) }
            ) {
                Text(
                    text = it.recipeName
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        uiPinState = Pair(first = it.recipeId, second = !it.pinned)
                    },
                ) {
                    val starOutlined: ImageVector =
                        ImageVector.vectorResource(id = R.drawable.staroutline)

                    val shouldBePinned = if (uiPinState.first == it.recipeId) {
                        uiPinState.second
                    } else {
                        it.pinned
                    }

                    Crossfade(targetState = shouldBePinned) { isPinned ->
                        if (isPinned) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Unpin",
                                tint = PinColor
                            )
                        } else {
                            Icon(
                                imageVector = starOutlined,
                                contentDescription = "Pin",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    )
}