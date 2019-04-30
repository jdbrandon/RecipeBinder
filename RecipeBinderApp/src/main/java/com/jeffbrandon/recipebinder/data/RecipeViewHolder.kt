package com.jeffbrandon.recipebinder.data

import androidx.core.app.ActivityOptionsCompat
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.activities.RecipeAppActivity
import timber.log.Timber

class RecipeViewHolder(context: Context, view: View, activity: RecipeAppActivity) : RecyclerView.ViewHolder(view) {

    private var idView: TextView = view.findViewById(R.id.id_view)
    private var nameView: TextView = view.findViewById(R.id.recipe_name)

    init {
        view.setOnClickListener {
            val nameView = view.findViewById<TextView>(R.id.recipe_name)
            val recipeId = view.findViewById<TextView>(R.id.id_view)
            Timber.i("id: $recipeId. dbID: ${recipeId.text}, ${nameView.text} clicked.")
            val intent = activity.getViewActivityIntent(recipeId.text.toString().toLong())
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //Setup transition to view activity
                val namePair = Pair(nameView as View, context.getString(R.string.name_transition))
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                                                                                 namePair)
                activity.startActivity(intent, options.toBundle())
            } else
                activity.startActivity(intent)

        }
    }

    fun getId(): TextView = idView
    fun getName(): TextView = nameView
}
