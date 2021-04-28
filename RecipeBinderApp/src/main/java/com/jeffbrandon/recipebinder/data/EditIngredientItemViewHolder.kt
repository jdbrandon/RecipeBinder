package com.jeffbrandon.recipebinder.data

import android.animation.AnimatorInflater
import android.content.ClipData
import android.graphics.Point
import android.view.DragEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.EditIngredientListItemBinding
import com.jeffbrandon.recipebinder.util.DragShadowHelper
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class EditIngredientItemViewHolder(
    private val viewModel: EditRecipeViewModel,
    private val view: View,
    callback: (Ingredient) -> Unit,
) : CallbackViewHolder<Ingredient, Ingredient>(view,
                                               ViewCompat.requireViewById(view, R.id.edit_ingredient_button),
                                               callback) {
    override lateinit var current: Ingredient
    private val binder = EditIngredientListItemBinding.bind(view)
    private val dragShadowBuilder = DragShadowHelper(view)

    private val dragListener = View.OnDragListener { _, event ->
        Timber.i("Drag event ${event.action}")
        val dragIngredient = event.localState as Ingredient
        when (event.action) {
            DragEvent.ACTION_DROP -> with(viewModel) {
                viewModelScope.launch {
                    setEditIngredient(dragIngredient)
                    moveEditIngredientBefore(current)
                }
            }
            DragEvent.ACTION_DRAG_STARTED -> if (dragIngredient == current) view.visibility = View.GONE
            DragEvent.ACTION_DRAG_ENDED -> if (dragIngredient == current) view.visibility = View.VISIBLE
        }
        true
    }

    override fun bind(item: Ingredient) = with(binder) {
        current = item
        ingredient.text =
            view.context.getString(R.string.ingredient_format, item.amountString(view.context, true), item.name)
        dragTarget.setOnLongClickListener {
            val clipData = ClipData.newPlainText(view.context.getString(R.string.ingredient), "")
            ViewCompat.startDragAndDrop(view, clipData, dragShadowBuilder, current, 0)

            // TODO: figure out what happens when list fills screen, maybe add margin to the recycler to account for that?

            true
        }
        view.setOnDragListener(dragListener)
        view.stateListAnimator = AnimatorInflater.loadStateListAnimator(view.context, R.animator.drag_over_animation)
    }
}
