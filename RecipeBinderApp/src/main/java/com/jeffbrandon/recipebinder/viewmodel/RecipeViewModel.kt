package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class RecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    state: SavedStateHandle,
    @ApplicationContext context: Context,
) : ViewModel() {
    private val id: Long = state[context.getString(R.string.extra_recipe_id)]
        ?: error("Did you forget to provide recipeId in the intent extras or fragment args bundle")
    private val db by lazy { dataSource.get() }
    private val recipe = liveData {
        emitSource(fetchRecipe(id))
    }

    fun getRecipe(): LiveData<RecipeData> = recipe

    protected fun getIngredientIndex(data: Ingredient): Int? =
        recipe.value?.ingredients?.indexOf(data)

    protected fun getInstructionIndex(data: Instruction): Int? =
        recipe.value?.instructions?.indexOf(data)

    protected fun appendIngredient(data: Ingredient) {
        val newIngredientList = recipe.value?.ingredients?.toMutableList()?.apply { add(data) }
            ?: error("failed to add ingredient")
        updateRecipeIngredients(newIngredientList)
    }

    protected fun appendInstruction(data: Instruction) {
        val newInstructionList = recipe.value?.instructions?.toMutableList()?.apply { add(data) }
            ?: error("failed to add ingredient")
        updateRecipeInstructions(newInstructionList)
    }

    protected fun updateIngredient(index: Int, data: Ingredient) {
        val newIngredientList =
            recipe.value?.ingredients?.toMutableList()?.apply { set(index, data) }
                ?: error("failed to update ingredient")
        updateRecipeIngredients(newIngredientList)
    }

    protected fun updateInstruction(index: Int, data: Instruction) {
        val newInstructionList =
            recipe.value?.instructions?.toMutableList()?.apply { set(index, data) }
                ?: error("failed to update instruction")
        updateRecipeInstructions(newInstructionList)
    }

    private fun updateRecipeIngredients(newIngredientList: List<Ingredient>) {
        val newRecipe = recipe.value?.copy(ingredients = newIngredientList)
            ?: error("failed to update ingredients")
        performUpdate(newRecipe)
    }

    private fun updateRecipeInstructions(newInstructionList: List<Instruction>) {
        val newRecipe = recipe.value?.copy(instructions = newInstructionList)
            ?: error("failed update instructions")
        performUpdate(newRecipe)
    }

    private fun performUpdate(data: RecipeData) {
        viewModelScope.launch(Dispatchers.IO) { db.updateRecipe(data) }
    }

    private suspend fun fetchRecipe(id: Long) = withContext(Dispatchers.IO) {
        db.fetchRecipe(id)
    }
}
