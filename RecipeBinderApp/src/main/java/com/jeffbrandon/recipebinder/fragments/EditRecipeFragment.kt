package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.EditRecipeViewBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditRecipeFragment : Fragment(R.layout.fragment_edit_recipe) {
    @Inject lateinit var binder: EditRecipeViewBinder
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, requireActivity(), requireView())
    }

    override fun onResume() {
        super.onResume()
        binder.onResume()
    }

    override fun onPause() {
        super.onPause()
        binder.onPause()
    }
}
