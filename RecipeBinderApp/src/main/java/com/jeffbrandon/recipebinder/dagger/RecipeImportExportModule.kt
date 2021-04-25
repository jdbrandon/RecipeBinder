package com.jeffbrandon.recipebinder.dagger

import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import com.jeffbrandon.recipebinder.util.RecipeExporter
import com.jeffbrandon.recipebinder.util.RecipeImportExportUtil
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RecipeImportExportModule {

    @Binds
    fun bindExportUtil(util: RecipeImportExportUtil): RecipeExporter

    @Binds
    fun bindImportUtil(util: RecipeImportExportUtil): RecipeBlobImporter
}
