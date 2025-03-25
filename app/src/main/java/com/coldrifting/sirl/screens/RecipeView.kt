package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes
import kotlinx.coroutines.delay

@Composable
fun RecipeView(
    navHostController: NavHostController,
    recipe: Recipe,
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
            var text by remember(recipe) { mutableStateOf(recipe.recipeName) }

            TextField(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                value = text,
                singleLine = true,
                onValueChange = { text = it },
                label = { Text("Recipe Name") }
            )

            // Debounce name changes to item
            LaunchedEffect(key1 = text) {
                if (text.trim() == recipe.recipeName)
                    return@LaunchedEffect

                delay(500)

                setRecipeName(recipe.recipeId, text)
            }
        }
    }
}