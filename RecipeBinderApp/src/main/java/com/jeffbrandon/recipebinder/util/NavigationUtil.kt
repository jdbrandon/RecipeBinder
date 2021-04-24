package com.jeffbrandon.recipebinder.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.activities.RecipeActivity
import com.jeffbrandon.recipebinder.enums.RecipeMode

class NavigationUtil private constructor() {
    companion object {
        fun viewRecipe(context: Context, id: Long) =
            ContextCompat.startActivity(context, buildIntent(context, id, RecipeMode.VIEW), null)

        fun editRecipe(context: Context, id: Long) =
            ContextCompat.startActivity(context, buildIntent(context, id, RecipeMode.EDIT), null)

        private fun buildIntent(context: Context, id: Long, mode: RecipeMode): Intent =
            Intent(context, RecipeActivity::class.java).apply {
                putExtra(context.getString(R.string.extra_recipe_id), id)
                putExtra(context.getString(R.string.extra_view_mode), mode)
            }
    }
}
