package com.jeffbrandon.recipebinder.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecipeData::class], version = 1)
@TypeConverters(IngredientListConverter::class, InstructionConverter::class, RecipeTagConverter::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase {
            INSTANCE?.also { return it }
            synchronized(RecipeDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipesBinderRecipes"
                )
                    .build()
            }
            return INSTANCE!!
        }
    }
}