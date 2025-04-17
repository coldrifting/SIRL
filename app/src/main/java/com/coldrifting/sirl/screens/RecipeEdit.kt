package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.Section
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.routes.RecipeEditSteps
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeEdit(
    navHostController: NavHostController,
    recipe: RecipeX,
    setRecipeName: (Int, String) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController, "Edit Recipe Ingredients") },
        bottomBar = { NavBar(navHostController, routeRecipes) },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = {navHostController.navigate(RecipeEditSteps(recipe.recipeId))}, icon = {Icon(Icons.Default.Edit,"Edit Steps")}, text = { Text("Edit Steps") })  }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .positionAwareImePadding()
                .verticalScroll(scrollState)
        ) {
            TextFieldWithDebounce(
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                obj = recipe,
                label = "Recipe Name",
                getId = { it.recipeId },
                getName = { it.recipeName },
                setName = setRecipeName
            )

            // TODO - Make this look somewhat like the figma and allow for edits
            recipe.recipeSections.forEach { section ->
                Section(title = section.sectionName, indentLevel = 0) {
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
