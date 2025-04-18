package com.coldrifting.sirl.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.HtmlView
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.Section
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeDetails(
    navHostController: NavHostController,
    recipe: RecipeX
) {
    Scaffold(
        topBar = { TopBar(navHostController, recipe.recipeName, { IconButton({navHostController.navigate(
            com.coldrifting.sirl.routes.RecipeEdit(recipe.recipeId)
        )}) {Icon(Icons.Default.Edit, "Edit")} }) },
        bottomBar = { NavBar(navHostController, routeRecipes) },
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            var checked by remember { mutableStateOf<Map<Int, Boolean>>(mapOf()) }

            val nonEmptySections: MutableList<Int> = mutableListOf()
            recipe.recipeSections.forEachIndexed { i, r ->
                if (r.items.isNotEmpty()) {
                    nonEmptySections += i
                }
            }

            if (nonEmptySections.size == 1) {
                Section(
                    title = "Ingredients",
                    collapsable = true,
                    startExpanded = true,
                    indentLevel = 1
                ) {
                    recipe.recipeSections[nonEmptySections.first()].items.forEach { i ->
                        Row(
                                modifier = Modifier
                                    .clickable {  checked += Pair(i.recipeEntryId,
                                        checked[i.recipeEntryId] != true
                                    ) }
                                    .padding(vertical = 12.dp)
                                    .padding(start = 32.dp, end = 16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = i.itemName)
                                if (i.itemPrep != null) {
                                    Text(
                                        text = " (${i.itemPrep.prepName})",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Checkbox(
                                    modifier = Modifier.padding(0.dp),
                                    checked = checked[i.recipeEntryId] == true,
                                    onCheckedChange = null
                                )
                            }
                    }
                }
            }
            else {
            Section(
                title = "Ingredients",
                collapsable = true,
                startExpanded = true,
                indentLevel = 1,
                subContent = recipe.recipeSections.filter { r -> r.items.isNotEmpty() }.map { r ->
                    Pair(
                        r.sectionName
                    ) { m ->
                        r.items.forEach { i ->
                            Row(
                                modifier = Modifier
                                    .clickable {  checked += Pair(i.recipeEntryId,
                                        checked[i.recipeEntryId] != true
                                    ) }
                                    .padding(vertical = 12.dp)
                                    .then(m)
                                    .fillMaxWidth()
                            ) {
                                Text(text = i.itemName)
                                if (i.itemPrep != null) {
                                    Text(
                                        text = " (${i.itemPrep.prepName})",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Checkbox(
                                    modifier = Modifier.padding(0.dp),
                                    checked = checked[i.recipeEntryId] == true,
                                    onCheckedChange = null
                                )
                            }
                        }

                    }
                }
            ) {}
            }
            Section(
                title = "Steps",
                collapsable = true,
                indentLevel = 1
            ) {
                HtmlView(recipe.recipeSteps)
            }
        }
    }
}