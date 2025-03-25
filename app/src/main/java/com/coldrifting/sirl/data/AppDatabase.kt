package com.coldrifting.sirl.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coldrifting.sirl.data.access.AisleDAO
import com.coldrifting.sirl.data.access.ItemAisleDAO
import com.coldrifting.sirl.data.access.ItemDAO
import com.coldrifting.sirl.data.access.ItemPrepDAO
import com.coldrifting.sirl.data.access.RecipeDAO
import com.coldrifting.sirl.data.access.StoreDAO
import com.coldrifting.sirl.data.access.base.BaseDAO.Companion.populate
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.data.entities.joined.ItemAisle
import java.util.concurrent.Executors

@Database(
    entities = [
        Store::class,
        Aisle::class,
        Item::class,
        ItemAisle::class,
        ItemPrep::class,
        Recipe::class
    ],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDAO
    abstract fun aisleDao(): AisleDAO
    abstract fun itemDao(): ItemDAO
    abstract fun itemAislesDao(): ItemAisleDAO
    abstract fun itemPrepDao(): ItemPrepDAO
    abstract fun recipeDao(): RecipeDAO

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun getAssetText(context: Context, fileName: String): String {
            return try {
                context.assets.open("database/$fileName.json")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: Exception) {
                "[]"
            }
        }

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        private fun ioThread(f : () -> Unit) {
            Executors.newSingleThreadExecutor().execute(f)
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val strStores = getAssetText(context, "stores")
            val strAisles = getAssetText(context, "aisles")
            val strItems = getAssetText(context, "items")
            val strItemAisles = getAssetText(context, "itemAisles")
            val strItemPreps = getAssetText(context, "itemPreps")
            val strRecipe = getAssetText(context, "recipes")

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "Sample.db"
            )
                // prepopulate the database after onCreate was called
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // insert the data on the IO Thread
                        ioThread {
                            getInstance(context).apply {
                                storeDao().populate(strStores)
                                aisleDao().populate(strAisles)
                                itemDao().populate(strItems)
                                itemAislesDao().populate(strItemAisles)
                                itemPrepDao().populate(strItemPreps)
                                recipeDao().populate(strRecipe)
                            }
                        }
                    }
                })
                .build()
        }
    }
}




