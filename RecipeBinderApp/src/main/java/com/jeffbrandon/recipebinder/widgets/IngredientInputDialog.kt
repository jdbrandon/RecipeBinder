package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.enums.UnitType
import timber.log.Timber
import java.lang.IllegalArgumentException

class IngredientInputDialog(context: Context) : AlertDialog(context) {

    private lateinit var quantityInput: TextInputEditText
    private lateinit var ingredientInput: TextInputEditText
    private lateinit var fractionTextView: TextView
    private lateinit var unitsTextView: TextView
    private val unitMap: HashMap<String, String> =
        hashMapOf(
            Pair(context.getString(R.string.cup), "c"),
            Pair(context.getString(R.string.ounce), "oz"),
            Pair(context.getString(R.string.table_spoon), "Tbsp"),
            Pair(context.getString(R.string.tea_spoon), "tsp"),
            Pair(context.getString(R.string.pint), "pt"),
            Pair(context.getString(R.string.quart), "qt"),
            Pair(context.getString(R.string.gallon), "gal"),
            Pair(context.getString(R.string.liter), "l"),
            Pair(context.getString(R.string.milliliter), "ml"),
            Pair(context.getString(R.string.pound), "lb"),
            Pair(context.getString(R.string.gram), "g")
        )

    private fun keyListener(v: MaterialButton) {
        quantityInput.text!!.append(v.text)
    }

    private fun unitListener(v: Chip) {
        unitsTextView.text = unitMap[v.text]
    }

    private fun fractionListener(v: Chip) {
        val newText = v.text
        if(fractionTextView.text != newText)
            fractionTextView.text = newText
        else fractionTextView.text = ""
    }

    private fun setupAddIngredientButtons(v: View) {
        quantityInput = v.findViewById(R.id.quantity_input)
        ingredientInput = v.findViewById(R.id.ingredient_input)
        fractionTextView = v.findViewById(R.id.fraction_text_view)
        fractionTextView.text = ""
        unitsTextView = v.findViewById(R.id.units_text_view)
        v.findViewById<MaterialButton>(R.id.button_input_0).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_1).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_2).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_3).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_4).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_5).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_6).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_7).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_8).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_9).setOnClickListener { keyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_dot).setOnClickListener {
            if(quantityInput.text!!.contains('.'))
                return@setOnClickListener
            keyListener(it as MaterialButton)
        }
        v.findViewById<MaterialButton>(R.id.button_input_delete).setOnClickListener {
            quantityInput.text!!.apply {
                if(length > 0) delete(lastIndex - 1, lastIndex)
            }
        }
        v.findViewById<Chip>(R.id.chip_input_quarter).setOnClickListener { fractionListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_third).setOnClickListener { fractionListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_half).setOnClickListener { fractionListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_2_thirds).setOnClickListener { fractionListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_3_quarter).setOnClickListener { fractionListener(it as Chip) }

        v.findViewById<Chip>(R.id.cup_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.ounce_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.tbsp_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.tsp_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.pint_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.quart_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.gallon_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.liter_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.milliliter_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.pound_chip).setOnClickListener { unitListener(it as Chip) }
        v.findViewById<Chip>(R.id.gram_chip).setOnClickListener { unitListener(it as Chip) }
    }

    fun addIngredientListener(ingredientAdapter: IngredientAdapter) {
        val view = View.inflate(context, R.layout.dialog_add_ingredient, null)
        setupAddIngredientButtons(view)
        Builder(context)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.cancel()
                //TODO: test this with fractional inputs
                val amount = computeAmount(quantityInput.text.toString(), fractionTextView.text)
                try {
                    val type = UnitType.fromString(unitsTextView.text)
                    val newIngredient = Ingredient(ingredientInput.text.toString(), amount, type)
                    ingredientAdapter.add(newIngredient)
                } catch(e: IllegalArgumentException) {
                    Timber.e(e)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun computeAmount(whole: String, fraction: CharSequence?): Float {
        val n = whole.toInt()
        return n + when(fraction) {
            "1/4" -> 0.25f
            "1/3" -> 0.333f
            "1/2" -> 0.5f
            "2/3" -> 0.667f
            "3/4" -> 0.75f
            else -> 0.0f
        }
    }
}