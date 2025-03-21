package com.coldrifting.sirl.data.access.base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import kotlinx.serialization.json.Json

@Dao
interface BaseDAO<T> {
    // insert single
    @Upsert
    fun insert(obj: T)

    // insert List
    @Upsert
    fun insert(obj: List<T>)

    @Delete
    fun delete(obj: T)

    companion object {
        inline fun<reified T> BaseDAO<T>.populate(json: String) {
            val entries = Json.decodeFromString<List<T>>(json)
            insert(entries)
        }
    }
}

