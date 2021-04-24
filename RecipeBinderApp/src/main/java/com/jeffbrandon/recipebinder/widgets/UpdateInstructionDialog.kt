package com.jeffbrandon.recipebinder.widgets

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateInstructionDialog @Inject constructor() {
    private lateinit var context: Context
    private lateinit var viewModel: EditRecipeViewModel
    private fun inflateView() =
        View.inflate(context, R.layout.dialog_update_instruction, null).also {
            textBox = ViewCompat.requireViewById(it, R.id.update_instruction_text)
        }

    private lateinit var textBox: EditText

    private fun buildAndShow(instruction: Instruction?) {
        val view = inflateView()
        textBox.setText(instruction?.text ?: "")
        val builder = AlertDialog.Builder(context)
            .setTitle(if (instruction != null) R.string.update_instruction else R.string.add_instruction)
            .setView(view).setPositiveButton(R.string.save) { _, _ ->
                viewModel.saveInstruction(Instruction(textBox.text.toString()))
                textBox.text?.clear()
            }.setNeutralButton(android.R.string.cancel) { _, _ ->
                textBox.text?.clear()
            }
        instruction?.let {
            builder.setNegativeButton(R.string.delete) { _, _ ->
                viewModel.deleteEditInstruction()
            }
        }
        builder.show()
    }

    fun show(c: Context, vm: EditRecipeViewModel, instruction: Instruction? = null) {
        this.context = c
        viewModel = vm
        buildAndShow(instruction)
    }
}
