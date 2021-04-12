package com.jeffbrandon.recipebinder.data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jeffbrandon.recipebinder.R

/**
 * @deprecated
 */
class IngredientAdapter(
    context: Context,
    ingredients: MutableList<Ingredient>,
    private val layout: Int = R.layout.ingredient_list_item,
) : AppendableAdapter<Ingredient>(context, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(layout, parent, false)
        val quantityView = view.findViewById(R.id.quantity) as TextView?
        val ingredientView = view.findViewById(R.id.ingredient_name) as TextView?
        dataSource[position].let { ingredient ->
            quantityView?.apply { text = ingredient.amountString(view.context) }
            ingredientView?.apply { text = ingredient.name }
        }
        return view
    }
}
