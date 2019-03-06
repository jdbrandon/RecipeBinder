package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.activities.RecipeAppActivity
import timber.log.Timber

class RecipeViewHolder(context: Context, view: View, activity: RecipeAppActivity) : RecyclerView.ViewHolder(view) {

    private var idView: TextView = view.findViewById(R.id.id_view)
    private var nameView: TextView = view.findViewById(R.id.recipe_name)
    private var timeView: TextView = view.findViewById(R.id.cook_time)
    private var tagsGroup: ChipGroup = view.findViewById(R.id.tags_group)

    init {
        view.setOnClickListener {
            val nameView = view.findViewById<TextView>(R.id.recipe_name)
            val recipeId = view.findViewById<TextView>(R.id.id_view)
            Timber.i("id: $recipeId. dbID: ${recipeId.text}, ${nameView.text} clicked.")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nameView.transitionName = context.getString(R.string.name_transition)
                activity.navigateToViewRecipeActivity(recipeId.text.toString().toLong(),
                                                      nameView,
                                                      context.getString(R.string.name_transition))
            } else
                activity.navigateToViewRecipeActivity(recipeId.text.toString().toLong())

        }
    }

    fun getId(): TextView = idView
    fun getName(): TextView = nameView
    fun getCookTime(): TextView = timeView
    fun getTags(): ChipGroup = tagsGroup
}
