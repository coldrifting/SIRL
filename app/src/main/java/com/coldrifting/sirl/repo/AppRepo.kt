package com.coldrifting.sirl.repo

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.repo.utils.toStateFlow
import kotlinx.coroutines.CoroutineScope

class AppRepo(
    database: Database,
    internal val scope: CoroutineScope,
) {
    val selectedStoreId = database.storesQueries.selectedStore().toStateFlow(scope, 0) { storeId ->
        storeId.toInt()
    }

    val store = StoreRepo(
        db = database,
        scope = scope,
        selectedStoreId = selectedStoreId
    )

    val items = ItemRepo(
        db = database,
        scope = scope,
        selectedStoreId = selectedStoreId
    )

    val recipes = RecipeRepo(
        db = database,
        scope = scope
    )

    val cart = CartRepo(
        db = database,
        selectedStoreId = selectedStoreId
    )
}