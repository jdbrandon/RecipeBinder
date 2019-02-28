package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.View
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.content_edit_recipe.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class EditRecipeActivity : RecipeAppActivity() {

    companion object {
        private const val BAD_ID: Long = -1
    }

    private var id: Long = BAD_ID
    private lateinit var currentRecipe: RecipeData
    private lateinit var dialog: IngredientInputDialog

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var instructionAdapter: InstructionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.apply {
            setContentView(R.layout.activity_edit_recipe)

            id = extras!!.getLong(getString(R.string.database_recipe_id))
            launch(Dispatchers.IO) {
                currentRecipe = recipePersistantData.fetchRecipe(id)
                setTags(currentRecipe.tags)
                val ingredients = currentRecipe.ingredientsJson
                val instructions = currentRecipe.instructionsJson
                launch(Dispatchers.Main) {
                    ingredientAdapter = populateIngredients(ingredients)
                    instructionAdapter = populateInstructions(instructions)
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
        add_instruction_button.setOnClickListener {
            instruction_input_layout.visibility = View.VISIBLE
            add_instruction_button.visibility = View.GONE
        }
        button_save_instruction.setOnClickListener {
            if(!instruction_input.text.isNullOrEmpty()) {
                instructionAdapter.add(Instruction(instruction_input.text.toString()))
                instruction_input.text!!.clear()
            }
            instruction_input_layout.visibility = View.GONE
            add_instruction_button.visibility = View.VISIBLE
        }
        button_save_recipe.setOnClickListener {
            saveRecipeState()
            //TODO: switch to view mode
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveRecipeState()
    }

    private fun saveRecipeState() {
        val name = recipe_name.text.toString()
        val time = cook_time.text.run { if(!isNullOrEmpty()) toString().toInt() else 0 }
        val tags = getTags()
        val ingredients = ingredientAdapter.getData()
        val instructions = instructionAdapter.getData()
        val dbInput = RecipeData(id,
                                 name,
                                 time,
                                 tags,
                                 ingredients,
                                 instructions)
        launch(Dispatchers.IO) {
            if(dbInput.id != BAD_ID) {
                recipePersistantData.updateRecipe(dbInput)
            }
        }
    }

    private fun getTags(): MutableList<RecipeTag> {
        val res = when(cook_type_chips.checkedChipId) {
            R.id.chip_instant_pot -> mutableListOf(RecipeTag.INSTANT_POT)
            R.id.chip_stove -> mutableListOf(RecipeTag.STOVE)
            R.id.chip_oven -> mutableListOf(RecipeTag.OVEN)
            R.id.chip_sous_vide -> mutableListOf(RecipeTag.SOUS_VIDE)
            else -> mutableListOf()
        }
        res.apply {
            if(chip_fast.isChecked)
                add(RecipeTag.FAST)
            if(chip_easy.isChecked)
                add(RecipeTag.EASY)
            if(chip_healthy.isChecked)
                add(RecipeTag.HEALTHY)
            if(chip_vegetarian.isChecked)
                add(RecipeTag.VEGETARIAN)
            if(chip_vegan.isChecked)
                add(RecipeTag.VEGAN)
        }
        return res
    }

    private fun setTags(tags: List<RecipeTag>) {
        for(tag in tags) {
            when(tag) {
                RecipeTag.INSTANT_POT -> chip_instant_pot.isChecked = true
                RecipeTag.STOVE -> chip_stove.isChecked = true
                RecipeTag.OVEN -> chip_oven.isChecked = true
                RecipeTag.SOUS_VIDE -> chip_sous_vide.isChecked = true
                RecipeTag.FAST -> chip_fast.isChecked = true
                RecipeTag.EASY -> chip_easy.isChecked = true
                RecipeTag.HEALTHY -> chip_healthy.isChecked = true
                RecipeTag.VEGETARIAN -> chip_vegetarian.isChecked = true
                RecipeTag.VEGAN -> chip_vegan.isChecked = true
            }
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
            Timber.d(instruction.text)
        return InstructionAdapter(this, instructions.toMutableList())
    }
}