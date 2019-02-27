package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.content_edit_recipe.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class EditRecipeActivity : RecipeAppActivity() {

    private lateinit var currentRecipe: RecipeData
    private lateinit var dialog: IngredientInputDialog
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val ingredientJsonAdapter =
        moshi.adapter<List<Ingredient>>(Types.newParameterizedType(List::class.java, Ingredient::class.java))
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var instructionAdapter: InstructionAdapter
    private val instructionJsonAdapter =
        moshi.adapter<List<Instruction>>(Types.newParameterizedType(List::class.java, Instruction::class.java))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.apply {
            val action = extras!!.getString(getString(R.string.activity_edit_recipe_intent_mode),
                                            getString(R.string.view_mode_view))

            if(action == getString(R.string.view_mode_edit))
                setContentView(R.layout.activity_edit_recipe)
            else
                setContentView(/*TODO*/0) //View action

            val id = extras!!.getLong(getString(R.string.database_recipe_id))
            launch(Dispatchers.IO) {
                currentRecipe = recipePersistantData.fetchRecipe(id)
                launch(Dispatchers.Main) {
                    ingredientAdapter =
                        populateIngredients(ingredientJsonAdapter.fromJson(currentRecipe.ingredientsJson))
                    instructionAdapter =
                        populateInstructions(instructionJsonAdapter.fromJson(currentRecipe.instructionsJson))
                    recipe_name.setText(currentRecipe.name)
                    cook_time.setText(if(currentRecipe.cookTime == 0) ""
                                      else currentRecipe.cookTime.toString())
                    ingredients_list_view.adapter = ingredientAdapter
                    instructions_list_view.adapter = instructionAdapter
                }
            }
        }
        dialog = IngredientInputDialog(this)
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { dialog.addIngredientListener(ingredientAdapter) }
        add_instruction_button.setOnClickListener { v ->
            //TODO: implement
        }
    }

    private fun populateIngredients(ingredients: List<Ingredient>?): IngredientAdapter {
        if(ingredients.isNullOrEmpty()) {
            Timber.d("No ingredients")
            return IngredientAdapter(this, mutableListOf())
        }
        for(ingredient in ingredients)
            Timber.d(ingredient.name)
        return IngredientAdapter(this, ingredients.toMutableList())
    }

    private fun populateInstructions(instructions: List<Instruction>?): InstructionAdapter {
        if(instructions.isNullOrEmpty()) {
            Timber.d("No instructions")
            return InstructionAdapter(this, mutableListOf())
        }
        for(instruction in instructions)
            Timber.d(instruction.get())
        return InstructionAdapter(this, instructions.toMutableList())
    }
}