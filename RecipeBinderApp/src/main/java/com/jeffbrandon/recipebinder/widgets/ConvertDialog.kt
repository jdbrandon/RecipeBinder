package com.jeffbrandon.recipebinder.widgets

import android.content.Context
import android.content.DialogInterface
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.viewmodel.EditRecipeViewModel

class ConvertDialog(context: Context, startUnit: UnitType, vm: EditRecipeViewModel) {
    private companion object {
        private val volumeUnits = setOf(UnitType.NONE,
                                        UnitType.OUNCE,
                                        UnitType.CUP,
                                        UnitType.MILLILITER,
                                        UnitType.MILLILITER,
                                        UnitType.LITER,
                                        UnitType.TEA_SPOON,
                                        UnitType.TABLE_SPOON,
                                        UnitType.PINT,
                                        UnitType.QUART,
                                        UnitType.GALLON)

        private val massUnits = setOf(UnitType.NONE, UnitType.OUNCE, UnitType.POUND, UnitType.GRAM)
    }

    init {
        val convertibleUnitTypes: List<UnitType> = (when (startUnit) {
            UnitType.OUNCE, UnitType.NONE -> volumeUnits + massUnits
            UnitType.GALLON,
            UnitType.QUART,
            UnitType.PINT,
            UnitType.CUP,
            UnitType.TABLE_SPOON,
            UnitType.TEA_SPOON,
            UnitType.LITER,
            UnitType.MILLILITER,
            -> volumeUnits
            UnitType.POUND,
            UnitType.GRAM,
            -> massUnits
        } - startUnit).toList()

        val listener = DialogInterface.OnClickListener { _, index ->
            vm.convertIngredientUnits(convertibleUnitTypes[index])
        }

        val adapter = ArrayAdapter(context,
                                   android.R.layout.select_dialog_singlechoice,
                                   convertibleUnitTypes.map { unit ->
                                       unit.getString(context, true)
                                   })

        val startName = startUnit.getString(context, true)

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.convert_unit_format, startName))
            .setAdapter(adapter, listener).setCancelable(true).show()
    }
}
