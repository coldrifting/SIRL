package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.data.objects.ChecklistItem
import com.coldrifting.sirl.ui.components.checklist.RecipeDetailsChecklist
import com.coldrifting.sirl.data.objects.RecipeTree
import com.coldrifting.sirl.data.RouteRecipeEdit
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeDetails(
    navHostController: NavHostController,
    recipe: RecipeTree
) {
    val ingredients = recipe.recipeSections.fold(mutableListOf<ChecklistHeader>()) { l, s ->
        l.add(
            ChecklistHeader(
                id = s.sectionId,
                name = s.sectionName,
                items = s.items.fold(mutableListOf<ChecklistItem>()){ lx, i ->
                    lx.add(ChecklistItem(
                        id = i.itemId,
                        name = i.itemName,
                        info = i.itemPrep?.prepName,
                        details = i.unitType.getPrepAbbreviation(i.amount)
                    ))
                    lx
                }))
        l
    }

    var test = rememberSaveable(recipe) { ingredients }

    Scaffold(
        topBar = {
            AppTopBar(navHostController, recipe.recipeName, topAction = {
                IconButton({
                    navHostController.navigate(
                        RouteRecipeEdit(recipe.recipeId)
                    )
                }) { Icon(Icons.Default.Edit, "Edit") }
            })
        },
        bottomBar = { AppNavBar(navHostController, routeRecipes) },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            RecipeDetailsChecklist(
                entries = test,
                steps = recipe.recipeSteps ?: "No Steps Entered")
        }
    }
}