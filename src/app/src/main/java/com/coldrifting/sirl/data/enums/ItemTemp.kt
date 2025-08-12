package com.coldrifting.sirl.data.enums

import app.cash.sqldelight.ColumnAdapter

enum class ItemTemp {
    Ambient,
    Chilled,
    Frozen;

    object Adapter : ColumnAdapter<ItemTemp, String> {
        override fun decode(databaseValue: String): ItemTemp = enumValueOf<ItemTemp>(databaseValue)
        override fun encode(value: ItemTemp): String = value.toString()
    }
}