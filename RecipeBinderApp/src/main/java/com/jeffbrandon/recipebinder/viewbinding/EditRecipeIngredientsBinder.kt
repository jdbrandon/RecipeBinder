package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.EditIngredientAdapter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeItemsBinding
import com.jeffbrandon.recipebinder.enums.UnitType
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
        binding.items.contentDescription = viewRoot.resources.getString(R.string.ingredient_list)
        vm.getIngredients().observe(lifecycle) { ingredients ->
            with(binding) {
                items.adapter = EditIngredientAdapter(vm, ingredients) {
                    vm.setEditIngredient(it)
                    openEditIngredientFragment(fm)
                }
                addItemFab.setOnClickListener {
                    vm.setEditIngredient(Ingredient("", 0f, UnitType.NONE))
                    openEditIngredientFragment(fm)
                }
            }
        }
    }

    private fun openEditIngredientFragment(fm: FragmentManager) {
        fm.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container, EditIngredientFragment::class.java, null)
            addToBackStack(null)
        }
    }
}
