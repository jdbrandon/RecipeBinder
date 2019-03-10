package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.core.view.children
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.chip.Chip
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import kotlinx.android.synthetic.main.activity_view_recipe.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ViewRecipeActivity : RecipeActivity() {
    private var mode: Int = VIEW
    private lateinit var deferredDialog: Deferred<IngredientInputDialog>
    private val dialog: IngredientInputDialog by lazy { runBlocking { deferredDialog.await() } }
    override lateinit var ingredientAdapter: IngredientAdapter
    override lateinit var instructionAdapter: InstructionAdapter
    private var crossToCheckAnimation: AnimatedVectorDrawableCompat? = null
    private var checkToCrossAnimation: AnimatedVectorDrawableCompat? = null
    private val editButtonAnimatedVector: AnimatedVectorDrawableCompat?
        get() = AnimatedVectorDrawableCompat.create(this, R.drawable.edit_to_save)
    private val saveButtonAnimatedVector: AnimatedVectorDrawableCompat? by lazy {
        AnimatedVectorDrawableCompat.create(this,
                                            R.drawable.save_to_edit)
    }

    private fun editActionListener() {
        mode = EDIT
        showCorrectViews()
        action_button.setImageDrawable(editButtonAnimatedVector)
        (action_button.drawable as Animatable).start()
        action_button.setOnClickListener { saveActionListener() }
    }

    private fun saveActionListener() {
        mode = VIEW
        showCorrectViews()
        saveRecipeState()
        action_button.setImageDrawable(saveButtonAnimatedVector)
        (action_button.drawable as Animatable).start()
        action_button.setOnClickListener { editActionListener() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)
        setupButtonListeners()
        crossToCheckAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.cross_to_check)
        checkToCrossAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.check_to_cross)
    }

    override fun launchDeferredTasks() {
        deferredDialog = async { IngredientInputDialog(this@ViewRecipeActivity) }
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { dialog.addIngredientListener(ingredientAdapter) }
        add_instruction_button.setOnClickListener { addInstructionClick() }
        action_button.setOnClickListener { editActionListener() }
        registerForContextMenu(ingredients_list_view)
        registerForContextMenu(instructions_list_view)
    }

    private fun addInstructionClick() {
        action_button.visibility = View.GONE
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
        action_button.visibility = View.VISIBLE
        instruction_input_layout.visibility = View.GONE
        hideKeyboard()
        add_instruction_button.setImageDrawable(checkToCrossAnimation)
        (add_instruction_button.drawable as Animatable).start()
        add_instruction_button.setOnClickListener { addInstructionClick() }
    }

    override fun onPause() {
        super.onPause()
        if(mode == EDIT)
            saveRecipeState()
    }

    override fun populateViews(intent: Intent?) {
        intent?.apply {
            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.database_recipe_id))
                mode = extras!!.getInt(getString(R.string.view_mode_extra), VIEW)
                currentRecipe = recipePersistentData.fetchRecipe(id)
                val ingredients = currentRecipe.ingredientsJson
                val instructions = currentRecipe.instructionsJson
                val cookTime = if(currentRecipe.cookTime == 0) ""
                else currentRecipe.cookTime.toString()
                launch(Dispatchers.Main) {
                    ingredientAdapter = populateIngredients(ingredients)
                    instructionAdapter = populateInstructions(instructions)
                    setTagViews(currentRecipe.tags)
                    recipe_name_view.text = currentRecipe.name
                    recipe_name.setText(currentRecipe.name)
                    cook_time_view.text = cookTime
                    cook_time.setText(cookTime)
                    ingredients_list_view.adapter = ingredientAdapter
                    instructions_list_view.adapter = instructionAdapter
                    loading_panel.visibility = View.GONE
                    view_activity_content.visibility = View.VISIBLE
                    showCorrectViews()
                }
            }
        }
    }

    private fun showCorrectViews() {
        when(mode) {
            VIEW -> {
                recipe_name_view_layout.visibility = View.VISIBLE
                recipe_name_edit_layout.visibility = View.INVISIBLE
                add_ingredient_button.visibility = View.GONE
                add_instruction_button.visibility = View.GONE
            }
            EDIT -> {
                recipe_name_view_layout.visibility = View.GONE
                recipe_name_edit_layout.visibility = View.VISIBLE
                add_ingredient_button.visibility = View.VISIBLE
                add_instruction_button.visibility = View.VISIBLE
                (cook_type_chips.children + tags_group.children).forEach {
                    it.visibility = View.VISIBLE
                    (it as Chip).isCheckable = true
                }
            }
        }
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

    private fun saveRecipeState() {
        val name = recipe_name.text.toString()
        val time = cook_time.text.run { if(!isNullOrEmpty()) toString().toInt() else 0 }
        val tags = buildTagsList()
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
            launch(Dispatchers.Main) {
                recipe_name_view.text = name
                cook_time_view.text = if(time == 0) "" else time.toString()
                setTagViews(tags)
            }
        }
    }

    private fun buildTagsList(): MutableList<RecipeTag> {
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
        val editing = mode == EDIT
        (cook_type_chips.children + tags_group.children).forEach {
            (it as Chip).apply {
                isCheckable = editing
                visibility = if(editing) View.VISIBLE else View.GONE
            }
        }

        for(tag in tags) {
            getChipForTag(tag).apply {
                visibility = View.VISIBLE
                isChecked = true
            }
        }
    }

    private fun getChipForTag(tag: RecipeTag): Chip {
        return when(tag) {
            RecipeTag.INSTANT_POT -> chip_instant_pot
            RecipeTag.STOVE -> chip_stove
            RecipeTag.OVEN -> chip_oven
            RecipeTag.SOUS_VIDE -> chip_sous_vide
            RecipeTag.FAST -> chip_fast
            RecipeTag.EASY -> chip_easy
            RecipeTag.HEALTHY -> chip_healthy
            RecipeTag.VEGETARIAN -> chip_vegetarian
            RecipeTag.VEGAN -> chip_vegan
        }
    }

    companion object {
        const val VIEW: Int = 0
        const val EDIT: Int = 1
    }
}
