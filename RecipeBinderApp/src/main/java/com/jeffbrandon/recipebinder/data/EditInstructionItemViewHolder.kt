package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R

class EditInstructionItemViewHolder(private val view: View, callback: (Instruction) -> Unit) :
    CallbackViewHolder<Instruction, Instruction>(view,
                                                 ViewCompat.requireViewById(view, R.id.edit_instruction_button),
                                                 callback) {
    override lateinit var current: Instruction

    override fun bind(item: Instruction) {
        current = item
        val text: TextView = ViewCompat.requireViewById(view, R.id.instruction)
        text.text = item.text
    }
}
