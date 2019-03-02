package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.AppendableAdapter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientEditAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionEditAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.content_edit_recipe.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class EditRecipeActivity : RecipeAppActivity() {

    companion object {
        private const val BAD_ID: Long = -1
    }

    private var id: Long = BAD_ID
    private lateinit var currentRecipe: RecipeData
    private lateinit var deferredDialog: Deferred<IngredientInputDialog>
    private val dialog: IngredientInputDialog by lazy { runBlocking { deferredDialog.await() } }

    private lateinit var ingredientEditAdapter: IngredientEditAdapter
    private lateinit var instructionEditAdapter: InstructionEditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deferredDialog = async { IngredientInputDialog(this@EditRecipeActivity) }
        intent?.apply {
            setContentView(R.layout.activity_edit_recipe)

            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.database_recipe_id))
                currentRecipe = recipePersistantData.fetchRecipe(id)
                setTags(currentRecipe.tags)
                val ingredients = currentRecipe.ingredientsJson
                val instructions = currentRecipe.instructionsJson
                val cookTime = if(currentRecipe.cookTime == 0) ""
                else currentRecipe.cookTime.toString()
                launch(Dispatchers.Main) {
                    ingredientEditAdapter = populateIngredients(ingredients)
                    instructionEditAdapter = populateInstructions(instructions)
                    recipe_name.setText(currentRecipe.name)
                    cook_time.setText(cookTime)
                    ingredients_list_view.adapter = ingredientEditAdapter
                    instructions_list_view.adapter = instructionEditAdapter
                }
            }
        }
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { dialog.addIngredientListener(ingredientEditAdapter) }
        add_instruction_button.setOnClickListener {
            instruction_input_layout.visibility = View.VISIBLE
            add_instruction_button.visibility = View.GONE
        }
        button_save_instruction.setOnClickListener {
            if(!instruction_input.text.isNullOrEmpty()) {
                instructionEditAdapter.add(Instruction(instruction_input.text.toString()))
                instruction_input.text!!.clear()
            }
            instruction_input_layout.visibility = View.GONE
            add_instruction_button.visibility = View.VISIBLE
        }
        button_save_recipe.setOnClickListener {
            saveRecipeState()
            //TODO: switch to view mode
        }
        registerForContextMenu(ingredients_list_view)
        registerForContextMenu(instructions_list_view)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when(item.itemId) {
            R.id.option_up -> {
                moveItemUp(info)
                true
            }
            R.id.option_down -> {
                moveItemDown(info)
                true
            }
            R.id.option_delete -> {
                deleteItem(info)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun moveItemUp(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.edit_ingredient_view -> ingredientEditAdapter.moveUp(menuInfo.position)
            R.id.edit_instruction_view -> instructionEditAdapter.moveUp(menuInfo.position)
        }
    }

    private fun moveItemDown(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.edit_ingredient_view -> ingredientEditAdapter.moveDown(menuInfo.position)
            R.id.edit_instruction_view -> instructionEditAdapter.moveDown(menuInfo.position)
        }
    }

    private fun deleteItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.edit_ingredient_view -> ingredientEditAdapter.delete(menuInfo.position)
            R.id.edit_instruction_view -> instructionEditAdapter.delete(menuInfo.position)
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
        val ingredients = ingredientEditAdapter.getData()
        val instructions = instructionEditAdapter.getData()
        launch(Dispatchers.IO) {
            val dbInput = RecipeData(id,
                                     name,
                                     time,
                                     tags,
                                     ingredients,
                                     instructions)
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

    private fun populateIngredients(ingredients: List<Ingredient>?): IngredientEditAdapter {
        if(ingredients.isNullOrEmpty()) {
            Timber.d("No ingredients")
            return IngredientEditAdapter(this, mutableListOf())
        }
        return IngredientEditAdapter(this, ingredients.toMutableList())
    }

    private fun populateInstructions(instructions: List<Instruction>?): InstructionEditAdapter {
        if(instructions.isNullOrEmpty()) {
            Timber.d("No instructions")
            return InstructionEditAdapter(this, mutableListOf())
        }
        return InstructionEditAdapter(this, instructions.toMutableList())
    }
}