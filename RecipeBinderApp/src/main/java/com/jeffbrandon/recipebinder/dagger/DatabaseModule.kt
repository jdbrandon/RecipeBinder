package com.jeffbrandon.recipebinder.dagger

import android.content.Context
import androidx.room.Room
import com.jeffbrandon.recipebinder.room.Migration1
import com.jeffbrandon.recipebinder.room.RecipeDao
import com.jeffbrandon.recipebinder.room.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDao(@ApplicationContext context: Context): RecipeDao =
        Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "recipesBinderRecipes")
            .addMigrations(Migration1)
            .build()
            .recipeDao()
}
