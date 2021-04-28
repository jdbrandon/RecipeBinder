package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.chip.Chip
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeMetadataBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.RecipeTag.Companion.recipeMap
import com.jeffbrandon.recipebinder.fragments.Savable
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditRecipeMetadataViewBinder @Inject constructor() : Savable {
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentEditRecipeMetadataBinding
    private lateinit var tagMap: Map<RecipeTag, Chip>

    fun bind(vm: EditRecipeViewModel, viewRoot: View, lifecycle: LifecycleOwner) {
        viewModel = vm
        binder = FragmentEditRecipeMetadataBinding.bind(viewRoot)
        viewModel.getRecipe().observe(lifecycle) { recipe ->
            with(binder.meta) {
                name.setText(recipe.name)
                cookTime.setText(recipe.cookTime.toString())
            }
            with(binder.tags) {
                tagMap = recipeMap()
                recipe.tags.forEach { tag ->
                    tagMap[tag]?.apply { isChecked = true } ?: error("Unmapped tag")
                }
            }
        }
    }

    override fun edit() {
        viewModel.beginEditing()
    }

    override suspend fun save() = withContext(Dispatchers.Default) {
        with(binder.meta) {
            val recipeName = name.text.toString()
            val cookTime = cookTime.text.toString().toInt()
            val tags = tagMap.entries.filter { it.value.isChecked }.map { it.key }
            viewModel.saveMetadata(recipeName, cookTime, tags)
        }
        viewModel.stopEditing()
    }
}
