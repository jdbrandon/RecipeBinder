package com.jeffbrandon.recipebinder.room

import androidx.room.Query
import androidx.room.Update

interface RecipeDataSource {

    @Query("SELECT * FROM RecipeData WHERE id = :id")
    fun fetchRecipe(id: Long): RecipeData

    @Update
    fun updateRecipe(recipe: RecipeData): Int
}
