package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R

class EditIngredientAdapter(
    ingredients: List<Ingredient>,
    private val callback: (Ingredient) -> Unit,
) : ListRecyclerViewAdapter<EditIngredientItemViewHolder, Ingredient>(ingredients) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EditIngredientItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_ingredient_list_item, parent, false)
        return EditIngredientItemViewHolder(view, callback)
    }
}
