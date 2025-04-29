package com.coldrifting.sirl.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.coldrifting.sirl.data.objects.RecipeTreeItem

@Composable
fun IngredientSearchDialog(
    title: String = "Add Ingredient",
    entries: List<RecipeTreeItem>,
    onSuccess: (RecipeTreeItem) -> Unit,
    onDismiss: () -> Unit
) {
    SearchDialog(
        title = title,
        textLabel = "Selected Ingredient",
        entries = entries,
        onSuccess = onSuccess,
        onDismiss = onDismiss)
}