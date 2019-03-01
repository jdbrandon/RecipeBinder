package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jeffbrandon.recipebinder.R

class IngredientAdapter(context: Context, private val ingredients: MutableList<Ingredient>) :
    AppendableAdapter<Ingredient>(context, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: inflater.inflate(R.layout.ingredient_list_item, parent, false)
        val quantityView = view.findViewById(R.id.quantity) as TextView?
        val ingredientView = view.findViewById(R.id.ingredient_name) as TextView?
        ingredients[position].let { ingredient ->
            quantityView?.apply { text = ingredient.amountString() }
            ingredientView?.apply { text = ingredient.name }
        }
        return view
    }
}
