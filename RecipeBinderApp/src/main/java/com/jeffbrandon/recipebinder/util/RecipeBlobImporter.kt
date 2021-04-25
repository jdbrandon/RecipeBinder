package com.jeffbrandon.recipebinder.util

import com.jeffbrandon.recipebinder.room.RecipeData

interface RecipeBlobImporter {
    suspend fun import(blob: String): RecipeData?
}
