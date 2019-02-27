package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.jeffbrandon.recipebinder.R

class InstructionAdapter(context: Context, private val instructions: MutableList<Instruction>) :
    AppendableAdapter<Instruction>(context, instructions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: inflater.inflate(R.layout.instruction_list_item, parent, false)
        val textView = view.findViewById(R.id.instruction_text) as AppCompatTextView?
        instructions[position].let { instruction ->
            textView?.apply { text = instruction.get() }
        }
        return view
    }
}
