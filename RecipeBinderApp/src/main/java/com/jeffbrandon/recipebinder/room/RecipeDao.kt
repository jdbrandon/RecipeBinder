package com.jeffbrandon.recipebinder.room

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface RecipeDao : RecipeDataSource, RecipeMenuDataSource {
    @Insert
    fun insertRecipes(recipes: List<RecipeData>): List<Long>
}
