package com.jeffbrandon.recipebinder.widgets

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.AppendableAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateInstructionDialog @Inject constructor() {
    private var id = -1
    private lateinit var context: Context
    private lateinit var adapter: AppendableAdapter<Instruction>
    private val view by lazy { View.inflate(context, R.layout.dialog_update_instruction, null) }
    private val textBox: EditText by lazy {
        ViewCompat.requireViewById(view, R.id.update_instruction_text)
    }

    private val dialog by lazy {
        AlertDialog.Builder(context).setTitle(R.string.update_instruction).setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.cancel()
                adapter.update(id, Instruction(textBox.text.toString()))
                textBox.text!!.clear()
            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
                textBox.text!!.clear()
            }.create()
    }

    fun show(context: Context, adapter: AppendableAdapter<Instruction>, id: Int) {
        this.context = context
        this.adapter = adapter
        this.id = id
        textBox.setText(adapter.getItem(id).text)
        dialog.show()
    }
}
