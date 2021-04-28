package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.EditIngredientViewBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditIngredientFragment : Fragment(R.layout.fragment_add_ingredient) {

    @Inject lateinit var binder: EditIngredientViewBinder
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, view, parentFragmentManager, viewLifecycleOwner)
    }

    /**
     * Hook into lifecycle callbacks to determine if we should warn about unsaved data
     */
    override fun onResume() {
        super.onResume()
        binder.edit()
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch { binder.save() }
    }
}
