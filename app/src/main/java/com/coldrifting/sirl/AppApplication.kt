package com.coldrifting.sirl

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppApplication: Application() {
    private val scope = CoroutineScope(SupervisorJob())

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    val appRepository by lazy { AppRepository(scope, db.appDao()) }
}