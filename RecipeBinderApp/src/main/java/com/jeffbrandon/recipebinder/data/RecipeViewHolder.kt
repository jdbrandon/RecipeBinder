package com.jeffbrandon.recipebinder.data

import android.view.View
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.util.NavigationUtil
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class RecipeViewHolder constructor(private val viewModel: RecipeMenuViewModel, view: View) :
    BindableViewHolder<RecipeData>(view) {
    private var nameView: TextView = view.findViewById(R.id.recipe_name)

    override fun bind(item: RecipeData) {
        nameView.text = item.name
        item.recipeId?.let { id ->
            nameView.setOnClickListener {
                NavigationUtil.viewRecipe(nameView.context, id)
            }
            nameView.setOnLongClickListener {
                viewModel.run {
                    viewModelScope.launch { setRecipe(id) }
                }
                false
            }
        }
    }
}
