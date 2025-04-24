package com.coldrifting.sirl.data.entities.interfaces

import com.coldrifting.sirl.Database

interface Insertable {
    fun insert(database: Database)
}