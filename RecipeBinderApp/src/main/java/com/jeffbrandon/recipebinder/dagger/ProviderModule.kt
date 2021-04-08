package com.jeffbrandon.recipebinder.dagger

import android.content.Context
import androidx.room.Room
import com.jeffbrandon.recipebinder.room.RecipeDao
import com.jeffbrandon.recipebinder.room.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProviderModule {

    @Provides
    @Singleton
    fun providesDao(@ApplicationContext context: Context): RecipeDao =
        Room.databaseBuilder(context.applicationContext,
                             RecipeDatabase::class.java,
                             "recipesBinderRecipes").build().recipeDao()

    @Provides
    fun providesJob(): Job = SupervisorJob()

    @Provides
    fun providesCoroutineContext(job: Job) = job + Dispatchers.Main
}
