package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel

class RecipeViewHolder(private val viewModel: RecipeMenuViewModel, view: View) : BindableViewHolder<RecipeData>(view) {
    private var nameView: TextView = view.findViewById(R.id.recipe_name)

    override fun bind(item: RecipeData) {
        nameView.text = item.name
        item.recipeId?.let { id ->
            nameView.setOnClickListener {
                NavigationUtil.viewRecipe(nameView.context, id)
            }
            nameView.setOnLongClickListener {
                viewModel.setRecipe(id)
                false
            }
        }
    }
}
