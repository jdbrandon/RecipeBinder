package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class EditRecipeActivity : RecipeActivity() {

    private lateinit var deferredDialog: Deferred<IngredientInputDialog>
    private val dialog: IngredientInputDialog by lazy { runBlocking { deferredDialog.await() } }

    override lateinit var ingredientAdapter: IngredientAdapter
    override lateinit var instructionAdapter: InstructionAdapter
    private var crossToCheckAnimation: AnimatedVectorDrawableCompat? = null
    private var checkToCrossAnimation: AnimatedVectorDrawableCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setupButtonListeners()
        crossToCheckAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.cross_to_check)
        checkToCrossAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.check_to_cross)
    }

    override fun launchDeferredTasks() {
        deferredDialog = async { IngredientInputDialog(this@EditRecipeActivity) }
    }

    override fun populateViews(intent: Intent?) {
        intent?.apply {

            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.database_recipe_id))
                currentRecipe = recipePersistentData.fetchRecipe(id)
                val ingredients = currentRecipe.ingredientsJson
                val instructions = currentRecipe.instructionsJson
                val cookTime = if(currentRecipe.cookTime == 0) ""
                else currentRecipe.cookTime.toString()
                launch(Dispatchers.Main) {
                    ingredientAdapter = populateIngredients(ingredients)
                    instructionAdapter = populateInstructions(instructions)
                    setTagViews(currentRecipe.tags)
                    toolbar.title = currentRecipe.name
                    recipe_name.setText(currentRecipe.name)
                    cook_time.setText(cookTime)
                    ingredients_list_view.adapter = ingredientAdapter
                    instructions_list_view.adapter = instructionAdapter
                    loading_panel.visibility = View.GONE
                    edit_activity_content.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addInstructionClick() {
        button_save_recipe.visibility = View.GONE
        instruction_input_layout.visibility = View.VISIBLE
        add_instruction_button.setImageDrawable(crossToCheckAnimation)
        (add_instruction_button.drawable as Animatable).start()
        add_instruction_button.setOnClickListener { saveInstructionClick() }
    }

    private fun saveInstructionClick() {
        if(!instruction_input.text.isNullOrEmpty()) {
            instructionAdapter.add(Instruction(instruction_input.text.toString()))
            instruction_input.text!!.clear()
        }
        button_save_recipe.visibility = View.VISIBLE
        instruction_input_layout.visibility = View.GONE
        hideKeyboard()
        add_instruction_button.setImageDrawable(checkToCrossAnimation)
        (add_instruction_button.drawable as Animatable).start()
        add_instruction_button.setOnClickListener { addInstructionClick() }
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { dialog.addIngredientListener(ingredientAdapter) }
        add_instruction_button.setOnClickListener { addInstructionClick() }
        button_save_recipe.setOnClickListener {
            saveRecipeState()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAfterTransition()
            else finish()
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
            R.id.ingredient_view -> ingredientAdapter.moveUp(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.moveUp(menuInfo.position)
        }
    }

    private fun moveItemDown(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.moveDown(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.moveDown(menuInfo.position)
        }
    }

    private fun deleteItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.delete(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.delete(menuInfo.position)
        }
    }

    override fun onPause() {
        super.onPause()
        saveRecipeState()
    }

    private fun saveRecipeState() {
        val name = recipe_name.text.toString()
        val time = cook_time.text.run { if(!isNullOrEmpty()) toString().toInt() else 0 }
        val tags = getTags()
        val ingredients = ingredientAdapter.getData()
        val instructions = instructionAdapter.getData()
        launch(Dispatchers.IO) {
            val dbInput = RecipeData(id,
                                     name,
                                     time,
                                     tags,
                                     ingredients,
                                     instructions)
            if(dbInput.id != RecipeActivity.BAD_ID) {
                recipePersistentData.updateRecipe(dbInput)
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

    override fun setTagViews(tags: List<RecipeTag>) {
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

    override fun populateIngredients(ingredients: List<Ingredient>?): IngredientAdapter {
        if(ingredients.isNullOrEmpty()) {
            Timber.d("No ingredients")
            return IngredientAdapter(this, mutableListOf())
        }
        return IngredientAdapter(this, ingredients.toMutableList())
    }

    override fun populateInstructions(instructions: List<Instruction>?): InstructionAdapter {
        if(instructions.isNullOrEmpty()) {
            Timber.d("No instructions")
            return InstructionAdapter(this, mutableListOf(), R.layout.instruction_edit_item)
        }
        return InstructionAdapter(this, instructions.toMutableList(), R.layout.instruction_edit_item)
    }
}