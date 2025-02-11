package com.coldrifting.sirl

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalView
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.coldrifting.sirl.ui.theme.SIRLTheme
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.random.Random

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
    val faves = remember { mutableStateListOf<Int>() }
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
                onClick = {list.add(ListItem(++i, getStoreNameString())) },
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
        val view = LocalView.current

        val lazyListState: LazyListState = rememberLazyListState()
        val reorderableLazyListState: ReorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            list.add(to.index, list.removeAt(from.index))

            ViewCompat.performHapticFeedback(
                view,
                HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
            )
        }


        val context = LocalContext.current
        SwipeRadioButtonList(
            modifier = Modifier.padding(innerPadding),
            list = list,
            favorites = faves,
            onEdit = { Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show() })

        return@Scaffold

        /*
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            state = lazyListState)
        {
            items(count = list.size, key = { index -> list[index].id }) { index ->
                Log.d("", "CurrentIndexList: $index")
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = list[index].id
                ) { isDragging ->
                    val elevation: Dp by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    val color: Color by animateColorAsState(if (index % 2 == 0 || isDragging) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer)

                    val addAction = SwipeTapAction(
                        Color.White,
                        com.coldrifting.sirl.ui.theme.EditColor,
                        Icons.Default.Edit,
                        {},
                        true,
                        "Edit"
                    )
                    val delAction = SwipeTapAction(
                        Color.White,
                        com.coldrifting.sirl.ui.theme.DelColor,
                        Icons.Default.Delete,
                        { list.removeAt(index) },
                        false,
                        "Delete"
                    )

                    Surface(color = color, shadowElevation = elevation) {
                        SwipeRevealItem(
                            leftAction = addAction,
                            rightAction = delAction
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(color)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = list[index].text)
                                Spacer(Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_START
                                            )
                                        },
                                        onDragStopped = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_END
                                            )
                                        },
                                    ),
                                    onClick = {},
                                ) {
                                    Icon(Icons.Rounded.Menu, contentDescription = "Reorder")
                                }
                            }
                        }

                    }
                }
            }
        }
         */
    }
}

fun getStoreNameString(): String {
    val list = listOf("Smiths", "Maceys", "Harmons", "Fresh Market", "WinCo", "Albersons")

    val num = Random.nextInt(0, list.size - 1)

    return list[num]
}