package com.jeffbrandon.recipebinder.data

import android.animation.AnimatorInflater
import android.content.ClipData
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.util.DragShadowHelper
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class EditInstructionItemViewHolder(
    private val viewModel: EditRecipeViewModel,
    private val view: View,
    callback: (Instruction) -> Unit,
) : CallbackViewHolder<Instruction, Instruction>(view,
                                                 ViewCompat.requireViewById(view, R.id.edit_instruction_button),
                                                 callback) {
    override lateinit var current: Instruction
    private val dragShadowBuilder = DragShadowHelper(view)
    private val dragListener = View.OnDragListener { _, event ->
        Timber.i("Drag event ${event.action}")
        val dragInstruction = event.localState as Instruction
        when (event.action) {
            DragEvent.ACTION_DROP -> with(viewModel) {
                viewModelScope.launch {
                    setEditInstruction(dragInstruction)
                    moveEditInstructionBefore(current)
                }
            }
            DragEvent.ACTION_DRAG_STARTED -> if (dragInstruction == current) view.visibility = View.GONE
            DragEvent.ACTION_DRAG_ENDED -> if (dragInstruction == current) view.visibility = View.VISIBLE
        }
        true
    }

    override fun bind(item: Instruction) {
        val text: TextView = ViewCompat.requireViewById(view, R.id.instruction)
        val dragTarget: Button = ViewCompat.requireViewById(view, R.id.drag_target)
        current = item
        text.text = item.text
        dragTarget.setOnLongClickListener {
            val clipData = ClipData.newPlainText(view.context.getString(R.string.instructions), "")
            ViewCompat.startDragAndDrop(view, clipData, dragShadowBuilder, current, 0)
            true
        }
        view.setOnDragListener(dragListener)
        view.stateListAnimator = AnimatorInflater.loadStateListAnimator(view.context, R.animator.drag_over_animation)
    }
}
