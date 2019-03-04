package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData

class RecipeAdapter(context: Context, recipeList: MutableList<RecipeData>) :
    AppendableAdapter<RecipeData>(context, recipeList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.recipe_menu_item, parent, false)
        val idView = view.findViewById<TextView>(R.id.id_view)
        val nameView = view.findViewById<TextView>(R.id.recipe_name)
        val tagsGroup = view.findViewById<ChipGroup>(R.id.tags_group)
        dataSource[position].run {
            idView.text = id.toString()
            nameView.text = name
            nameView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 11.0f)
            for(tag in tags) {
                val chip = tag.toChipView(context)
                chip.isCheckable = false
                tagsGroup.addView(chip)
            }
        }
        return view
    }
}