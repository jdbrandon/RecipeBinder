package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.jeffbrandon.recipebinder.data.EditInstructionAdapter
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeItemsBinding
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import com.jeffbrandon.recipebinder.widgets.UpdateInstructionDialog
import javax.inject.Inject

class EditRecipeInstructionsBinder @Inject constructor(private val dialog: UpdateInstructionDialog) {

    fun bind(
        vm: EditRecipeViewModel,
        viewRoot: View,
        lifecycle: LifecycleOwner,
    ) {
        val binding = FragmentEditRecipeItemsBinding.bind(viewRoot)
        vm.getRecipe().observe(lifecycle) {
            with(binding) {
                items.adapter = EditInstructionAdapter(it.instructions) {
                    vm.setEditInstruction(it)
                    dialog.show(viewRoot.context, vm, it)
                }
                addItemFab.setOnClickListener {
                    dialog.show(viewRoot.context, vm)
                }
            }
        }
    }
}
