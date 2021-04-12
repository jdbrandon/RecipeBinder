package com.jeffbrandon.recipebinder.data

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.databinding.IngredientListItemBinding

class IngredientItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val binder: IngredientListItemBinding by lazy { IngredientListItemBinding.bind(view) }

    fun bind(data: Ingredient) {
        with(binder) {
            val amount = data.amountString(view.context)
            quantity.text = amount
            ingredientName.text = data.name
            ingredientView.contentDescription = "$amount ${data.name}"
        }
    }
}
