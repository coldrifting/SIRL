package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.coldrifting.sirl.components.ListItem
import com.coldrifting.sirl.screens.StoreAisleList
import com.coldrifting.sirl.screens.StoreList
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
object Stores

@Serializable
object StoreList

@Serializable
data class StoreAisleList(val id: Int)

@Serializable
object Ingredients

@Serializable
object Recipes

@Serializable
object Cart

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Stores", Stores, Icons.Filled.Place),
    TopLevelRoute("Ingredients", Ingredients, Icons.Filled.Menu),
    TopLevelRoute("Recipes", Recipes, Icons.Filled.Star),
    TopLevelRoute("Cart", Cart, Icons.Filled.ShoppingCart),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppViewModel by viewModels()
            val editAction by viewModel.editAction.collectAsState()
            val navController = rememberNavController()
            val title by viewModel.title.collectAsState()
            SIRLTheme {
                Scaffold(
                    topBar = { TopBar(navController, title, editAction) },
                    bottomBar = { NavBar(navController) },
                    floatingActionButton = { Fab(viewModel, navController) },
                    content = { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(navController = navController, startDestination = Stores) {
                                navigation<Stores>(startDestination = StoreList) {
                                    composable<StoreList> { StoreList(navController, viewModel) }
                                    composable<StoreAisleList> { backStackEntry ->
                                        val aisleList: StoreAisleList = backStackEntry.toRoute()

                                        StoreAisleList(aisleList.id, viewModel) }
                                }
                                composable<Ingredients> { Text("Ingredients") }
                                composable<Recipes> { Text("Recipes") }
                                composable<Cart> { Text("Cart") }
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, title: String, editAction: (() -> Unit)?) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    TopAppBar(
        title = {
            Box(modifier = Modifier.padding(4.dp)) {Text(title)}
        },
        navigationIcon = {
            key(navBackStackEntry) {
                if (navController.previousBackStackEntry != null) {
                    IconButton(modifier = Modifier.padding(4.dp),
                        onClick = {navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            }
                         },
        actions = {
            if (editAction != null) {
                Box(modifier = Modifier.padding(4.dp)) {
                    IconButton(onClick = editAction) {
                        Icon(Icons.Filled.Edit, "Edit")
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                label = { Text(topLevelRoute.name) },
                icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                selected = navBackStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(topLevelRoute.route::class)
                } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = false
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Fab(viewModel: AppViewModel, navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            viewModel.addAisle(0, getStoreNameString())
            //viewModel.addStore(getStoreNameString())
        }
    ) {
        Icon(Icons.Filled.Add, "Add")
    }
}

fun getStoreNameString(): String {
    val list = listOf("Smiths", "Maceys", "Harmons", "Fresh Market", "WinCo", "Albersons")

    val num = Random.nextInt(0, list.size - 1)

    return list[num]
}

fun getNextListId(list: List<ListItem>): Int {
    if (list.isNotEmpty()) {
        return (list.maxOfOrNull { it.id } ?: -1) + 1
    }
    return 0
}