package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeMetadataBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.RecipeTag.Companion.recipeMap
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import timber.log.Timber
import javax.inject.Inject

class EditRecipeMetadataViewBinder @Inject constructor() {
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentEditRecipeMetadataBinding

    fun bind(vm: EditRecipeViewModel, viewRoot: View, lifecycle: LifecycleOwner) {
        viewModel = vm
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

    fun save() = with(binder.meta) {
        val recipeName = name.text.toString()
        val cookTime = cookTime.text.toString().toInt()
        val tags = mutableListOf<RecipeTag>()
        binder.tags.recipeMap().entries.forEach { entry ->
            if (entry.value.isChecked) tags.add(entry.key)
        }
        Timber.i("saving $recipeName, $cookTime, $tags")
        viewModel.saveMetadata(recipeName, cookTime, tags)
    }
}
