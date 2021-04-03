package com.jeffbrandon.recipebinder.dagger

import com.jeffbrandon.recipebinder.activities.NewRecipeMenuActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {
    @ContributesAndroidInjector
    abstract fun contriubuteRecipeMenuActivity(): NewRecipeMenuActivity
}