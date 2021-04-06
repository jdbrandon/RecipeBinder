package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData

class RecipeAdapter(private val recipeList: List<RecipeData>, private val cb: (Long) -> Unit) :
    RecyclerView.Adapter<RecipeViewHolder>() {

    private var currentPosition: Int? = null
    val position: Int? get() = currentPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_menu_item, parent, false)
        return RecipeViewHolder(view, cb).also { viewHolder ->
            view.setOnLongClickListener {
                currentPosition = viewHolder.adapterPosition
                false
            }
        }
    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }
}
