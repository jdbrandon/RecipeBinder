package com.jeffbrandon.recipebinder.viewbinding

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.EditInstructionAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.databinding.FragmentEditRecipeItemsBinding
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import com.jeffbrandon.recipebinder.widgets.UpdateInstructionDialog
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditRecipeInstructionsBinder @Inject constructor(private val dialog: UpdateInstructionDialog) {

    fun bind(
        vm: EditRecipeViewModel,
        viewRoot: View,
        lifecycle: LifecycleOwner,
    ) {
        val binding = FragmentEditRecipeItemsBinding.bind(viewRoot)
        binding.items.contentDescription = viewRoot.resources.getString(R.string.instruction_list)
        vm.getInstructions().observe(lifecycle) { instructions ->
            with(binding) {
                items.adapter = EditInstructionAdapter(vm, instructions) {
                    vm.viewModelScope.launch { vm.setEditInstruction(it) }
                    dialog.show(viewRoot.context, vm, it)
                }
                addItemFab.setOnClickListener {
                    vm.viewModelScope.launch { vm.setEditInstruction(Instruction("")) }
                    dialog.show(viewRoot.context, vm)
                }
            }
        }
    }
}
