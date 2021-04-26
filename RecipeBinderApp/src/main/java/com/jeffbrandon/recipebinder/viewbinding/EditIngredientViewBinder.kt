package com.jeffbrandon.recipebinder.viewbinding

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.databinding.FragmentAddIngredientBinding
import com.jeffbrandon.recipebinder.enums.FractionalMeasurement
import com.jeffbrandon.recipebinder.enums.FractionalMeasurement.Companion.fractionViewMap
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.enums.UnitType.Companion.unitMap
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel
import com.jeffbrandon.recipebinder.widgets.ConvertDialog
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class EditIngredientViewBinder @Inject constructor(@ApplicationContext context: Context) {
    private var unit: String? = null
    private var selectedUnitText: String?
        get() = unit
        set(v) {
            unit = v.also { updateQuantitySuffix(selectedFractionText, it) }
        }
    private var fraction: String? = null
    private var selectedFractionText: String?
        get() = fraction
        set(v) {
            fraction = v.also { updateQuantitySuffix(it, selectedUnitText) }
        }
    private val unitChipListener: (Chip) -> Unit = { v ->
        selectedUnitText = if (selectedUnitText != unitMap[v.text]) unitMap[v.text]
        else null
    }

    private val fractionChipListener: (Chip) -> Unit = { v ->
        v.text.toString().let { text ->
            selectedFractionText = if (selectedFractionText != text) text
            else null
        }
    }

    private val unitMap: HashMap<String, String> =
        hashMapOf(Pair(context.getString(R.string.cup), context.getString(R.string.abbreviation_cup)),
                  Pair(context.getString(R.string.ounce), context.getString(R.string.abbreviation_ounce)),
                  Pair(context.getString(R.string.table_spoon), context.getString(R.string.abbreviation_tablespoon)),
                  Pair(context.getString(R.string.tea_spoon), context.getString(R.string.abbreviation_teaspoon)),
                  Pair(context.getString(R.string.pint), context.getString(R.string.abbreviation_pint)),
                  Pair(context.getString(R.string.quart), context.getString(R.string.abbreviation_quart)),
                  Pair(context.getString(R.string.gallon), context.getString(R.string.abbreviation_gallon)),
                  Pair(context.getString(R.string.liter), context.getString(R.string.abbreviation_liter)),
                  Pair(context.getString(R.string.milliliter), context.getString(R.string.abbreviation_milliliter)),
                  Pair(context.getString(R.string.pound), context.getString(R.string.abbreviation_pound)),
                  Pair(context.getString(R.string.gram), context.getString(R.string.abbreviation_gram)))
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var binder: FragmentAddIngredientBinding

    fun bind(
        vm: EditRecipeViewModel,
        viewRoot: View,
        fm: FragmentManager,
        lifecycle: LifecycleOwner,
    ) {
        viewModel = vm
        binder = FragmentAddIngredientBinding.bind(viewRoot)
        with(binder) {
            setupAddIngredientViews()

            convertButton.setOnClickListener {
                ConvertDialog(viewRoot.context, getSelectedUnit(), viewModel)
            }

            deleteButton.setOnClickListener {
                Snackbar.make(viewRoot, R.string.delete_this_confirmation_message, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok) {
                        viewModel.deleteEditIngredient()
                        fm.popBackStack()
                    }.show()
            }

            saveIngredientButton.setOnClickListener {
                saveAndPop(fm)
            }
        }
        vm.editIngredientLiveData.observe(lifecycle) { ingredient ->
            // Ingredient is null when adding an ingredient
            if (ingredient == null) {
                Timber.i("Ingredient was null, hiding delete button")
                binder.deleteButton.hide()
            } else {
                with(binder) {
                    ingredientInput.setText(ingredient.name)
                    quantityInput.setText(ingredient.amountWhole().toString())
                    deleteButton.show()

                    val fractionTagMap = fractionViewMap()
                    fractionTagMap[ingredient.amountFraction()]?.apply {
                        isChecked = true
                        callOnClick()
                    }
                    val unitTagMap = unitMap()
                    unitTagMap[ingredient.unit]?.apply {
                        isChecked = true
                        callOnClick()
                    }
                }
            }
        }
    }

    fun onResume() {
        viewModel.beginEditing()
    }

    fun continueEditing() {
        viewModel.beginEditing()
    }

    private fun FragmentAddIngredientBinding.saveAndPop(fm: FragmentManager) {
        saveIngredient()
        fm.popBackStack()
    }

    private fun FragmentAddIngredientBinding.saveIngredient() {
        val whole = this.quantityInput.text
        val fraction = when (fractionChipGroup.checkedChipId) {
            R.id.chip_input_quarter -> FractionalMeasurement.QUARTER
            R.id.chip_input_third -> FractionalMeasurement.THIRD
            R.id.chip_input_half -> FractionalMeasurement.HALF
            R.id.chip_input_2_thirds -> FractionalMeasurement.THIRD_TWO
            R.id.chip_input_3_quarter -> FractionalMeasurement.QUARTER_THREE
            else -> FractionalMeasurement.ZERO
        }

        val amount = computeAmount(whole, fraction)

        Timber.d("amount: $amount")

        val unit = getSelectedUnit()
        val newIngredient = Ingredient(ingredientInput.text.toString(), amount, unit)
        Timber.d(newIngredient.toString())
        viewModel.saveIngredient(newIngredient)
    }

    private fun FragmentAddIngredientBinding.getSelectedUnit() = when (unitChips.checkedChipId) {
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

    private fun FragmentAddIngredientBinding.setupAddIngredientViews() {
        val fractionChipList =
            listOf(chipInputQuarter, chipInputThird, chipInputHalf, chipInput2Thirds, chipInput3Quarter)
        val unitChipList = listOf(cupChip,
                                  ounceChip,
                                  tbspChip,
                                  tspChip,
                                  pintChip,
                                  quartChip,
                                  gallonChip,
                                  literChip,
                                  milliliterChip,
                                  poundChip,
                                  gramChip)

        fractionChipList.forEach { view ->
            view.setOnClickListener { fractionChipListener(it as Chip) }
        }

        unitChipList.forEach { resId ->
            resId.setOnClickListener { unitChipListener(it as Chip) }
        }
    }

    private fun computeAmount(whole: CharSequence?, fraction: FractionalMeasurement): Float {
        val n = if (whole.isNullOrBlank()) 0f else whole.toString().toFloat()
        return n + fraction.toFloat()
    }

    private fun updateQuantitySuffix(fraction: String?, unit: String?) {
        val suffix = fraction?.let { safeFraction ->
            unit?.let { unit -> "$safeFraction $unit" } ?: safeFraction
        } ?: unit
        binder.quantityInputLayout.suffixText = suffix
        // Setting isExpandedHintEnabled doesn't animate, this is hacky but works
        if (binder.quantityInput.text.isNullOrBlank()) {
            if (!suffix.isNullOrBlank()) binder.quantityInput.setText(" ")
            else binder.quantityInput.setText("")
        }
    }
}
