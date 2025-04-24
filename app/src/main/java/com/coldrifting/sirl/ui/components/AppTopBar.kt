package com.coldrifting.sirl.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.ui.theme.SIRLTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navHostController: NavHostController,
    title: String,
    titleAction: (() -> Unit)? = null,
    topAction: @Composable (() -> Unit)? = null
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    TopAppBar(
        title = {
            Box(
                modifier = (if (titleAction != null)
                                Modifier.clickable { titleAction.invoke() }
                            else Modifier).padding(4.dp)
            ) { Text(title) }
        },
        navigationIcon = {
            key(navBackStackEntry) {
                if (navHostController.previousBackStackEntry != null) {
                    IconButton(
                        modifier = Modifier.padding(4.dp),
                        onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            }
        },
        actions = {
            topAction?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppTopBarPreview() {
    SIRLTheme {
        AppTopBar(navHostController = rememberNavController(), title = "Cart")
    }
}