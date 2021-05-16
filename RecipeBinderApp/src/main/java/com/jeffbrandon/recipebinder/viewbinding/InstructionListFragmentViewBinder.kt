package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentListBinding
import com.jeffbrandon.recipebinder.viewmodel.RecipeViewModel
import javax.inject.Inject

class InstructionListFragmentViewBinder @Inject constructor() {

    fun bind(
        viewModel: RecipeViewModel,
        view: View,
        lifecycle: LifecycleOwner,
    ) {
        with(FragmentListBinding.bind(view)) {
            name.text = view.resources.getString(R.string.instructions)
            recycler.contentDescription = view.resources.getString(R.string.instruction_list)
            viewModel.getInstructions().observe(lifecycle) { instructions ->
                recycler.adapter = InstructionAdapter(instructions)
            }
        }
    }
}
