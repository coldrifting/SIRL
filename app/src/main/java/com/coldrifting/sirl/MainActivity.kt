package com.coldrifting.sirl

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.coldrifting.sirl.ui.theme.SIRLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIRLTheme {
                SmallTopAppBarExample()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample() {

    val context = LocalContext.current

    val list =
        remember { mutableStateListOf(
            ListItem(1, "Maceys (1700 S)"),
            ListItem(2, "WinCo (2100 S)"),
            ListItem(3, "Harmons")) }
    var i = 3

    val openAlertDialog = remember { mutableStateOf(false) }
    val listItem = remember { mutableIntStateOf(-1) }

    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Confirmation registered") // Add logic here to handle confirmation.

                    list.removeAt(listItem.intValue)

                },
                dialogTitle = "Alert dialog example",
                dialogText = "This is an example of an alert dialog with buttons.",
                icon = Icons.Default.Info
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {list.add(ListItem(++i, "blah")) },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Locations")
                }
            )
        },
    ) { innerPadding ->
        SwipeRadioButtonList(
            modifier = Modifier.padding(innerPadding),
            list = list.toMutableList(),
            onEdit = {Toast.makeText(context, "delete$it", Toast.LENGTH_SHORT).show()},
            onDelete = { listItem.intValue = it; openAlertDialog.value = true})
    }
}