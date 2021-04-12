package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R

class InstructionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(data: Instruction) {
        ViewCompat.requireViewById<TextView>(view, R.id.instruction_text).text = data.text
    }
}
