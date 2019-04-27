package com.jeffbrandon.recipebinder.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {

    @Insert
    fun insertRecipe(recipe: RecipeData): Long

    @Insert
    fun insertRecipes(recipes: List<RecipeData>): List<Long>

    @Query("SELECT * FROM RecipeData WHERE id = :id")
    fun fetchRecipe(id: Long): RecipeData

    @Query("SELECT * FROM RecipeData")
    fun fetchAllRecipes(): LiveData<List<RecipeData>>

    @Update
    fun updateRecipe(recipe: RecipeData)

    @Delete
    fun deleteRecipe(recipe: RecipeData)
}