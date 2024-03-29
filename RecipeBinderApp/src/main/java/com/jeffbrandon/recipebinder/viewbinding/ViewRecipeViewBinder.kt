package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentViewRecipeBinding
import com.jeffbrandon.recipebinder.fragments.EditRecipeFragment
import com.jeffbrandon.recipebinder.fragments.ViewFragmentPagerAdapter
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Inject

@Module
@InstallIn(FragmentComponent::class)
class ViewRecipeViewBinder @Inject constructor() {

    private lateinit var viewRoot: View
    private val binder by lazy { FragmentViewRecipeBinding.bind(viewRoot) }

    fun bind(
        viewModel: RecipeViewModel,
        view: View,
        activity: FragmentActivity,
        lifecycleOwner: LifecycleOwner,
    ) {
        viewRoot = view
        viewModel.getRecipe().observe(lifecycleOwner) { recipe -> onNewRecipe(recipe) }
        with(binder) {
            listFragmentContainer.adapter = ViewFragmentPagerAdapter(activity)
            editButton.setOnClickListener {
                activity.supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragment_container, EditRecipeFragment::class.java, null)
                    addToBackStack(null)
                }
            }
        }
    }

    private fun onNewRecipe(recipe: RecipeData) {
        with(binder) {
            recipe.run {
                nameText.text = name
                cookTimeView.text = viewRoot.resources.getQuantityString(R.plurals.minute, cookTime, cookTime)
                servingsText.text = servings.toString()
            }
        }
    }
}
