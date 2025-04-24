package com.coldrifting.sirl

import android.app.Application
import android.content.Context
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemAisle
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.data.entities.RecipeEntry
import com.coldrifting.sirl.data.entities.RecipeSection
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

object BayTypeAdapter : ColumnAdapter<BayType, String> {
    override fun decode(databaseValue: String): BayType = enumValueOf<BayType>(databaseValue)
    override fun encode(value: BayType): String = value.toString()
}

object TempAdapter : ColumnAdapter<ItemTemp, String> {
    override fun decode(databaseValue: String): ItemTemp = enumValueOf<ItemTemp>(databaseValue)
    override fun encode(value: ItemTemp): String = value.toString()
}

object UnitTypeAdapter : ColumnAdapter<UnitType, String> {
    override fun decode(databaseValue: String): UnitType = enumValueOf<UnitType>(databaseValue)
    override fun encode(value: UnitType): String = value.toString()
}


class AppApplication : Application() {

    val json = Json{
        coerceInputValues = true
        explicitNulls = false
    }

    fun getAssetText(context: Context, fileName: String): String {
        return try {
            context.assets.open("database/$fileName.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (_: Exception) {
            "[]"
        }
    }

    inline fun <reified T> populateEntries(context: Context, filename: String, insertAction: (T) -> Unit) {
        val str = getAssetText(context, filename)
        val entries = json.decodeFromString<List<T>>(str)
        entries.forEach(insertAction)
    }

    private val scope = CoroutineScope(SupervisorJob())

    private val db by lazy {
        val dbName = "a_test.db"
        val dbFile = this.applicationContext.getDatabasePath(dbName)

        val seedDatabase = !dbFile.exists()

        val database = Database(
            driver = AndroidSqliteDriver(Database.Schema, applicationContext, dbName),
            AislesAdapter = Aisles.Adapter(
                storeIdAdapter = IntColumnAdapter,
                aisleIdAdapter = IntColumnAdapter,
                sortingPrefixAdapter = IntColumnAdapter
            ),
            StoresAdapter = Stores.Adapter(
                storeIdAdapter = IntColumnAdapter
            ),
            ItemsAdapter = Items.Adapter(
                itemIdAdapter = IntColumnAdapter,
                temperatureAdapter = TempAdapter,
                defaultUnitsAdapter = UnitTypeAdapter,
            ),
            ItemPrepsAdapter = ItemPreps.Adapter(
                itemPrepIdAdapter = IntColumnAdapter,
                itemIdAdapter = IntColumnAdapter
            ),
            ItemAislesAdapter = ItemAisles.Adapter(
                itemIdAdapter = IntColumnAdapter,
                storeIdAdapter = IntColumnAdapter,
                aisleIdAdapter = IntColumnAdapter,
                bayAdapter = BayTypeAdapter
            ),
            RecipesAdapter = Recipes.Adapter(
                recipeIdAdapter = IntColumnAdapter
            ),
            RecipeSectionsAdapter = RecipeSections.Adapter(
                recipeSectionIdAdapter = IntColumnAdapter,
                recipeIdAdapter = IntColumnAdapter,
                sortIndexAdapter = IntColumnAdapter
            ),
            RecipeEntriesAdapter = RecipeEntries.Adapter(
                recipeEntryIdAdapter = IntColumnAdapter,
                recipeIdAdapter = IntColumnAdapter,
                recipeSectionIdAdapter = IntColumnAdapter,
                itemIdAdapter = IntColumnAdapter,
                itemPrepIdAdapter = IntColumnAdapter,
                unitTypeAdapter = UnitTypeAdapter,
                amountAdapter = IntColumnAdapter
            )
        )

        if (seedDatabase) {
            populateEntries<Store>(this.applicationContext, "stores") {
                database.storesQueries.insert(it.storeId, it.storeName, it.selected) }
            populateEntries<Aisle>(this.applicationContext, "aisles") {
                database.aislesQueries.insert(it.aisleId, it.storeId, it.aisleName, it.sortingPrefix) }
            populateEntries<Item>(this.applicationContext, "items") {
                database.itemsQueries.insert(it.itemId, it.itemName, it.itemTemp, it.defaultUnits) }
            populateEntries<ItemAisle>(this.applicationContext, "itemAisles") {
                database.itemAislesQueries.insert(it.itemId, it.storeId, it.aisleId, it.bay) }
            populateEntries<ItemPrep>(this.applicationContext, "itemPreps") {
                 database.itemPrepsQueries.insert(it.itemPrepId, it.itemId, it.prepName) }
            populateEntries<Recipe>(this.applicationContext, "recipes") {
                 database.recipesQueries.insert(it.recipeId, it.recipeName, it.url, it.pinned, it.steps) }
            populateEntries<RecipeSection>(this.applicationContext, "recipeSections") {
                 database.recipeSectionsQueries.insert(it.recipeSectionId, it.recipeId, it.recipeSectionName, it.sortIndex) }
            populateEntries<RecipeEntry>(this.applicationContext, "recipeEntries") {
                 database.recipeEntriesQueries.insert(it.recipeEntryId, it.recipeId, it.recipeSectionId, it.itemId, it.itemPrepId, it.unitType, it.amount) }
        }

        database
    }

    val appRepository by lazy { AppRepo(db, scope) }
}