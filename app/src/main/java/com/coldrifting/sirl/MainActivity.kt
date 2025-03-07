package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.getStoreNameString
import com.coldrifting.sirl.screens.StoreAisleList
import com.coldrifting.sirl.screens.StoreList
import com.coldrifting.sirl.ui.theme.SIRLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIRLTheme {
                val viewModel: AppViewModel by viewModels()
                MainContent(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: AppViewModel) {
    val navController = rememberNavController()
    var title by remember { mutableStateOf("") }
    var addAction by remember { mutableStateOf({})}
    Scaffold(
        topBar = { TopBar(navController, title) },
        bottomBar = { NavBar(navController) },
        floatingActionButton = { Fab(addAction = addAction) },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(navController = navController, startDestination = Stores) {
                    navigation<Stores>(startDestination = StoreList) {
                        composable<StoreList> {
                            title = getRouteName(StoreList, viewModel)
                            addAction = { viewModel.addStore(getStoreNameString())}
                            StoreList(navController, viewModel)
                        }
                        composable<StoreAisleList> { backStackEntry ->
                            val aisleList: StoreAisleList = backStackEntry.toRoute()
                            title = getRouteName(aisleList, viewModel)
                            addAction = { viewModel.addAisle(aisleList.id, getStoreNameString())}
                            StoreAisleList(aisleList.id, viewModel)
                        }
                    }
                    composable<Ingredients> {
                        title = getRouteName(Ingredients, viewModel)
                        Text(title)
                    }
                    composable<Recipes> {
                        title = getRouteName(Recipes, viewModel)
                        Text(title)
                    }
                    composable<Cart> {
                        title = getRouteName(Cart, viewModel)
                        Text(title)
                    }
                }
            }
        }
    )
}