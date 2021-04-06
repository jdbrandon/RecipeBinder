package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import timber.log.Timber

class RecipeViewHolder(view: View, itemClickCallback: (Long) -> Unit) :
    RecyclerView.ViewHolder(view) {

    private var id: Long = -1
    private var nameView: TextView = view.findViewById(R.id.recipe_name)

    init {
        view.setOnClickListener {
            Timber.i("id: $id ${nameView.text} clicked.")
            itemClickCallback.invoke(id)
        }
    }

    fun bind(recipe: RecipeData) {
        id = recipe.id ?: -1
        nameView.text = recipe.name
    }
}
