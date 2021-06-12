package com.jeffbrandon.recipebinder.room

import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface RecipeDataSource {

    @Query("SELECT * FROM RecipeData WHERE id = :id")
    fun fetchRecipe(id: Long): Flow<RecipeData>

    @Update
    fun updateRecipe(recipe: RecipeData): Int
}
