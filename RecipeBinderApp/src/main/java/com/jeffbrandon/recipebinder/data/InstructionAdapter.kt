package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R

class InstructionAdapter(instructions: List<Instruction>) :
    ListRecyclerViewAdapter<InstructionViewHolder, Instruction>(instructions) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.instruction_list_item, parent, false)
        return InstructionViewHolder(view)
    }
}
