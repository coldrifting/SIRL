package com.coldrifting.sirl.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.coldrifting.sirl.GetAvailableRecipesForCart

@Composable
fun RecipeSearchDialog(
    entries: List<GetAvailableRecipesForCart>,
    onSuccess: (GetAvailableRecipesForCart) -> Unit,
    onDismiss: () -> Unit
) {
    SearchDialog(
        title = "Add Recipe",
        textLabel = "Selected Recipe",
        entries = entries,
        toString = {it.recipeName},
        onSuccess = onSuccess,
        onDismiss = onDismiss)
}