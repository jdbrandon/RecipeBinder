package com.jeffbrandon.recipebinder.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecipeData::class], version = 2)
@TypeConverters(IngredientListConverter::class, InstructionConverter::class, RecipeTagConverter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
