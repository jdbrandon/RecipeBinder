package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.EditIngredientViewBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditIngredientFragment : Fragment(R.layout.fragment_add_ingredient) {

    @Inject lateinit var binder: EditIngredientViewBinder
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, view, parentFragmentManager, viewLifecycleOwner)
    }
}
