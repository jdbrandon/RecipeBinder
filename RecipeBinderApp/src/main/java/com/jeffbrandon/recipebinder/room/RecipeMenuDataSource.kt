package com.jeffbrandon.recipebinder.room

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface RecipeMenuDataSource {

    @Insert
    fun insertRecipe(recipe: RecipeData): Long

    @Delete(entity = RecipeData::class)
    fun deleteRecipe(id: Long?): Int

    @Query("SELECT * FROM RecipeData WHERE name like :filter")
    fun fetchAllRecipes(filter: String = "%"): LiveData<List<RecipeData>>
}
