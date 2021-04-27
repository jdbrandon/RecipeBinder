package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.EditIngredientAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeItemsBinding
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
        val binding = FragmentEditRecipeItemsBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycle) {
            with(binding) {
                items.adapter = EditIngredientAdapter(vm, it.ingredients) {
                    vm.setEditIngredient(it)
                    openEditIngredientFragment(fm)
                }
                addItemFab.setOnClickListener {
                    openEditIngredientFragment(fm)
                }
            }
        }
    }

    private fun openEditIngredientFragment(fm: FragmentManager) {
        fm.beginTransaction().add(R.id.fragment_container, EditIngredientFragment::class.java, null)
            .addToBackStack(null).commit()
    }
}
