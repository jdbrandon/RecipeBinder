package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentListBinding
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import javax.inject.Inject

class IngredientListFragmentViewBinder @Inject constructor() {

    fun bind(
        viewModel: RecipeViewModel,
        view: View,
        lifecycle: LifecycleOwner,
    ) {
        with(FragmentListBinding.bind(view)) {
            name.text = view.context.getString(R.string.ingredients)
            viewModel.getIngredients().observe(lifecycle) { ingredients ->
                recycler.adapter = IngredientAdapter(ingredients)
            }
        }
    }
}
