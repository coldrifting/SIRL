package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.Section
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeEditSteps(
    navHostController: NavHostController,
    recipe: RecipeX,
    setRecipeName: (Int, String) -> Unit,
    setRecipeSteps: (Int, String) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController, "Edit Recipe Name & Steps") },
        bottomBar = { NavBar(navHostController, routeRecipes) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TextFieldWithDebounce(
                modifier = Modifier.padding(all = 16.dp).fillMaxWidth(),
                obj = recipe,
                label = "Recipe Name",
                getId = { it.recipeId },
                getName = { it.recipeName },
                setName = setRecipeName
            )

            TextFieldWithDebounce(
                modifier = Modifier.positionAwareImePadding().padding(all = 12.dp).fillMaxSize(),
                obj = recipe,
                label = "Steps",
                getId = {it.recipeId},
                getName = {it.recipeSteps ?: ""},
                setName = setRecipeSteps,
                singleLine = false
            )
        }
    }
}
