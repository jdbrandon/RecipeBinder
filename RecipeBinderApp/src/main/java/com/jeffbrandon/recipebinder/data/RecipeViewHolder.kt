package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData

class RecipeViewHolder(view: View, callback: (Long) -> Unit) : CallbackViewHolder<RecipeData, Long>(view, callback) {
    override var current: Long = -1
    private var nameView: TextView = view.findViewById(R.id.recipe_name)

    override fun bind(item: RecipeData) {
        current = item.id ?: -1
        nameView.text = item.name
    }
}
