package com.jeffbrandon.recipebinder.data

import android.view.View
import androidx.core.view.ViewCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.EditIngredientListItemBinding

class EditIngredientItemViewHolder(
    private val view: View,
    callback: (Ingredient) -> Unit,
) : CallbackViewHolder<Ingredient, Ingredient>(view,
                                               ViewCompat.requireViewById(view,
                                                                          R.id.edit_ingredient_button),
                                               callback) {
    override lateinit var current: Ingredient
    val binder = EditIngredientListItemBinding.bind(view)

    override fun bind(item: Ingredient) = with(binder) {
        current = item
        ingredient.text = view.context.getString(R.string.ingredient_format,
                                                 item.amountString(view.context, true),
                                                 item.name)
    }
}
