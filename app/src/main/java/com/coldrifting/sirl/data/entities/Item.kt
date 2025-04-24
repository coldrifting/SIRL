package com.coldrifting.sirl.data.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.ui.theme.LocalCustomColorsPalette
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val itemId: Int = -1,
    val itemName: String = "",
    val itemTemp: ItemTemp = ItemTemp.Ambient,
    val defaultUnits: UnitType = UnitType.Count
) : Insertable {
    override fun insert(database: Database) {
        database.itemsQueries.insertItem(itemId, itemName, itemTemp, defaultUnits)
    }

    @Composable
    fun getTempColor(): Color {
        return when(itemTemp) {
            ItemTemp.Chilled -> LocalCustomColorsPalette.current.chilledColor
            ItemTemp.Frozen -> LocalCustomColorsPalette.current.frozenColor
            else -> LocalCustomColorsPalette.current.ambientColor
        }
    }
}
