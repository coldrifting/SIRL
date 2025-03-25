package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDAO: BaseDAO<Recipe> {
    @Query("SELECT * FROM Recipes ORDER BY pinned DESC, LOWER(recipeName)")
    fun all(): Flow<List<Recipe>>

    @Query("UPDATE Recipes SET pinned = NOT pinned WHERE recipeId = :recipeId")
    fun togglePin(recipeId: Int)

    @Query("UPDATE RECIPES SET recipeName = :recipeName WHERE recipeId = :recipeId")
    fun setName(recipeId: Int, recipeName: String)
}