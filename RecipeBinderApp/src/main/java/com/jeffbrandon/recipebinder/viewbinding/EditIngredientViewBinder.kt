package com.jeffbrandon.recipebinder.viewbinding

import android.content.Context
import android.graphics.drawable.Animatable
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
    private lateinit var binder: FragmentAddIngredientBinding

    fun bind(
        vm: EditRecipeViewModel,
        viewRoot: View,
        fm: FragmentManager,
        lifecycle: LifecycleOwner,
    ) {
        binder = FragmentAddIngredientBinding.bind(viewRoot)
        with(binder) {
            setupAddIngredientViews()
            setupConvertButton(viewRoot.context, vm)
            setupDeleteButton(viewRoot, vm, fm)
            setupSaveButton(viewRoot.context) {
                saveIngredient(vm)
                fm.popBackStack()
            }
        }
        vm.editIngredientLiveData.observe(lifecycle) { ingredient ->
            // Ingredient is null when adding an ingredient
            ingredient?.let {
                with(binder) {
                    ingredientInput.setText(ingredient.name)
                    quantityInput.setText(ingredient.amountWhole().toString())
                    deleteButton.visibility = View.VISIBLE

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
            } ?: run { binder.deleteButton.visibility = View.GONE }
        }
    }

    private fun FragmentAddIngredientBinding.setupConvertButton(
        context: Context,
        vm: EditRecipeViewModel,
    ) {
        convertButton.setOnClickListener {
            ConvertDialog(context, getSelectedUnit(), vm)
        }
    }

    private fun FragmentAddIngredientBinding.setupDeleteButton(
        view: View,
        vm: EditRecipeViewModel,
        fm: FragmentManager,
    ) {
        deleteButton.setOnClickListener {
            Snackbar.make(view, R.string.delete_this_confirmation_message, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok) {
                    vm.deleteEditIngredient()
                    fm.popBackStack()
                }.show()
        }
    }

    private fun FragmentAddIngredientBinding.setupSaveButton(
        context: Context,
        callback: () -> Unit,
    ) = AnimatedVectorDrawableCompat.create(context, R.drawable.save_to_edit)?.let {
        saveIngredientButton.apply {
            setImageDrawable(it)
            setOnClickListener {
                (drawable as Animatable).start()
                callback()
            }
        }
    } ?: saveIngredientButton.setOnClickListener {
        callback()
    }

    private fun FragmentAddIngredientBinding.saveIngredient(vm: EditRecipeViewModel) {
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
        vm.saveIngredient(newIngredient)
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
        val fractionChipList = listOf(chipInputQuarter,
                                      chipInputThird,
                                      chipInputHalf,
                                      chipInput2Thirds,
                                      chipInput3Quarter)
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

    private fun unitChipListener(v: Chip) {
        selectedUnitText = if (selectedUnitText != unitMap[v.text]) unitMap[v.text]
        else null
    }

    private fun fractionChipListener(v: Chip) = v.text.toString().let { text ->
        selectedFractionText = if (selectedFractionText != text) text
        else null
    }

    private fun computeAmount(whole: CharSequence?, fraction: FractionalMeasurement): Float {
        val n = if (whole.isNullOrEmpty()) 0f else whole.toString().toFloat()
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

    private val unitMap: HashMap<String, String> = hashMapOf(Pair(context.getString(R.string.cup),
                                                                  context.getString(R.string.abbreviation_cup)),
                                                             Pair(context.getString(R.string.ounce),
                                                                  context.getString(R.string.abbreviation_ounce)),
                                                             Pair(context.getString(R.string.table_spoon),
                                                                  context.getString(R.string.abbreviation_tablespoon)),
                                                             Pair(context.getString(R.string.tea_spoon),
                                                                  context.getString(R.string.abbreviation_teaspoon)),
                                                             Pair(context.getString(R.string.pint),
                                                                  context.getString(R.string.abbreviation_pint)),
                                                             Pair(context.getString(R.string.quart),
                                                                  context.getString(R.string.abbreviation_quart)),
                                                             Pair(context.getString(R.string.gallon),
                                                                  context.getString(R.string.abbreviation_gallon)),
                                                             Pair(context.getString(R.string.liter),
                                                                  context.getString(R.string.abbreviation_liter)),
                                                             Pair(context.getString(R.string.milliliter),
                                                                  context.getString(R.string.abbreviation_milliliter)),
                                                             Pair(context.getString(R.string.pound),
                                                                  context.getString(R.string.abbreviation_pound)),
                                                             Pair(context.getString(R.string.gram),
                                                                  context.getString(R.string.abbreviation_gram)))
}
