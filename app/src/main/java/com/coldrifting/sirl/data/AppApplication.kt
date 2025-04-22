package com.coldrifting.sirl.data

import android.app.Application
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.coldrifting.sirl.Aisles
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.Stores
import com.coldrifting.sirl.repo.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication: Application() {
    private val scope = CoroutineScope(SupervisorJob())

    private val database2 by lazy { Database(
        driver = AndroidSqliteDriver(Database.Schema, applicationContext, "a_test.db"),
        AislesAdapter = Aisles.Adapter(
            storeIdAdapter = IntColumnAdapter,
            aisleIdAdapter = IntColumnAdapter,
            sortingPrefixAdapter = IntColumnAdapter),
        StoresAdapter = Stores.Adapter(
            storeIdAdapter = IntColumnAdapter)
    ) }

    private val db by lazy { AppDatabase.getInstance(applicationContext) }

    val appRepository by lazy {
        AppRepository(
            database2,
            scope,
            db.storeDao(),
            db.aisleDao(),
            db.itemDao(),
            db.itemAislesDao(),
            db.itemPrepDao(),
            db.recipeDao(),
            this.applicationContext) }
}