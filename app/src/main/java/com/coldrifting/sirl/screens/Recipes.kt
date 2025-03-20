package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.routeRecipes

@Composable
fun Recipes(navHostController: NavHostController, title: String) {
    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController, routeRecipes) },
        floatingActionButton = { Fab(addAction = {})},
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Text("Recipes")
            }
        }
    )
}