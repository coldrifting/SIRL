package com.coldrifting.sirl.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes
import com.coldrifting.sirl.ui.theme.SIRLTheme

@Composable
fun Recipes(navHostController: NavHostController) {
    Scaffold(
        topBar = { TopBar(navHostController, "Recipes") },
        bottomBar = { NavBar(navHostController, routeRecipes) },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Text("Recipes")
            }
        }
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RecipesPreview() {
    SIRLTheme {
        Recipes(navHostController = rememberNavController())
    }
}