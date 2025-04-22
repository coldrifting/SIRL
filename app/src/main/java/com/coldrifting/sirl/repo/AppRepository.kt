package com.coldrifting.sirl.repo

import android.content.Context
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.access.AisleDAO
import com.coldrifting.sirl.data.access.ItemAisleDAO
import com.coldrifting.sirl.data.access.ItemDAO
import com.coldrifting.sirl.data.access.ItemPrepDAO
import com.coldrifting.sirl.data.access.RecipeDAO
import com.coldrifting.sirl.data.access.StoreDAO
import com.coldrifting.sirl.db.StoresRepo
import com.coldrifting.sirl.util.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

class AppRepository(
    database: Database,
    internal val scope: CoroutineScope,
    storeDao: StoreDAO,
    aisleDao: AisleDAO,
    itemDao: ItemDAO,
    itemAisleDao: ItemAisleDAO,
    itemPrepDao: ItemPrepDAO,
    recipeDao: RecipeDAO,
    context: Context
) {
    val selectedStoreId = database.storesQueries.selected()
        .asFlow()
        .mapToOne(scope.coroutineContext)
        .map{s -> s.toInt()}
        .toStateFlow(scope, 0)

    val store = StoresRepo(
        scope = scope,
        storesQueries = database.storesQueries,
        aislesQueries = database.aislesQueries,
        selectedStoreId = selectedStoreId
    )

    val storeOld = StoreRepository(
        scope = scope,
        storeDao = storeDao,
        aisleDao = aisleDao,
        selectedStoreId = selectedStoreId
    )

    val items = ItemRepository(
        scope = scope,
        itemDao = itemDao,
        itemPrepDao = itemPrepDao,
        itemAisleDao = itemAisleDao,
        selectedStoreId = selectedStoreId
    )

    val recipes = RecipeRepository(
        scope = scope,
        recipeDao = recipeDao
    )

    val cart = CartRepository(
        recipeDao = recipeDao,
        selectedStoreId = selectedStoreId
    )
}