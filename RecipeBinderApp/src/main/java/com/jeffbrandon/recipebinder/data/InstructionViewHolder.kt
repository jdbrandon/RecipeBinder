package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R

class InstructionViewHolder(private val view: View) : BindableViewHolder<Instruction>(view) {

    override fun bind(item: Instruction) {
        ViewCompat.requireViewById<TextView>(view, R.id.instruction_text).text = item.text
    }
}
