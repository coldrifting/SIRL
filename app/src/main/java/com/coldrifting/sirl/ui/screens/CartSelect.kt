package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.data.RouteCartList
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeCart
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar

@Composable
fun CartSelect(
    navHostController: NavHostController,
    generateCartList: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                navHostController,
                "Create New List"
            )
        },
        bottomBar = { AppNavBar(navHostController, routeCart) })
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                generateCartList.invoke()
                navHostController.navigate(RouteCartList)
            }) {
                Text("Generate List")
            }
        }
    }
}