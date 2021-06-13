package com.jeffbrandon.recipebinder.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.dagger.IDispatchers
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.room.RecipeDataSource
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@SuppressWarnings("TooManyFunctions")
@HiltViewModel
open class RecipeViewModel @Inject constructor(
    dataSource: Lazy<RecipeDataSource>,
    state: SavedStateHandle,
    @ApplicationContext context: Context,
    lazyDispatchers: Lazy<IDispatchers>,
) : ViewModel() {
    private val id: Long = state[context.getString(R.string.extra_recipe_id)]
        ?: error("Did you forget to provide recipeId in the intent extras or fragment args bundle")
    private val db = dataSource.get()
    private val recipe = db.fetchRecipe(id).asLiveData()
    protected val dispatchers: IDispatchers by lazy { lazyDispatchers.get() }

    fun getRecipe(): LiveData<RecipeData> = recipe

    fun getIngredients() = recipe.map { it.ingredients }

    fun getInstructions() = recipe.map { it.instructions }

    protected fun getIngredientIndex(data: Ingredient): Int? =
        recipe.value?.ingredients?.indexOf(data).takeIf { it != -1 }

    protected fun getInstructionIndex(data: Instruction): Int? =
        recipe.value?.instructions?.indexOf(data).takeIf { it != -1 }

    protected suspend fun appendIngredient(data: Ingredient) = withContext(dispatchers.default) {
        val newIngredientList =
            recipe.value?.ingredients?.toMutableList()?.apply { add(data) }
                ?: error("failed to add ingredient")
        updateRecipeIngredients(newIngredientList)
    }

    protected suspend fun appendInstruction(data: Instruction) = withContext(dispatchers.default) {
        val newInstructionList =
            recipe.value?.instructions?.toMutableList()?.apply { add(data) }
                ?: error("failed to add ingredient")
        updateRecipeInstructions(newInstructionList)
    }

    protected suspend fun updateIngredient(index: Int, data: Ingredient) =
        withContext(dispatchers.default) {
            val newIngredientList =
                recipe.value?.ingredients?.toMutableList()?.apply { set(index, data) }
                    ?: error("failed to update ingredient")
            updateRecipeIngredients(newIngredientList)
        }

    protected suspend fun updateInstruction(index: Int, data: Instruction) =
        withContext(dispatchers.default) {
            val newInstructionList =
                recipe.value?.instructions?.toMutableList()?.apply { set(index, data) }
                    ?: error("failed to update instruction")
            updateRecipeInstructions(newInstructionList)
        }

    protected suspend fun updateRecipeMetadata(
        recipeName: String,
        cookTime: Int,
        servings: Int,
        tags: Set<RecipeTag>,
    ) = withContext(dispatchers.default) {
        val newRecipe = recipe.value?.copy(
            name = recipeName,
            cookTime = cookTime,
            servings = servings,
            tags = tags
        )
            ?: error("failed to update metadata")
        db.updateRecipe(newRecipe)
    }

    protected suspend fun deleteIngredient(index: Int) = withContext(dispatchers.default) {
        val newIngredientList =
            recipe.value?.ingredients?.toMutableList()?.apply { removeAt(index) }
                ?: error("failed to delete ingredient")
        updateRecipeIngredients(newIngredientList)
    }

    protected suspend fun deleteInstruction(index: Int) = withContext(dispatchers.default) {
        val newInstructionList =
            recipe.value?.instructions?.toMutableList()?.apply { removeAt(index) }
                ?: error("failed to delete ingredient")
        updateRecipeInstructions(newInstructionList)
    }

    protected suspend fun moveTo(target: Ingredient, data: Ingredient) =
        withContext(dispatchers.default) {
            val newIngredients = recipe.value?.ingredients?.toMutableList()?.apply {
                if (contains(target)) {
                    remove(data)
                    add(indexOf(target), data)
                } else {
                    Timber.w("Attempted to move to an element not contained in ingredients")
                }
            } ?: error("Failed to move ingredient")
            updateRecipeIngredients(newIngredients)
        }

    protected suspend fun moveTo(target: Instruction, data: Instruction) =
        withContext(dispatchers.default) {
            val newInstructions = recipe.value?.instructions?.toMutableList()?.apply {
                if (contains(target)) {
                    remove(data)
                    add(indexOf(target), data)
                } else {
                    Timber.w("Attempted to move to an element not contained in instructions")
                }
            } ?: error("Failed to move instruction")
            updateRecipeInstructions(newInstructions)
        }

    private suspend fun updateRecipeIngredients(newIngredientList: List<Ingredient>) =
        withContext(dispatchers.io) {
            val newRecipe = recipe.value?.copy(ingredients = newIngredientList)
                ?: error("failed to update ingredients")
            db.updateRecipe(newRecipe)
        }

    private suspend fun updateRecipeInstructions(newInstructionList: List<Instruction>) =
        withContext(dispatchers.io) {
            val newRecipe = recipe.value?.copy(instructions = newInstructionList)
                ?: error("failed update instructions")
            db.updateRecipe(newRecipe)
        }
}
