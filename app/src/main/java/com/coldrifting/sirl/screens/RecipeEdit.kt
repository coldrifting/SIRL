package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.AlertDialog
import com.coldrifting.sirl.components.IngredientAmountEdit
import com.coldrifting.sirl.components.IngredientSearchDialog
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeList
import com.coldrifting.sirl.components.TextDialog
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.data.entities.ItemX
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.routes.RecipeEditSteps
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEdit(
    navHostController: NavHostController,
    itemsWithPrep: List<ItemX>,
    recipe: RecipeX,
    addSection: (Int, String) -> Unit,
    deleteSection: (Int) -> Unit,
    setRecipeSectionName: (Int, String) -> Unit,
    setRecipeItemAmount: (Int, UnitType, Float) -> Unit,
    addRecipeEntry: (Int?, Int, Int, Int, Int?, UnitType, Float) -> Unit,
    deleteRecipeEntry: (Int) -> Unit,
) {
    var selectedRecipeItem by remember { mutableIntStateOf(-1) }
    var selectedRecipeItemAmount by remember { mutableFloatStateOf(0.0f) }
    var selectedRecipeItemUnitType by remember { mutableStateOf(UnitType.EACHES) }

    var showAmountEditDialog by remember { mutableStateOf(false) }

    var currentSectionId by remember { mutableIntStateOf(-1) }
    var currentSectionEntryId by remember { mutableStateOf<Int?>(null) }

    var showAddIngredientDialog by remember { mutableStateOf(false) }

    var showAddSectionDialog by remember { mutableStateOf(false) }
    var showDeleteSectionDialog by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { recipe.recipeSections.size })
    var selectedTabIndex by remember { mutableIntStateOf(0) }


    var newTabTarget by remember { mutableStateOf<Int?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                navHostController,
                "Edit Recipe Ingredients",
                {
                    IconButton(onClick = { navHostController.navigate(RecipeEditSteps(recipe.recipeId)) }) {
                        Icon(
                            Icons.Default.Edit,
                            "Edit Steps"
                        )
                    }
                })
        },
        bottomBar = { NavBar(navHostController, routeRecipes) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentSectionId = recipe.recipeSections[selectedTabIndex].sectionId
                currentSectionEntryId = null

                showAddIngredientDialog = true
            }) {
                Icon(Icons.Default.Add, "Add Ingredient")
            }
        }
    ) { innerPadding ->

        if (showAddSectionDialog) {
            TextDialog(
                title = "Section Name",
                placeholder = "New Section",
                action = "Add",
                onSuccess = { newTabTarget = recipe.recipeSections.size; addSection.invoke(recipe.recipeId, it) },
                onDismiss = { showAddSectionDialog = false }
            )
        }

        if (showDeleteSectionDialog) {
            AlertDialog(
                title = "Delete Section?",
                dismissText = "Delete",
                onConfirm = {
                    val sectionToDelete = recipe.recipeSections[selectedTabIndex].sectionId
                    selectedTabIndex--
                    deleteSection(sectionToDelete)
                },
                onDismiss = { showDeleteSectionDialog = false }
            ) {
                Text("Are you sure you wish to delete the section titled ${recipe.recipeSections[selectedTabIndex].sectionName}")
            }
        }

        // Adjust recipe item amount dialog
        if (showAmountEditDialog) {
            IngredientAmountEdit(
                placeholderAmount = selectedRecipeItemAmount,
                placeholderUnitType = selectedRecipeItemUnitType,
                onSuccess = { unitType, amount ->
                    setRecipeItemAmount(
                        selectedRecipeItem,
                        unitType,
                        amount
                    )
                },
                onDismiss = { showAmountEditDialog = false })
        }

        // Add recipe item
        if (showAddIngredientDialog) {
            IngredientSearchDialog(
                title = if (currentSectionEntryId == null) "Add Ingredient" else "Replace Ingredient",
                entries = itemsWithPrep.filterNot { item ->
                    var itemRecipeMatch =
                        recipe.recipeSections.first { s -> s.sectionId == currentSectionId }.items.firstOrNull { i -> i.itemId == item.itemId }
                    return@filterNot itemRecipeMatch != null && item.equals(itemRecipeMatch)
                },
                onSuccess = { itemWithPrep ->
                    addRecipeEntry(
                        currentSectionEntryId,
                        recipe.recipeId,
                        currentSectionId,
                        itemWithPrep.itemId,
                        itemWithPrep.itemPrep?.itemPrepId,
                        itemWithPrep.defaultUnits,
                        1.0f
                    )
                },
                onDismiss = { showAddIngredientDialog = false }
            )
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (recipe.recipeSections.isEmpty()) {
                return@Column
            }

            LaunchedEffect(newTabTarget, recipe.recipeSections.size) {
                val savedNewTabTarget = newTabTarget
                if (savedNewTabTarget != null) {
                    if (savedNewTabTarget < recipe.recipeSections.size) {
                        val target: Int = savedNewTabTarget
                        scope.launch {
                            pagerState.animateScrollToPage(target)
                        }
                    }
                }
            }

            LaunchedEffect(pagerState.targetPage) {
                selectedTabIndex = pagerState.targetPage
            }

            PrimaryTabRow(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTabIndex = selectedTabIndex,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        width = 110.dp,
                        modifier = Modifier.tabIndicatorOffset(
                            selectedTabIndex,
                            matchContentSize = true
                        )
                    )
                }
            ) {
                if (recipe.recipeSections.size > 1) {
                    recipe.recipeSections.forEachIndexed { index, section ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                selectedTabIndex = index
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(all = 8.dp),
                                text = section.sectionName
                            )
                        }
                    }
                }
            }

            HorizontalPager(modifier = Modifier.fillMaxHeight(), state = pagerState) { page ->

                var section = recipe.recipeSections[page]


                SwipeList(
                    listItems = section.items,
                    getKey = { it.recipeEntryId },
                    tapAction = {
                        currentSectionId = section.sectionId
                        currentSectionEntryId = it

                        showAddIngredientDialog = true
                    },
                    margin = 16.dp,
                    rightAction = swipeDeleteAction { deleteRecipeEntry(it) },
                    cornerRadius = 6.dp,
                    rowPadding = PaddingValues(start = 16.dp, end = 8.dp),
                    spacing = 6.dp,
                    top = if (recipe.recipeSections.size > 1) {
                        {
                            TextFieldWithDebounce(
                                modifier = Modifier.fillMaxWidth(),
                                obj = section,
                                label = "Section Name",
                                getId = { section -> section.sectionId },
                                getName = { section -> section.sectionName },
                                setName = setRecipeSectionName
                            )
                            if (section.items.isEmpty()) {
                                Row(
                                    modifier = Modifier.padding(top = 32.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text("No Ingredients Added")
                                }
                            }
                        }
                    } else if (section.items.isEmpty()) {
                        {
                            Row(
                                modifier = Modifier.padding(top = 32.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text("No Ingredients Added")
                            }
                        }
                    } else
                        null,
                    bottom = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    showAddSectionDialog = true
                                }
                            ) {
                                Text("Add Section")
                            }

                            if (recipe.recipeSections.size > 1 && section != recipe.recipeSections[0]) {
                                Button(
                                    colors = ButtonDefaults.elevatedButtonColors()
                                        .copy(
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        ),
                                    onClick = {
                                        showDeleteSectionDialog = true
                                    }
                                ) {
                                    Text("Delete Section")
                                }
                            }
                        }
                    }
                ) {
                    Text(it.itemName)
                    Spacer(Modifier.weight(1f))
                    if (it.itemPrep != null) {
                        Text(
                            it.itemPrep.prepName,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.width(80.dp),
                        colors = ButtonDefaults.elevatedButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(CornerSize(6.dp)),
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        onClick = {
                            selectedRecipeItem = it.recipeEntryId
                            selectedRecipeItemAmount = it.amount
                            selectedRecipeItemUnitType = it.unitType

                            showAmountEditDialog = true
                        }
                    ) {
                        Text(getPrepAbbreviation(it.unitType, it.amount))
                    }
                }

            }
        }


    }
}


