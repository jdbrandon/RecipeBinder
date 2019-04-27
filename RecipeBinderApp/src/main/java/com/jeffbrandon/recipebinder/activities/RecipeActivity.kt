package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.os.Bundle
import com.jeffbrandon.recipebinder.data.AppendableAdapter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class RecipeActivity : RecipeAppActivity() {
    companion object {
        const val BAD_ID: Long = -1
    }

    protected var id: Long = BAD_ID
    protected lateinit var currentRecipe: RecipeData

    abstract val ingredientAdapter: AppendableAdapter<Ingredient>
    abstract val instructionAdapter: AppendableAdapter<Instruction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchDeferredTasks()
    }

    override fun onResume() {
        super.onResume()
        populateViews(intent)
    }

    open fun launchDeferredTasks() {
        launch(Dispatchers.Default) {
            Timber.d("Default deferred task")
        }
    }

    protected fun populateIngredients(ingredients: List<Ingredient>?): IngredientAdapter {
        if(ingredients.isNullOrEmpty())
            return IngredientAdapter(this, mutableListOf())
        return IngredientAdapter(this, ingredients.toMutableList())
    }

    protected fun populateInstructions(instructions: List<Instruction>?): InstructionAdapter {
        if(instructions.isNullOrEmpty())
            return InstructionAdapter(this, mutableListOf())
        return InstructionAdapter(this, instructions.toMutableList())
    }

    abstract fun populateViews(intent: Intent?)
    abstract fun setTagViews(tags: List<RecipeTag>)
}