package com.jeffbrandon.recipebinder.viewbinding

import android.net.Uri
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.Chip
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeMetadataBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.RecipeTag.Companion.recipeMap
import com.jeffbrandon.recipebinder.fragments.Savable
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class EditRecipeMetadataViewBinder @Inject constructor() : Savable {
    interface ViewContract {
        fun startCameraActivityForResult(uri: Uri)
    }

    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentEditRecipeMetadataBinding
    private lateinit var tagMap: Map<RecipeTag, Chip>

    // TODO
    private val uri = Uri.EMPTY

    fun bind(vm: EditRecipeViewModel, vc: ViewContract, viewRoot: View, lifecycle: LifecycleOwner) {
        viewModel = vm
        binder = FragmentEditRecipeMetadataBinding.bind(viewRoot)
        viewModel.getRecipe().observe(lifecycle) { recipe ->
            with(binder.meta) {
                name.setText(recipe.name)
                cookTime.setText(recipe.cookTime.toString())
                servings.setText(recipe.servings.toString())
            }
            with(binder.tags) {
                tagMap = recipeMap()
                recipe.tags.forEach { tag ->
                    tagMap[tag]?.apply { isChecked = true } ?: error("Unmapped tag")
                }
            }
        }
        binder.addImageButton.setOnClickListener {
            vc.startCameraActivityForResult(uri)
        }
    }

    override fun edit() {
        viewModel.beginEditing()
    }

    override suspend fun save() = withContext(Dispatchers.Default) {
        with(binder.meta) {
            val recipeName = name.text.toString()
            val cookTime = cookTime.text.toString().toInt()
            val servings = servings.text.toString().toInt()
            val tags = tagMap.entries.filter { it.value.isChecked }.map { it.key }.toSet()
            viewModel.saveMetadata(recipeName, cookTime, servings, tags)
        }
        viewModel.stopEditing()
    }

    fun saveBitmap(success: Boolean): Unit = with(viewModel) {
        if (success) {
            viewModelScope.launch {
                viewModel.saveImage(uri)
            }
        } else {
            Timber.w("Unable to save image, capture failed")
        }
    }
}
