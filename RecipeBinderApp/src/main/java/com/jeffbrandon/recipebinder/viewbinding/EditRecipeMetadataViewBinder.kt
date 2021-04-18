package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeMetadataBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag.Companion.recipeMap
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject

class EditRecipeMetadataViewBinder @Inject constructor() {
    private lateinit var binder: FragmentEditRecipeMetadataBinding

    fun bind(viewModel: EditRecipeViewModel, viewRoot: View, lifecycle: LifecycleOwner) {
        binder = FragmentEditRecipeMetadataBinding.bind(viewRoot)
        viewModel.getRecipe().observe(lifecycle) { recipe ->
            with(binder.meta) {
                name.setText(recipe.name)
                cookTime.setText(recipe.cookTime.toString())
            }
            with(binder.tags) {
                val tagMap = recipeMap()
                recipe.tags.forEach { tag ->
                    tagMap[tag]?.apply { isChecked = true } ?: error("Unmapped tag")
                }
            }
        }
    }
}
