package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.chip.Chip
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.databinding.DialogAddIngredientBinding
import com.jeffbrandon.recipebinder.enums.FractionalMeasurement
import com.jeffbrandon.recipebinder.enums.UnitType

class IngredientInputDialog(context: Context) {
    private var id: Int = -1
    private val view = View.inflate(context, R.layout.dialog_add_ingredient, null)
    private val binder = DialogAddIngredientBinding.bind(view)
    private var action = Mode.ADD
    private val oneQuarter: String
    private val oneThird: String
    private val oneHalf: String
    private val twoThirds: String
    private val threeQuarter: String
    private lateinit var ingredientAdapter: IngredientAdapter
    private var unit: String? = null
    private var selectedUnit: String?
        get() = unit
        set(v) {
            unit = v.also { updateQuantitySuffix(selectedFraction, it) }
        }
    private var fraction: String? = null
    private var selectedFraction: String?
        get() = fraction
        set(v) {
            fraction = v.also { updateQuantitySuffix(it, selectedUnit) }
        }

    private val dialog by lazy {
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val amount =
                    computeAmount(binder.quantityInput.text?.trim(), getSelectedFraction())
                val type = getSelectedType()
                val newIngredient = Ingredient(binder.ingredientInput.text.toString(), amount, type)
                when (action) {
                    Mode.ADD -> ingredientAdapter.add(newIngredient)
                    Mode.UPDATE -> ingredientAdapter.update(id, newIngredient)
                }
                binder.clearValues()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                binder.clearValues()
            }.create()
    }

    init {
        with(context) {
            oneQuarter = getString(R.string._1_quarter)
            oneThird = getString(R.string._1_third)
            oneHalf = getString(R.string._1_half)
            twoThirds = getString(R.string._2_thirds)
            threeQuarter = getString(R.string._3_quarters)
        }
        binder.setupAddIngredientViews()
    }


    fun addIngredientListener(ingredientAdapter: IngredientAdapter) {
        this.ingredientAdapter = ingredientAdapter
        action = Mode.ADD
        dialog.show()
    }

    fun updateIngredientListener(ingredientAdapter: IngredientAdapter, pos: Int) {
        this.ingredientAdapter = ingredientAdapter
        action = Mode.UPDATE
        binder.populateViews(ingredientAdapter.getItem(pos))
        id = pos
        dialog.show()
    }

    private fun getSelectedType(): UnitType {
        return when (binder.unitChips.checkedChipId) {
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
        return when (binder.fractionChipGroup.checkedChipId) {
            R.id.chip_input_quarter -> 0.25f
            R.id.chip_input_third -> 0.33f
            R.id.chip_input_half -> 0.5f
            R.id.chip_input_2_thirds -> 0.66f
            R.id.chip_input_3_quarter -> 0.75f
            else -> 0.0f
        }
    }

    private val unitMap: HashMap<String, String> =
        hashMapOf(
            Pair(context.getString(R.string.cup), context.getString(R.string.cup_shorthand)),
            Pair(context.getString(R.string.ounce), context.getString(R.string.ounce_shorthand)),
            Pair(
                context.getString(R.string.table_spoon),
                context.getString(R.string.tablespoon_shorthand)
            ),
            Pair(
                context.getString(R.string.tea_spoon),
                context.getString(R.string.teaspoon_shorthand)
            ),
            Pair(context.getString(R.string.pint), context.getString(R.string.pint_shorthand)),
            Pair(context.getString(R.string.quart), context.getString(R.string.quart_shorthand)),
            Pair(context.getString(R.string.gallon), context.getString(R.string.gallon_shorthand)),
            Pair(context.getString(R.string.liter), context.getString(R.string.liter_shorthand)),
            Pair(
                context.getString(R.string.milliliter),
                context.getString(R.string.milliliter_shorthand)
            ),
            Pair(context.getString(R.string.pound), context.getString(R.string.pound_shorthand)),
            Pair(context.getString(R.string.gram), context.getString(R.string.gram_shorthand))
        )

    private fun unitChipListener(v: Chip) {
        selectedUnit =
            if (selectedUnit != unitMap[v.text])
                unitMap[v.text]
            else null
    }

    private fun fractionChipListener(v: Chip) = v.text.toString().let { text ->
        selectedFraction =
            if (selectedFraction != text)
                text
            else null
    }

    private fun updateQuantitySuffix(fraction: String?, unit: String?) {
        val suffix = fraction?.let { safeFraction ->
            unit?.let { unit -> "$safeFraction $unit" } ?: safeFraction
        }
            ?: unit
        binder.quantityInputLayout.suffixText = suffix
        // Setting isExpandedHintEnabled doesn't animate, this is hacky but works
        if (binder.quantityInput.text.isNullOrBlank()) {
            if (!suffix.isNullOrBlank()) binder.quantityInput.setText(" ")
            else binder.quantityInput.setText("")
        }
    }

    private fun DialogAddIngredientBinding.setupAddIngredientViews() {
        val fractionChipList = listOf(
            chipInputQuarter,
            chipInputThird,
            chipInputHalf,
            chipInput2Thirds,
            chipInput3Quarter
        )
        val unitChipList = listOf(
            cupChip,
            ounceChip,
            tbspChip,
            tspChip,
            pintChip,
            quartChip,
            gallonChip,
            literChip,
            milliliterChip,
            poundChip,
            gramChip
        )

        fractionChipList.forEach { view ->
            view.setOnClickListener { fractionChipListener(it as Chip) }
        }

        unitChipList.forEach { resId ->
            resId.setOnClickListener { unitChipListener(it as Chip) }
        }
    }

    private fun DialogAddIngredientBinding.populateViews(ingredient: Ingredient) {
        val whole = ingredient.amountWhole()
        setFraction(ingredient.amountFraction())
        setUnit(ingredient.unit)
        quantityInput.setText(if (whole != 0) whole.toString() else "")
        ingredientInput.setText(ingredient.name)
    }

    private fun DialogAddIngredientBinding.setUnit(type: UnitType) {
        when (type) {
            UnitType.GALLON -> selectUnit(gallonChip)
            UnitType.QUART -> selectUnit(quartChip)
            UnitType.PINT -> selectUnit(pintChip)
            UnitType.CUP -> selectUnit(cupChip)
            UnitType.OUNCE -> selectUnit(ounceChip)
            UnitType.TABLE_SPOON -> selectUnit(tbspChip)
            UnitType.TEA_SPOON -> selectUnit(tspChip)
            UnitType.POUND -> selectUnit(poundChip)
            UnitType.LITER -> selectUnit(literChip)
            UnitType.MILLILITER -> selectUnit(milliliterChip)
            UnitType.GRAM -> selectUnit(gramChip)
            UnitType.NONE -> selectedUnit = null
        }
    }

    private fun selectUnit(v: Chip) {
        selectedUnit = v.text.toString()
        v.isChecked = true
    }

    private fun DialogAddIngredientBinding.setFraction(fraction: FractionalMeasurement) {
        when (fraction) {
            FractionalMeasurement.QUARTER -> selectFraction(chipInputQuarter)
            FractionalMeasurement.THIRD -> selectFraction(chipInputThird)
            FractionalMeasurement.HALF -> selectFraction(chipInputHalf)
            FractionalMeasurement.THIRD_TWO -> selectFraction(chipInput2Thirds)
            FractionalMeasurement.QUARTER_THREE -> selectFraction(chipInput3Quarter)
            else -> selectedFraction = null
        }
    }

    private fun selectFraction(v: Chip) {
        selectedFraction = v.text.toString()
        v.isChecked = true
    }

    private fun computeAmount(whole: CharSequence?, fraction: Float): Float {
        val n = if (whole.isNullOrEmpty()) 0f else whole.toString().toFloat()
        return n + fraction
    }

    private fun DialogAddIngredientBinding.clearValues() {
        quantityInput.text!!.clear()
        quantityInputLayout.suffixText = ""
        fraction = null
        unit = null
        ingredientInput.text!!.clear()
        ingredientInput.clearFocus()
        unitChips.clearCheck()
        fractionChipGroup.clearCheck()
    }

    companion object {
        private enum class Mode {
            ADD,
            UPDATE
        }
    }
}
