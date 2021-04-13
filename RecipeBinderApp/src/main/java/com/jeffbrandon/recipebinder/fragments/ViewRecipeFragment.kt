package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.ViewRecipeViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ViewRecipeFragment : Fragment(R.layout.fragment_view_recipe) {
    @Inject lateinit var binder: ViewRecipeViewBinder
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("fragment view created")
        binder.bind(viewModel, requireView(), requireActivity(), viewLifecycleOwner)
    }
}
