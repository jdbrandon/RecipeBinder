package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.enums.UnitType

class IngredientInputDialog(context: Context) : AlertDialog(context) {
    private val view = View.inflate(context, R.layout.dialog_add_ingredient, null)

    init {
        setupAddIngredientViews(view)
    }

    private val dialog = Builder(context)
        .setView(view)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.cancel()
            val amount = computeAmount(quantityInput.text.toString(), getSelectedFraction())
            val type = getSelectedType()
            val newIngredient = Ingredient(ingredientInput.text.toString(), amount, type)
            ingredientAdapter.add(newIngredient)
            clearValues()
        }
        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
            clearValues()
        }.create()

    private fun getSelectedType(): UnitType {
        return when(unitsChipGroup.checkedChipId) {
            R.id.gallon_chip -> UnitType.GALLON
            R.id.quart_chip -> UnitType.QUART
            R.id.pint_chip -> UnitType.PINT
            R.id.cup_chip -> UnitType.CUP
            R.id.ounce_chip -> UnitType.OUNCE
            R.id.tbsp_chip -> UnitType.TABLE_SPOON
            R.id.tsp_chip -> UnitType.TEA_SPOON
            R.id.pound_chip -> UnitType.POUND
            R.id.liter_chip -> UnitType.LITER
            R.id.milliliter_chip -> UnitType.MILLILITER
            R.id.gram_chip -> UnitType.GRAM
            else -> UnitType.NONE
        }
    }

    private fun getSelectedFraction(): Float {
        return when(fracChipGroup.checkedChipId) {
            R.id.chip_input_quarter -> 0.25f
            R.id.chip_input_third -> 0.33f
            R.id.chip_input_half -> 0.5f
            R.id.chip_input_2_thirds -> 0.66f
            R.id.chip_input_3_quarter -> 0.75f
            else -> 0.0f
        }
    }

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var quantityInput: TextInputEditText
    private lateinit var ingredientInput: TextInputEditText
    private lateinit var fractionTextView: TextView
    private lateinit var unitsTextView: TextView
    private lateinit var unitsChipGroup: ChipGroup
    private lateinit var fracChipGroup: ChipGroup
    private lateinit var cupChip: Chip
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

    private fun setupAddIngredientViews(v: View) {
        quantityInput = v.findViewById(R.id.quantity_input)
        ingredientInput = v.findViewById(R.id.ingredient_input)
        fractionTextView = v.findViewById(R.id.fraction_text_view)
        fractionTextView.text = ""
        unitsTextView = v.findViewById(R.id.units_text_view)
        cupChip = v.findViewById(R.id.cup_chip)
        unitsChipGroup = v.findViewById(R.id.unit_chips)
        fracChipGroup = v.findViewById(R.id.frac_chip_group)

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
        this.ingredientAdapter = ingredientAdapter
        dialog.show()
    }

    private fun computeAmount(whole: String, fraction: Float): Float {
        val n = if(whole.isEmpty()) 0.0f else whole.toFloat()
        return n + fraction
    }

    private fun clearValues() {
        quantityInput.text!!.clear()
        fractionTextView.text = ""
        unitsTextView.text = ""
        ingredientInput.text!!.clear()
        ingredientInput.clearFocus()
        unitsChipGroup.clearCheck()
        fracChipGroup.clearCheck()
    }
}