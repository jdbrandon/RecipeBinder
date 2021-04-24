package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@SuppressWarnings("TooManyFunctions")
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
    private var shouldWarn = false
    private var editing = false

    fun setEditIngredient(data: Ingredient) {
        Timber.i("Editing $data")
        editIngredient = Edit(getIngredientIndex(data) ?: error("Invalid index"), data)
        editIngredientData.value = data
    }

    fun setEditInstruction(data: Instruction) {
        Timber.i("Editing $data")
        editInstruction = Edit(getInstructionIndex(data) ?: error("Invalid index"), data)
        editInstructionData.value = data
    }

    fun saveMetadata(recipeName: String, cookTime: Int, tags: MutableList<RecipeTag>) {
        viewModelScope.launch {
            updateRecipeMetadata(recipeName.trim().capitalize(Locale.getDefault()), cookTime, tags)
        }
    }

    fun saveIngredient(data: Ingredient) {
        Timber.i("Saving ingredient")
        val sanitized = data.copy(name = data.name.trim())
        val oldValue = editIngredient
        editIngredient = null
        editIngredientData.value = null
        if (sanitized.name.isEmpty()) {
            return
        }
        viewModelScope.launch {
            oldValue?.let {
                if (data != it.data) {
                    updateIngredient(it.index, sanitized)
                }
            } ?: appendIngredient(sanitized)
        }
    }

    fun saveInstruction(data: Instruction) {
        Timber.i("Saving Instruction")
        val sanitized = data.copy(text = data.text.trim())
        val oldValue = editInstruction
        editInstruction = null
        editInstructionData.value = null
        if (sanitized.text.isEmpty()) {
            return
        }
        viewModelScope.launch {
            oldValue?.let {
                if (data != it.data) {
                    updateInstruction(it.index, sanitized)
                }
            } ?: appendInstruction(sanitized)
        }
    }

    fun convertIngredientUnits(unitType: UnitType) {
        editIngredient?.let {
            val newIngredient = it.copy(data = it.data.convertTo(unitType))
            editIngredient = newIngredient
            editIngredientData.value = newIngredient.data
        }
    }

    fun deleteEditIngredient() {
        viewModelScope.launch {
            editIngredient?.let {
                deleteIngredient(it.index)
            }
        }
    }

    fun deleteEditInstruction() {
        viewModelScope.launch {
            editInstruction?.let {
                deleteInstruction(it.index)
            }
        }
    }

    fun shouldWarnAboutUnsavedData(): Boolean {
        val result = editing && shouldWarn
        shouldWarn = false
        return result
    }

    fun beginEditing() {
        editing = true
        shouldWarn = true
    }

    fun stopEditing() {
        editing = false
    }

    private data class Edit<T>(val index: Int, val data: T)
}
