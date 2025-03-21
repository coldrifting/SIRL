package com.coldrifting.sirl

import android.app.Application
import com.coldrifting.sirl.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication: Application() {
    private val scope = CoroutineScope(SupervisorJob())

    private val db by lazy { AppDatabase.getInstance(applicationContext) }

    val appRepository by lazy {
        AppRepository(scope,
            db.storeDao(),
            db.aisleDao(),
            db.itemDao(),
            db.itemAislesDao(),
            this.applicationContext) }
}