fun getPrepAbbreviation(unitType: UnitType, amount: Float): String {
    // Halves -> 0.5
    // Thirds -> 0.33, 0.66
    // Fourths -> 0.25, 0.75,
    // Fifths -> 0.2, 0.4, 0.6, 0.8
    // Sixths -> 0.166, 0.833
    // Eights -> 0.125, 0.325, 0.625, 0.875
    val amountInt: String = if (amount.toInt() >= 1) amount.toInt().toString() + " " else ""
    val amountFraction: Float = amount - amount.toInt()
    val amountAbbrev = when (round(amountFraction * 1000).toInt()) {
        500 -> "$amountInt½"

        330 -> "$amountInt⅓"
        660 -> "$amountInt⅔"

        250 -> "$amountInt¼"
        750 -> "$amountInt¾"

        200 -> "$amountInt⅕"
        400 -> "$amountInt⅖"
        600 -> "$amountInt⅗"
        800 -> "$amountInt⅘"

        166 -> "$amountInt⅙"
        833 -> "$amountInt⅚"

        125 -> "$amountInt⅛"
        325 -> "$amountInt⅜"
        625 -> "$amountInt⅝"
        875 -> "$amountInt⅞"

        // Trim leading 0
        else -> BigDecimal(amount.toString()).setScale(3, RoundingMode.HALF_UP).toString()
            .trimEnd { it == '0' }.trimEnd { it == '.' }
    }

    val plural = amount > 1

    val unitAbbrev: String = when (unitType) {
        UnitType.EACHES -> "ea."
        UnitType.Teaspoons -> "tsp"
        UnitType.Tablespoons -> "Tbsp"
        UnitType.Cups -> if (plural) "cups" else "cup"
        UnitType.Quarts -> "qt."
        UnitType.Pints -> "pt."
        UnitType.Gallons -> "gal."
        UnitType.Ounces -> "oz."
        UnitType.Pounds -> if (plural) "lbs" else "lb"
    }


    return "$amountAbbrev $unitAbbrev"
}