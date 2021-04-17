package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData

class RecipeAdapter(
    recipeList: List<RecipeData>,
    private val callback: (Long) -> Unit,
) : ListRecyclerViewAdapter<RecipeViewHolder, RecipeData>(recipeList) {

    private var currentPosition: Int? = null
    val position: Int? get() = currentPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_menu_item, parent, false)
        return RecipeViewHolder(view, callback).also { viewHolder ->
            view.setOnLongClickListener {
                currentPosition = viewHolder.bindingAdapterPosition
                false
            }
        }
    }
}
