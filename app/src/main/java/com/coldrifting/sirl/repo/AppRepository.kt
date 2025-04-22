package com.coldrifting.sirl.repo

import android.content.Context
import com.coldrifting.sirl.data.access.AisleDAO
import com.coldrifting.sirl.data.access.ItemAisleDAO
import com.coldrifting.sirl.data.access.ItemDAO
import com.coldrifting.sirl.data.access.ItemPrepDAO
import com.coldrifting.sirl.data.access.RecipeDAO
import com.coldrifting.sirl.data.access.StoreDAO
import com.coldrifting.sirl.util.toStateFlow
import kotlinx.coroutines.CoroutineScope

class AppRepository(
    internal val scope: CoroutineScope,
    storeDao: StoreDAO,
    aisleDao: AisleDAO,
    itemDao: ItemDAO,
    itemAisleDao: ItemAisleDAO,
    itemPrepDao: ItemPrepDAO,
    recipeDao: RecipeDAO,
    context: Context
) {
    val selectedStoreId = storeDao.selected().toStateFlow(scope, 0)

    val store = StoreRepository(
        scope = scope,
        context = context,
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
        scope = scope,
        recipeDao = recipeDao,
        selectedStoreId = selectedStoreId
    )
}