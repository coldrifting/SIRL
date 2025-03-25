package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeView(
    navHostController: NavHostController,
    recipe: RecipeX,
    setRecipeName: (Int, String) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController, "Recipe Details") },
        bottomBar = { NavBar(navHostController, routeRecipes) }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            TextFieldWithDebounce(
                obj = recipe,
                label = "Recipe Name",
                getId = { it.recipeId },
                getName = { it.recipeName },
                setRecipeName
            )

            // TODO - Make this look somewhat like the figma and allow for edits
            recipe.recipeSections.forEach { section ->
                Section(section.sectionName) {
                    section.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val itemPrep = item.itemPrep?.prepName
                            val itemWithPrep =
                                if (itemPrep != null) item.itemName + " - " + itemPrep
                                else item.itemName

                            Text(itemWithPrep)
                            Text(item.unitType.toString())
                            Text(item.amount.toString())
                        }
                    }
                }
            }

        }
    }
}
