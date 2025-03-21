package com.coldrifting.sirl.entities.types

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ItemCategory {
    NonFood,
    Shelf,
    Bakery,
    Produce,
    Meat,
    Dairy,
    Deli,
    Vegetarian,
    Drinks,
    Frozen;

    companion object {
        fun ItemCategory.getIcon(): ImageVector {
        return when (this) {
            NonFood -> Icons.Default.Settings // Spray Bottle
            Shelf -> Icons.Default.Face // Boxes and Cans
            Bakery -> Icons.Default.Delete // Bread Loaf
            Produce -> Icons.Default.Edit // Tomato
            Meat -> Icons.Default.Menu // Meat
            Dairy -> Icons.Default.Refresh // Milk, cheese, and Eggs
            Deli -> Icons.Default.Refresh // Cheese + Something?
            Vegetarian -> Icons.Default.Refresh // Tofu
            Drinks -> Icons.Default.Refresh // Can + Bottle
            Frozen -> Icons.Default.Info // Snowflake
        }
    }
    }
}

