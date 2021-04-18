package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.EditIngredientAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeIngredientsBinding
import com.jeffbrandon.recipebinder.fragments.EditIngredientFragment
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject

class EditRecipeIngredientsBinder @Inject constructor() {

    fun bind(
        vm: EditRecipeViewModel,
        viewRoot: View,
        fm: FragmentManager,
        lifecycle: LifecycleOwner,
    ) {
        val binding = FragmentEditRecipeIngredientsBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycle) {
            with(binding) {
                ingredientsRecyclerView.adapter = EditIngredientAdapter(it.ingredients) {
                    vm.setEditIngredient(it)
                    openEditIngredientFragment(fm)
                }
                addIngredientFab.setOnClickListener {
                    openEditIngredientFragment(fm)
                }
            }
        }
    }

    private fun openEditIngredientFragment(fm: FragmentManager) {
        fm.beginTransaction()
            .replace(R.id.fragment_container, EditIngredientFragment::class.java, null)
            .addToBackStack(null).commit()
    }
}
