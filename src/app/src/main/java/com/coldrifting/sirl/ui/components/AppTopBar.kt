package com.coldrifting.sirl.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navHostController: NavHostController,
    title: String,
    titleAction: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    topAction: @Composable (() -> Unit)? = null
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    val clickableModifier = if (titleAction != null)
                                Modifier.clip(RoundedCornerShape(6.dp)).clickable { titleAction.invoke() }
                            else Modifier

    TopAppBar(
        title = {
            Box(
                modifier = clickableModifier.padding(6.dp))
            { Text(title) }
        },
        navigationIcon = {
            key(navBackStackEntry) {
                if (navHostController.previousBackStackEntry != null) {
                    IconButton(
                        modifier = Modifier.padding(4.dp),
                        onClick = {
                            if (onBack != null) {
                                onBack.invoke()
                            }
                            else {
                                navHostController.popBackStack()
                            }
                        }) {
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