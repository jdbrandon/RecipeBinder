package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel

class RecipeAdapter(
    recipeList: List<RecipeData>,
    private val viewModel: RecipeMenuViewModel,
) : ListRecyclerViewAdapter<RecipeViewHolder, RecipeData>(recipeList) {

    private var currentId: Long? = null
    val recipeId: Long? get() = currentId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_menu_item, parent, false)
        return RecipeViewHolder(viewModel, view)
    }
}
