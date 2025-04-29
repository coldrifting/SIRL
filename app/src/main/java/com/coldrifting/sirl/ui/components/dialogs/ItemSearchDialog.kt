package com.coldrifting.sirl.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.coldrifting.sirl.GetAvailableItemsForCart

@Composable
fun ItemSearchDialog(
    entries: List<GetAvailableItemsForCart>,
    onSuccess: (GetAvailableItemsForCart) -> Unit,
    onDismiss: () -> Unit
) {
    SearchDialog(
        title = "Add Item",
        textLabel = "Selected Item",
        entries = entries,
        toString = {it.itemName},
        onSuccess = onSuccess,
        onDismiss = onDismiss)
}