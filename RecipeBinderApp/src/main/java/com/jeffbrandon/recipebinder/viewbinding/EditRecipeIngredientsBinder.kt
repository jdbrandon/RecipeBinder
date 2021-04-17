package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.data.IngredientAdapter2
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeIngredientsBinding
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject

class EditRecipeIngredientsBinder @Inject constructor() {

    fun bind(vm: EditRecipeViewModel, viewRoot: View, lifecycle: LifecycleOwner) {
        val binding = FragmentEditRecipeIngredientsBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycle) {
            with(binding) {
                ingredientsRecyclerView.adapter = IngredientAdapter2(it.ingredients)
            }
        }
    }
}
