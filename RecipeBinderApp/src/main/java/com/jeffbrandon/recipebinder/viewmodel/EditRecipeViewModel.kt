package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    state: SavedStateHandle,
    @ApplicationContext context: Context,
) : RecipeViewModel(dataSource, state, context) {

    private var editIngredient: Edit<Ingredient>? = null
    private var editInstruction: Edit<Instruction>? = null

    private val editIngredientData = MutableLiveData<Ingredient?>()
    private val editInstructionData = MutableLiveData<Instruction?>()

    val editIngredientLiveData: LiveData<Ingredient?> = editIngredientData
    val editInstructionLiveData: LiveData<Instruction?> = editInstructionData

    fun setEditIngredient(data: Ingredient) {
        editIngredient = Edit(getIngredientIndex(data) ?: error("Invalid index"), data)
        editIngredientData.value = data
    }

    fun setEditInstruction(data: Instruction) {
        editInstruction = Edit(getInstructionIndex(data) ?: error("Invalid index"), data)
        editInstructionData.value = data
    }

    fun saveIngredient(data: Ingredient) {
        editIngredient?.let {
            if (data != it.data) {
                 updateIngredient(it.index, data)
            }
        } ?:  appendIngredient(data)
        editIngredient = null
        editIngredientData.value = null
    }

    fun saveInstruction(data: Instruction) {
        editInstruction?.let {
            if (data != it.data) {
                updateInstruction(it.index, data)
            }
        } ?:  appendInstruction(data)
        editInstruction = null
        editInstructionData.value = null
    }

    fun convertIngredientUnits(unitType: UnitType) {
        editIngredient?.let {
            val newIngredient = it.copy(data = it.data.convertTo(unitType))
            editIngredient = newIngredient
            editIngredientData.value = newIngredient.data
        }
    }

    private data class Edit<T>(val index: Int, val data: T)
}
