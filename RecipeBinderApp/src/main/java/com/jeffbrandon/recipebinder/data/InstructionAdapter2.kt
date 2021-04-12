package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R

class InstructionAdapter2(private val instructions: List<Instruction>) :
    RecyclerView.Adapter<InstructionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.instruction_list_item, parent, false)
        return InstructionViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstructionViewHolder, position: Int) =
        holder.bind(instructions[position])

    override fun getItemCount(): Int = instructions.size
}
