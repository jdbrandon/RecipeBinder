package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jeffbrandon.recipebinder.R

class InstructionAdapter(context: Context,
                         instructions: MutableList<Instruction>,
                         private val layout: Int = R.layout.instruction_list_item) :
    AppendableAdapter<Instruction>(context, instructions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(layout, parent, false)
        view.id = R.id.instruction_view
        val textView = view.findViewById<TextView>(R.id.instruction_text)
        dataSource[position].let { instruction ->
            textView?.apply { text = instruction.text }
        }
        return view
    }
}
