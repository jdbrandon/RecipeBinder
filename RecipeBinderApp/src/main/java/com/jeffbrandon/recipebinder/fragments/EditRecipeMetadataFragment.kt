package com.jeffbrandon.recipebinder.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.EditRecipeMetadataViewBinder
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditRecipeMetadataFragment : EditFragment(R.layout.fragment_edit_recipe_metadata) {
    @Inject lateinit var binder: EditRecipeMetadataViewBinder
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder.bind(viewModel, requireView(), viewLifecycleOwner)
    }

    override suspend fun save() {
        binder.save()
    }
}
