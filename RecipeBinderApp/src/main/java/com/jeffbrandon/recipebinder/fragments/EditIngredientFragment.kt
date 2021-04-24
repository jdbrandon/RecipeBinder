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

    override fun onResume() {
        super.onResume()
        binder.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        // When we pop this off the back stack it doesn't trigger onResume so this is a workaround
        // to make sure we still warn about back presses in EditRecipeFragment, which should be the
        // only entry point to this fragment
        binder.continueEditing()
    }
}
