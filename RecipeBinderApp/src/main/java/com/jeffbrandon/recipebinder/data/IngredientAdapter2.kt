package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R

class IngredientAdapter2(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_list_item, parent, false)
        return IngredientItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientItemViewHolder, position: Int) =
        holder.bind(ingredients[position])

    override fun getItemCount(): Int = ingredients.size
}
