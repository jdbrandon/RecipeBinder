package com.jeffbrandon.recipebinder.widgets

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.DialogEditTagsBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.RecipeTag.Companion.recipeMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsFilterDialog @Inject constructor() {

    fun show(context: Context, currentFilters: List<RecipeTag>, callback: (List<RecipeTag>) -> Unit) {
        val view = View.inflate(context, R.layout.dialog_edit_tags, null)
        val binding = DialogEditTagsBinding.bind(view)
        binding.setupChecks(currentFilters)
        AlertDialog.Builder(context).setView(view).setTitle(context.getString(R.string.filter_recipes))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                binding.tags.recipeMap().filter { it.value.isChecked }.map { it.key }.also { callback(it) }
            }.show()
    }

    private fun DialogEditTagsBinding.setupChecks(currentFilters: List<RecipeTag>) {
        tags.recipeMap().let { map ->
            currentFilters.forEach {
                map[it]?.apply { isChecked = true } ?: error("Unmapped tag")
            }
        }
    }
}
