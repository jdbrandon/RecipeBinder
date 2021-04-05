package com.jeffbrandon.recipebinder.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.activities.ViewRecipeActivity

class NavigationUtil {
    companion object {

        fun viewRecipe(context: Context, id: Long) =
            ContextCompat.startActivity(context, viewIntent(context, id), null)

        fun editRecipe(context: Context, id: Long) =
            ContextCompat.startActivity(context, editIntent(context, id), null)

        private fun viewIntent(context: Context, id: Long): Intent =
            Intent(context, ViewRecipeActivity::class.java).apply {
                putExtra(context.getString(R.string.database_recipe_id), id)
            }

        private fun editIntent(context: Context, id: Long): Intent = viewIntent(context, id).apply {
            putExtra(
                context.getString(R.string.view_mode_extra),
                ViewRecipeActivity.EDIT_TAGS
            )
        }
    }
}
