package com.jeffbrandon.recipebinder.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.activities.RecipeAppActivity
import com.jeffbrandon.recipebinder.room.RecipeData
import timber.log.Timber

class RecipeAdapter(private val activity: RecipeAppActivity, private var recipeList: MutableList<RecipeData>) :
    RecyclerView.Adapter<RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_menu_item, parent, false)
        return RecipeViewHolder(parent.context, view, activity)
    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe: RecipeData = recipeList[position]
        holder.apply {
            getId().text = recipe.id.toString()
            getName().text = recipe.name
            getCookTime().text = if(recipe.cookTime == 0) "" else recipe.cookTime.toString()
            getTags().removeAllViews()
            for(tag in recipe.tags) {
                val chip = tag.toChipView(activity)
                getTags().addView(chip)
            }
        }
    }

    fun getRecipe(i: Int): RecipeData {
        if(i in 0 until recipeList.size)
            return recipeList[i]
        Timber.w("Attempted to get recipe $i: out of bounds")
        return RecipeData(-1, "Unknown", 0, mutableListOf(), listOf(), listOf())
    }
    fun deleteRecipe(i: Int): RecipeData {
        if(i in 0 until recipeList.size) {
            val recipe = recipeList.removeAt(i)
            notifyDataSetChanged()
            return recipe
        }
        Timber.w("Attempted to delete recipe $i: out of bounds")
        return RecipeData(-1, "Unknown", 0, mutableListOf(), listOf(), listOf())
    }

    fun setDataSource(t: List<RecipeData>) {
        recipeList = t.toMutableList()
        notifyDataSetChanged()
    }
}