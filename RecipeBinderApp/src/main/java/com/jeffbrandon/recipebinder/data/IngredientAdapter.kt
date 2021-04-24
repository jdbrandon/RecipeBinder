package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R

class IngredientAdapter(ingredients: List<Ingredient>) :
    ListRecyclerViewAdapter<IngredientItemViewHolder, Ingredient>(ingredients) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_list_item, parent, false)
        return IngredientItemViewHolder(view)
    }
}
