package com.coldrifting.sirl.data.enums

import app.cash.sqldelight.ColumnAdapter

enum class BayType {
    Start,
    Middle,
    End;

    object Adapter : ColumnAdapter<BayType, String> {
        override fun decode(databaseValue: String): BayType = enumValueOf<BayType>(databaseValue)
        override fun encode(value: BayType): String = value.toString()
    }
}