package com.jeffbrandon.recipebinder.data

import android.view.View
import com.jeffbrandon.recipebinder.databinding.IngredientListItemBinding

class IngredientItemViewHolder(private val view: View) : BindableViewHolder<Ingredient>(view) {

    private val binder: IngredientListItemBinding by lazy { IngredientListItemBinding.bind(view) }

    override fun bind(item: Ingredient) {
        with(binder) {
            val amountText = item.amountString(view.context)
            amount.text = amountText
            name.text = item.name
            ingredientView.contentDescription = "$amount ${item.name}"
        }
    }
}
