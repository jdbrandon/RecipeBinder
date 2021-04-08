package com.jeffbrandon.recipebinder.dagger

import com.jeffbrandon.recipebinder.room.RecipeDao
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import com.jeffbrandon.recipebinder.room.RecipeMenuDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BindingModule {

    @Binds
    fun bindRecipeDataSource(dao: RecipeDao) : RecipeDataSource

    @Binds
    fun bindRecipeMenuDataSource(dao: RecipeDao) : RecipeMenuDataSource
}