package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@SuppressWarnings("TooManyFunctions")
@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    state: SavedStateHandle,
    @ApplicationContext context: Context,
    lazyDispatchers: Lazy<IDispatchers>,
) : RecipeViewModel(dataSource, state, context, lazyDispatchers) {

    private var editIngredient: MutableStateFlow<Edit<Ingredient>?> = MutableStateFlow(null)
    private var editInstruction: MutableStateFlow<Edit<Instruction>?> = MutableStateFlow(null)

    val editIngredientLiveData: LiveData<Ingredient?> = editIngredient.map { it?.data }.asLiveData()
    val editInstructionLiveData: LiveData<Instruction?> =
        editInstruction.map { it?.data }.asLiveData()
    private var shouldWarn = false
    private var editing = false

    suspend fun setEditIngredient(data: Ingredient) {
        Timber.i("Editing $data")
        editIngredient.emit(Edit(getIngredientIndex(data), data))
    }

    suspend fun setEditInstruction(data: Instruction) {
        Timber.i("Editing $data")
        editInstruction.emit(Edit(getInstructionIndex(data), data))
    }

    suspend fun saveMetadata(
        recipeName: String,
        cookTime: Int,
        servings: Int,
        tags: Set<RecipeTag>
    ) {
        updateRecipeMetadata(recipeName.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }, abs(cookTime), abs(servings), tags)
    }

    suspend fun saveIngredient(data: Ingredient) {
        Timber.i("Saving ingredient")
        val sanitized = data.copy(name = data.name.trim())
        val oldValue = editIngredient.value
        editIngredient.emit(null)
        if (sanitized.name.isEmpty()) {
            return
        }
        oldValue?.apply {
            if (data != this.data) {
                index?.let { updateIngredient(it, sanitized) } ?: appendIngredient(sanitized)
            }
        } ?: error("Edit ingredient was not set")
    }

    suspend fun saveInstruction(data: Instruction) {
        Timber.i("Saving Instruction")
        val sanitized = data.copy(text = data.text.trim())
        val oldValue = editInstruction.value
        editInstruction.emit(null)
        if (sanitized.text.isEmpty()) {
            return
        }
        oldValue?.apply {
            if (data != this.data) {
                index?.let { updateInstruction(it, sanitized) } ?: appendInstruction(sanitized)
            }
        } ?: error("Edit Instruction was not set")
    }

    suspend fun convertIngredientUnits(amount: Float, startType: UnitType, targetType: UnitType) {
        editIngredient.value?.let {
            val newIngredient = it.copy(
                data = it.data.copy(amount = amount, unit = startType).convertTo(targetType)
            )
            editIngredient.emit(newIngredient)
        } ?: Timber.w("Edit ingredient null")
    }

    suspend fun moveEditIngredientBefore(target: Ingredient) {
        Timber.d("target ${target.name}")
        editIngredient.value?.let {
            Timber.d("edit: ${it.data.name}")
            moveTo(target, it.data)
        }
        editIngredient.emit(null)
    }

    suspend fun moveEditInstructionBefore(target: Instruction) {
        editInstruction.value?.let { moveTo(target, it.data) }
        editInstruction.emit(null)
    }

    suspend fun deleteEditIngredient() {
        editIngredient.value?.index?.let {
            deleteIngredient(it)
        }
        editIngredient.emit(null)
    }

    suspend fun deleteEditInstruction() {
        editInstruction.value?.index?.let {
            deleteInstruction(it)
        }
        editIngredient.emit(null)
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

    suspend fun stopEditing() = withContext(dispatchers.main) {
        editing = false
        editIngredient.emit(null)
        editInstruction.emit(null)
    }

    /**
     * @param index null when adding a new item
     * @param data to edit
     */
    private data class Edit<T>(val index: Int?, val data: T)
}
