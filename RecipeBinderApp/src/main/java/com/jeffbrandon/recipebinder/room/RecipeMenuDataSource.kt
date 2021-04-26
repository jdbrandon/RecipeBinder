package com.jeffbrandon.recipebinder.room

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface RecipeMenuDataSource {

    @Insert
    fun insertRecipe(recipe: RecipeData): Long

    // TODO delete or revert
    //    @Query("DELETE FROM RecipeData WHERE id = :recipeId")
    @Delete(entity = RecipeData::class)
    fun deleteRecipe(recipeId: Long?): Int

    @Query("SELECT * FROM RecipeData WHERE name like :filter")
    fun fetchAllRecipes(filter: String = "%"): LiveData<List<RecipeData>>
}
