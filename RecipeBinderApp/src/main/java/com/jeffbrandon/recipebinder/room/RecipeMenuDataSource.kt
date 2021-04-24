package com.jeffbrandon.recipebinder.room

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface RecipeMenuDataSource {

    @Insert
    fun insertRecipe(recipe: RecipeData): Long

    @Delete
    fun deleteRecipe(recipe: RecipeData): Int

    @Query("SELECT * FROM RecipeData WHERE name like :filter")
    fun fetchAllRecipes(filter: String = "%"): List<RecipeData>
}
