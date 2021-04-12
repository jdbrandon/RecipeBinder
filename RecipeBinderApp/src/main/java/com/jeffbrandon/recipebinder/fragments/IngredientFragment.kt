package com.jeffbrandon.recipebinder.fragments

import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.IngredientAdapter2
import com.jeffbrandon.recipebinder.data.IngredientItemViewHolder
import com.jeffbrandon.recipebinder.room.RecipeData

class IngredientFragment : ListFragment<IngredientItemViewHolder>() {
    override val nameResourceId: Int = R.string.ingredients
    override fun buildAdapter(recipe: RecipeData): RecyclerView.Adapter<IngredientItemViewHolder> =
        IngredientAdapter2(recipe.ingredients)
}
