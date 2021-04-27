package com.jeffbrandon.recipebinder.room

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

interface RecipeMenuDataSource {

    @Insert
    fun insertRecipe(recipe: RecipeData): Long

    @Query("DELETE FROM RecipeData WHERE id = :recipeId")
    fun deleteRecipe(recipeId: Long?): Int

    @Query("SELECT * FROM RecipeData WHERE name like :filter")
    fun fetchAllRecipes(filter: String = "%"): LiveData<List<RecipeData>>
}
