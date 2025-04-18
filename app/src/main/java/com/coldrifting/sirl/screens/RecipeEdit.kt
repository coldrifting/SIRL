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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeList
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.data.entities.RecipeX
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.routes.RecipeEditSteps
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeRecipes

@Composable
fun RecipeEdit(
    navHostController: NavHostController,
    recipe: RecipeX,
    setRecipeName: (Int, String) -> Unit,
    setRecipeSectionName: (Int, String) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController, "Edit Recipe Ingredients", {IconButton(onClick = {navHostController.navigate(RecipeEditSteps(recipe.recipeId))}) { Icon(Icons.Default.Edit, "Edit Steps") } }) },
        bottomBar = { NavBar(navHostController, routeRecipes) },
    ) { innerPadding ->
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
                Divider(modifier = Modifier.fillMaxWidth())

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
                        onClick = {}
                    ) {
                        Text(getPrepAbbreviation(it.unitType, it.amount))
                    }
                }
            }
        }
    }
}

fun getPrepAbbreviation(unitType: UnitType, amount: Float): String {
    var amountAbbrev = ""
    if (amount >= 1) {
        amountAbbrev = amount.toInt().toString()
    }

    // Halves -> 0.5
    // Thirds -> 0.33, 0.66
    // Fourths -> 0.25, 0.75,
    // Fifths -> 0.2, 0.4, 0.6, 0.8
    // Sixths -> 0.166, 0.833
    // Eights -> 0.125, 0.325, 0.625, 0.875
    val amountFraction: Float = amount - amount.toInt()
    if (amountFraction > 0.005f) {
        amountAbbrev += when (amountFraction) {
            0.5f -> "½"

            0.33f -> "⅓"
            0.66f -> "⅔"

            0.25f -> "¼"
            0.75f -> "¾"

            0.2f -> "⅕"
            0.4f -> "⅖"
            0.6f -> "⅗"
            0.8f -> "⅘"

            0.166f -> "⅙"
            0.833f -> "⅚"

            0.125f -> "⅛"
            0.325f -> "⅜"
            0.625f -> "⅝"
            0.875f -> "⅞"

            // Trim leading 0
            else -> amountFraction.toString().substring(1)
        }
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