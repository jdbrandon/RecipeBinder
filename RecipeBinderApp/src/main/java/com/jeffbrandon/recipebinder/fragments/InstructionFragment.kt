package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.InstructionListFragmentViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InstructionFragment : Fragment(R.layout.fragment_list) {

    private val viewModel: RecipeViewModel by activityViewModels()
    @Inject lateinit var binder: InstructionListFragmentViewBinder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, view, viewLifecycleOwner)
    }
}
