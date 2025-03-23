package com.coldrifting.sirl.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDetails(
    navHostController: NavHostController,
    title: String,
    itemId: Int,
    getItemName: (Int) -> String)
{
    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController, routeIngredients) },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                    value = getItemName(itemId),
                    onValueChange = {},
                    label = {Text("Ingredient Name")}
                    )

                Section("Location and Temperature") {
                    DropDown(listOf("Macey's (1700 S)", "WinCo (2100 S)"), "Current Store")

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        DropDown(listOf("Ambient", "Chilled", "Frozen"), "Temp", 110.dp)
                        DropDown(listOf("Aisle 1", "Bakery", "Produce", "Back Wall"), "Aisle", 110.dp)
                        DropDown(listOf("None", "Bay 1", "Bay 2", "Bay 3"), "Bay", 110.dp)
                    }
                }

                Section("Package Details") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DropDown(listOf("Bag(s)", "Box(es)"), "Package Type", 260.dp)

                        TextField(
                            modifier = Modifier.width(100.dp),
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                                // works like onClick
                                                Log.d("TEST", "Button clicked")
                                            }
                                        }
                                    }
                                },
                            label = {Text("Amount")},
                            value = "16 OZ",
                            onValueChange = {},
                            readOnly = true
                        )
                    }
                }

                Section("Preparations") {
                    TextField(modifier = Modifier.fillMaxWidth(), label = {Text("Preparation")}, value = "Preparation 1", onValueChange = {})
                }
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

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    HorizontalDivider(thickness = 2.dp)

    Text(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp), text = title, fontSize = 18.sp)

    content()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DropDown(list: List<String>, label: String, width: Dp? = null) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = if (width != null) Modifier.width(width)
                    else Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {expanded = it})
    {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
            value = list.first(),
            readOnly = true,
            label = { Text(label) },
            onValueChange = {})
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false })
        {
            list.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { expanded = false }
                )
            }
        }
    }
}