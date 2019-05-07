package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.nfc.FormatException
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
import timber.log.Timber

class IngredientInputDialog(context: Context) {
    private var id: Int = -1
    private val view = View.inflate(context, R.layout.dialog_add_ingredient, null)
    private var action = Mode.ADD

    init {
        setupAddIngredientViews(view)
    }

    private val dialog = AlertDialog.Builder(context)
        .setView(view)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.cancel()
            val amount = computeAmount(quantityInput.text.toString(), getSelectedFraction())
            val type = getSelectedType()
            val newIngredient = Ingredient(ingredientInput.text.toString(), amount, type)
            when(action) {
                Mode.ADD -> ingredientAdapter.add(newIngredient)
                Mode.UPDATE -> ingredientAdapter.update(id, newIngredient)
            }
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
        return when(fractionChipGroup.checkedChipId) {
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
    private lateinit var fractionChipGroup: ChipGroup
    private val unitMap: HashMap<String, String> =
        hashMapOf(
            Pair(context.getString(R.string.cup), context.getString(R.string.cup_shorthand)),
            Pair(context.getString(R.string.ounce), context.getString(R.string.ounce_shorthand)),
            Pair(context.getString(R.string.table_spoon), context.getString(R.string.tablespoon_shorthand)),
            Pair(context.getString(R.string.tea_spoon), context.getString(R.string.teaspoon_shorthand)),
            Pair(context.getString(R.string.pint), context.getString(R.string.pint_shorthand)),
            Pair(context.getString(R.string.quart), context.getString(R.string.quart_shorthand)),
            Pair(context.getString(R.string.gallon), context.getString(R.string.gallon_shorthand)),
            Pair(context.getString(R.string.liter), context.getString(R.string.liter_shorthand)),
            Pair(context.getString(R.string.milliliter), context.getString(R.string.mililiter_shorthand)),
            Pair(context.getString(R.string.pound), context.getString(R.string.pound_shorthand)),
            Pair(context.getString(R.string.gram), context.getString(R.string.gram_shorthand))
        )

    private fun numberKeyListener(v: MaterialButton) {
        quantityInput.text!!.append(v.text)
    }

    private fun unitChipListener(v: Chip) {
        unitsTextView.text =
            if(unitsTextView.text != unitMap[v.text])
                unitMap[v.text]
            else ""
    }

    private fun fractionChipListener(v: Chip) {
        fractionTextView.text =
            if(fractionTextView.text != v.text)
                v.text
            else ""
    }

    private fun setupAddIngredientViews(v: View) {
        quantityInput = v.findViewById(R.id.quantity_input)
        ingredientInput = v.findViewById(R.id.ingredient_input)
        fractionTextView = v.findViewById(R.id.fraction_text_view)
        unitsTextView = v.findViewById(R.id.units_text_view)
        unitsChipGroup = v.findViewById(R.id.unit_chips)
        fractionChipGroup = v.findViewById(R.id.frac_chip_group)

        v.findViewById<MaterialButton>(R.id.button_input_0)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_1)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_2)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_3)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_4)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_5)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_6)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_7)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_8)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_9)
            .setOnClickListener { numberKeyListener(it as MaterialButton) }
        v.findViewById<MaterialButton>(R.id.button_input_dot).setOnClickListener {
            if(quantityInput.text!!.contains('.'))
                return@setOnClickListener
            numberKeyListener(it as MaterialButton)
        }
        v.findViewById<MaterialButton>(R.id.button_input_delete).setOnClickListener {
            quantityInput.text!!.apply {
                if(length > 0) delete(lastIndex, length)
            }
        }
        v.findViewById<Chip>(R.id.chip_input_quarter).setOnClickListener { fractionChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_third).setOnClickListener { fractionChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_half).setOnClickListener { fractionChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_2_thirds).setOnClickListener { fractionChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.chip_input_3_quarter).setOnClickListener { fractionChipListener(it as Chip) }

        v.findViewById<Chip>(R.id.cup_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.ounce_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.tbsp_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.tsp_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.pint_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.quart_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.gallon_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.liter_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.milliliter_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.pound_chip).setOnClickListener { unitChipListener(it as Chip) }
        v.findViewById<Chip>(R.id.gram_chip).setOnClickListener { unitChipListener(it as Chip) }
    }

    fun addIngredientListener(ingredientAdapter: IngredientAdapter) {
        this.ingredientAdapter = ingredientAdapter
        action = Mode.ADD
        dialog.show()
    }

    fun updateIngredientListener(ingredientAdapter: IngredientAdapter, pos: Int) {
        this.ingredientAdapter = ingredientAdapter
        action = Mode.UPDATE
        populateViews(ingredientAdapter.getItem(pos))
        id = pos
        dialog.show()
    }

    private fun populateViews(ingredient: Ingredient) {
        val amountInfo = ingredient.amountString().split(" ")
        when(amountInfo.size) {
            1 -> quantityInput.setText(amountInfo[0])
            2 -> {
                quantityInput.setText(amountInfo[0])
                //check to see if it's a float or not
                if(amountInfo[1].toFloatOrNull() != null) setFraction(amountInfo[1])
                else setUnit(amountInfo[1])
            }
            3 -> {
                quantityInput.setText(amountInfo[0])
                setFraction(amountInfo[1])
                setUnit(amountInfo[2])
            }
            else -> {
                Timber.w("Failing to parse amount string!")
                throw FormatException("Unexpected amount string format ${ingredient.amountString()}")
            }
        }
        ingredientInput.setText(ingredient.name)
    }

    private fun setUnit(s: String) {
        unitsTextView.text = s
        when(UnitType.fromString(s)) {
            UnitType.GALLON -> unitsChipGroup.findViewById<Chip>(R.id.gallon_chip)?.isChecked = true
            UnitType.QUART -> unitsChipGroup.findViewById<Chip>(R.id.quart_chip)?.isChecked = true
            UnitType.PINT -> unitsChipGroup.findViewById<Chip>(R.id.pint_chip)?.isChecked = true
            UnitType.CUP -> unitsChipGroup.findViewById<Chip>(R.id.cup_chip)?.isChecked = true
            UnitType.OUNCE -> unitsChipGroup.findViewById<Chip>(R.id.ounce_chip)?.isChecked = true
            UnitType.TABLE_SPOON -> unitsChipGroup.findViewById<Chip>(R.id.tbsp_chip)?.isChecked = true
            UnitType.TEA_SPOON -> unitsChipGroup.findViewById<Chip>(R.id.tsp_chip)?.isChecked = true
            UnitType.POUND -> unitsChipGroup.findViewById<Chip>(R.id.pound_chip)?.isChecked = true
            UnitType.LITER -> unitsChipGroup.findViewById<Chip>(R.id.liter_chip)?.isChecked = true
            UnitType.MILLILITER -> unitsChipGroup.findViewById<Chip>(R.id.milliliter_chip)?.isChecked = true
            UnitType.GRAM -> unitsChipGroup.findViewById<Chip>(R.id.gram_chip)?.isChecked = true
            UnitType.NONE -> {
            }
        }
    }

    private fun setFraction(s: String) {
        fractionTextView.text = s
        when(s) {
            "1/4" -> fractionChipGroup.findViewById<Chip>(R.id.chip_input_quarter)?.isChecked = true
            "1/3" -> fractionChipGroup.findViewById<Chip>(R.id.chip_input_third)?.isChecked = true
            "1/2" -> fractionChipGroup.findViewById<Chip>(R.id.chip_input_half)?.isChecked = true
            "2/3" -> fractionChipGroup.findViewById<Chip>(R.id.chip_input_2_thirds)?.isChecked = true
            "3/4" -> fractionChipGroup.findViewById<Chip>(R.id.chip_input_3_quarter)?.isChecked = true
        }
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
        fractionChipGroup.clearCheck()
    }

    companion object {
        private enum class Mode {
            ADD,
            UPDATE
        }
    }
}