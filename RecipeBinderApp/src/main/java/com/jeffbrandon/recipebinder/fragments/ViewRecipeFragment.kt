package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.ViewRecipeViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import timber.log.Timber

class ViewRecipeFragment : Fragment(R.layout.fragment_view_recipe) {
    private lateinit var binding: ViewRecipeViewBinder
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("fragment view created")
        binding =
            ViewRecipeViewBinder(viewModel, requireView(), requireActivity(), viewLifecycleOwner)
    }
}
