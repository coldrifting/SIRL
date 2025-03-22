package com.coldrifting.sirl.data.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldrifting.sirl.data.entities.helper.PackageInfo
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.ui.theme.LocalCustomColorsPalette
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    val itemName: String,
    val itemTemp: ItemTemp = ItemTemp.Ambient,
    val packageInfo: PackageInfo? = null
) {
    @Composable
    fun getTempColor(): Color {
        return when (itemTemp) {
            ItemTemp.Ambient -> LocalCustomColorsPalette.current.ambientColor
            ItemTemp.Chilled -> LocalCustomColorsPalette.current.chilledColor
            ItemTemp.Frozen  -> LocalCustomColorsPalette.current.frozenColor
        }
    }
}