package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.data.EditIngredientAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeIngredientsBinding
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import timber.log.Timber
import javax.inject.Inject

class EditRecipeIngredientsBinder @Inject constructor() {

    fun bind(vm: EditRecipeViewModel, viewRoot: View, lifecycle: LifecycleOwner) {
        val binding = FragmentEditRecipeIngredientsBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycle) {
            with(binding) {
                ingredientsRecyclerView.adapter = EditIngredientAdapter(it.ingredients) {
                    Timber.i("editing ${it.name}")
                }
            }
        }
    }
}
