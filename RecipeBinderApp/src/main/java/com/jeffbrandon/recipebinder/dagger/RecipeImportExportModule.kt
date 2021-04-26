package com.jeffbrandon.recipebinder.dagger

import com.jeffbrandon.recipebinder.util.Base64Encoder
import com.jeffbrandon.recipebinder.util.Base64Util
import com.jeffbrandon.recipebinder.util.CompressionHelper
import com.jeffbrandon.recipebinder.util.CompressionUtil
import com.jeffbrandon.recipebinder.util.RecipeBlobImporter
import com.jeffbrandon.recipebinder.util.RecipeExporter
import com.jeffbrandon.recipebinder.util.RecipeImportExportUtil
import com.jeffbrandon.recipebinder.util.UriHelper
import com.jeffbrandon.recipebinder.util.UriUtil
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

    @Binds
    fun bindBase64Impl(b64: Base64Util): Base64Encoder

    @Binds
    fun bindBaseUriImpl(b64: UriUtil): UriHelper

    @Binds
    fun bindCompressor(compressor: CompressionUtil): CompressionHelper
}
