package com.jeffbrandon.recipebinder.widgets

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.AppendableAdapter
import com.jeffbrandon.recipebinder.data.Instruction

class UpdateInstructionDialog(context: Context) {
    private var id = -1
    private val view = View.inflate(context, R.layout.dialog_update_instruction, null)
    private lateinit var adapter: AppendableAdapter<Instruction>
    private val textBox = view.findViewById<EditText>(R.id.update_instruction_text)

    private val dialog = AlertDialog.Builder(context)
        .setTitle(R.string.update_instruction)
        .setView(view)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.cancel()
            adapter.update(id, Instruction(textBox!!.text!!.toString()))
            textBox.text!!.clear()
        }
        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
            textBox.text!!.clear()
        }.create()

    fun updateInstruction(adapter: AppendableAdapter<Instruction>, id: Int) {
        this.adapter = adapter
        this.id = id
        textBox.setText(adapter.getItem(id).text)
        dialog.show()
    }
}