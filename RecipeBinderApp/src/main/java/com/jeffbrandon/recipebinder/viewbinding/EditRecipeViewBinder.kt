package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeBinding
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import javax.inject.Inject

class EditRecipeViewBinder @Inject constructor() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var viewRoot: View
    private lateinit var lifecycle: LifecycleOwner
    private val binder by lazy { FragmentEditRecipeBinding.bind(viewRoot) }

    fun bind(vm: RecipeViewModel, v: View, lifecycle: LifecycleOwner) {
        viewModel = vm
        viewRoot = v
        this.lifecycle = lifecycle
        vm.getRecipe().observe(lifecycle) { recipe ->
            with(binder) {
                name.setText(recipe.name)
                cookTime.setText(recipe.cookTime.toString())
            }
        }
    }
}
