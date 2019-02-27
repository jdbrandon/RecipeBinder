package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.jeffbrandon.recipebinder.R

class IngredientAdapter(context: Context, private val ingredients: MutableList<Ingredient>) :
    AppendableAdapter<Ingredient>(context, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: inflater.inflate(R.layout.ingredient_list_item, parent, false)
        val q = view.findViewById(R.id.quantity) as AppCompatTextView?
        val i = view.findViewById(R.id.ingredient_name) as AppCompatTextView?
        ingredients[position].let { ingredient ->
            q?.apply { text = ingredient.amountString() }
            i?.apply { text = ingredient.name }
        }
        return view
    }
}
