package com.coldrifting.sirl.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme

@Composable
fun IngredientDetails(navHostController: NavHostController, title: String, itemId: Int, getItemName: (Int) -> String) {
    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController, routeIngredients) },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Text("Ingredients: ${getItemName(itemId)}")
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IngredientDetailsPreview() {
    SIRLTheme {
        IngredientDetails(
            navHostController = rememberNavController(),
            title = "Ingredients - French Bread",
            itemId = 17,
            getItemName = {"French Bread"})
    }
}