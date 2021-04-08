package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentViewRecipeBinding
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewRecipeFragment : Fragment(R.layout.fragment_view_recipe) {
    private val viewModel: RecipeViewModel by activityViewModels()
    private val binder by lazy { FragmentViewRecipeBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRecipe().observe(viewLifecycleOwner) { recipe ->
            with(binder){
                recipe.run {
                    cookTimeView.text = cookTime.toString()
                    nameText.text = name
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.list_fragment_container, IngredientFragment())
                        .commit()
                }
            }
        }
    }
}