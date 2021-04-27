package com.jeffbrandon.recipebinder.data

import android.content.ClipData
import android.graphics.Point
import android.view.View
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.EditIngredientListItemBinding

class EditIngredientItemViewHolder(
    private val view: View,
    callback: (Ingredient) -> Unit,
) : CallbackViewHolder<Ingredient, Ingredient>(view,
                                               ViewCompat.requireViewById(view, R.id.edit_ingredient_button),
                                               callback) {
    override lateinit var current: Ingredient
    private val binder = EditIngredientListItemBinding.bind(view)
    private val dragShadowBuilder = object : View.DragShadowBuilder(view) {
        override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
            outShadowSize.set(view.width, view.height)
            outShadowTouchPoint.set(view.width - (view.resources.getDimension(R.dimen.drag_target_width).toInt() / 2),
                                    view.height / 2)
        }
    }

    override fun bind(item: Ingredient) = with(binder) {
        current = item
        ingredient.text =
            view.context.getString(R.string.ingredient_format, item.amountString(view.context, true), item.name)
        dragTarget.setOnLongClickListener {
            val clipData = ClipData.newPlainText(view.context.getString(R.string.ingredient), "")
            ViewCompat.startDragAndDrop(view, clipData, dragShadowBuilder, current, 0)
            true
        }
    }
}
