package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel

class EditInstructionAdapter(
    instructions: List<Instruction>,
    private val viewModel: EditRecipeViewModel,
    private val callback: (Instruction) -> Unit,
) : ListRecyclerViewAdapter<EditInstructionItemViewHolder, Instruction>(instructions) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EditInstructionItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_instruction_list_item, parent, false)
        return EditInstructionItemViewHolder(view, viewModel, callback)
    }
}
