package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.IngredientAmountEdit
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeList
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.routes.RecipeEditSteps
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

@Composable
fun RecipeEdit(
    navHostController: NavHostController,
    recipe: RecipeX,
    setRecipeName: (Int, String) -> Unit,
    setRecipeSectionName: (Int, String) -> Unit,
    setRecipeItemAmount: (Int, UnitType, Float) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController, "Edit Recipe Ingredients", {IconButton(onClick = {navHostController.navigate(RecipeEditSteps(recipe.recipeId))}) { Icon(Icons.Default.Edit, "Edit Steps") } }) },
        bottomBar = { NavBar(navHostController, routeRecipes) },
    ) { innerPadding ->
        var selectedRecipeItem by remember { mutableIntStateOf(-1) }
        var selectedRecipeItemAmount by remember { mutableFloatStateOf(0.0f) }
        var selectedRecipeItemUnitType by remember {mutableStateOf(UnitType.EACHES)}

        var showAmountEditDialog by remember { mutableStateOf(false) }
        if (showAmountEditDialog) {
            IngredientAmountEdit(
                placeholderAmount = selectedRecipeItemAmount,
                placeholderUnitType = selectedRecipeItemUnitType,
                onSuccess = { unitType, amount -> setRecipeItemAmount(selectedRecipeItem, unitType, amount) },
                onDismiss = { showAmountEditDialog = false })
        }

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
                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                TextFieldWithDebounce(
                    modifier = Modifier.padding(top = 20.dp, bottom = 16.dp).fillMaxWidth(),
                    obj = section,
                    label = "Section Name",
                    getId = {it.sectionId},
                    getName = {it.sectionName},
                    setName = setRecipeSectionName
                )

                SwipeList(
                    modifier = Modifier.padding(bottom = 12.dp),
                    listItems = section.items,
                    getKey = {it.recipeEntryId},
                    scroll = false,
                    rightAction = swipeDeleteAction {  },
                    cornerRadius = 6.dp,
                    rowPadding = PaddingValues(start = 16.dp, end = 8.dp),
                    spacing = 6.dp,
                    addListItemElement = {
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.elevatedButtonColors()
                                .copy(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(CornerSize(6.dp)),
                        ) {
                            Text("Add Ingredient")
                        }
                        Spacer(Modifier.weight(1f))
                    }
                ) {
                    val itemPrep = it.itemPrep?.prepName
                    val itemWithPrep =
                        if (itemPrep != null) it.itemName + " - " + itemPrep
                        else it.itemName

                    Text(itemWithPrep)
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier.width(80.dp),
                        colors = ButtonDefaults.elevatedButtonColors().copy(contentColor = MaterialTheme.colorScheme.onSurface, containerColor = MaterialTheme.colorScheme.surface),
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
        else -> BigDecimal(amount.toString()).setScale(3, RoundingMode.HALF_UP).toString().trimEnd{it == '0'}.trimEnd{it == '.'}
    }

    val plural = amount > 1

    val unitAbbrev: String = when(unitType) {
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