package com.jeffbrandon.recipebinder.util

import com.jeffbrandon.recipebinder.room.RecipeData

interface RecipeExporter {
    suspend fun encode(recipe: RecipeData): String
}
