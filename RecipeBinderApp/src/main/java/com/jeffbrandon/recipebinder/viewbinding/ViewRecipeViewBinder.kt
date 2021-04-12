package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.databinding.FragmentViewRecipeBinding
import com.jeffbrandon.recipebinder.fragments.ViewFragmentPagerAdapter
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel

class ViewRecipeViewBinder(
    viewModel: RecipeViewModel,
    viewRoot: View,
    activity: FragmentActivity,
    lifecycleOwner: LifecycleOwner,
) {

    private val binder by lazy { FragmentViewRecipeBinding.bind(viewRoot) }

    init {
        viewModel.getRecipe().observe(lifecycleOwner) { recipe -> onNewRecipe(recipe) }
        binder.listFragmentContainer.adapter = ViewFragmentPagerAdapter(activity)
    }

    private fun onNewRecipe(recipe: RecipeData) {
        with(binder) {
            recipe.run {
                cookTimeView.text = cookTime.toString()
                nameText.text = name
            }
        }
    }
}
