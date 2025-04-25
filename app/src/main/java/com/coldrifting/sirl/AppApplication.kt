package com.coldrifting.sirl

import android.app.Application
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemAisle
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.data.entities.RecipeEntry
import com.coldrifting.sirl.data.entities.RecipeSection
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

class AppApplication : Application() {

    val json = Json{
        coerceInputValues = true
        explicitNulls = false
    }

    fun getAssetText(fileName: String): String {
        return try {
            this.applicationContext.assets.open("database/$fileName.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (_: Exception) {
            "[]"
        }
    }

    inline fun <reified T : Insertable> populateEntries(database: Database) {

        val fileName = T::class.simpleName
            ?.plus("s")
            ?.replace(Regex("ys$"), "ies")
            ?.replaceFirstChar { it.lowercase() }

        if (fileName == null) {
            return
        }

        val str = getAssetText(fileName)

        val entries = json.decodeFromString<List<T>>(str)
        entries.forEach { it.insert(database) }
    }

    private val scope = CoroutineScope(SupervisorJob())

    private val db by lazy {
        val dbName = "database.db"
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
                temperatureAdapter = ItemTemp.Adapter,
                defaultUnitsAdapter = UnitType.Adapter,
            ),
            ItemPrepsAdapter = ItemPreps.Adapter(
                itemPrepIdAdapter = IntColumnAdapter,
                itemIdAdapter = IntColumnAdapter
            ),
            ItemAislesAdapter = ItemAisles.Adapter(
                itemIdAdapter = IntColumnAdapter,
                storeIdAdapter = IntColumnAdapter,
                aisleIdAdapter = IntColumnAdapter,
                bayAdapter = BayType.Adapter
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
                unitTypeAdapter = UnitType.Adapter,
                amountAdapter = IntColumnAdapter
            ),
            CartHeadersAdapter = CartHeaders.Adapter(
                headerIdAdapter = IntColumnAdapter
            ),
            CartItemsAdapter = CartItems.Adapter(
                cartItemIdAdapter = IntColumnAdapter,
                headerIdAdapter = IntColumnAdapter
            )
        )

        if (seedDatabase) {
            populateEntries<Store>(database)
            populateEntries<Aisle>(database)
            populateEntries<Item>(database)
            populateEntries<ItemAisle>(database)
            populateEntries<ItemPrep>(database)
            populateEntries<Recipe>(database)
            populateEntries<RecipeSection>(database)
            populateEntries<RecipeEntry>(database)
        }

        database
    }

    val appRepository by lazy { AppRepo(db, scope) }
}