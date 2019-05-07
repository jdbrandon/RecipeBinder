package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.children
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import com.jeffbrandon.recipebinder.widgets.UpdateInstructionDialog
import kotlinx.android.synthetic.main.activity_view_recipe.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ViewRecipeActivity : RecipeActivity() {
    private var mode: Int = VIEW
    private lateinit var deferredIngredientDialog: Deferred<IngredientInputDialog>
    private val ingredientDialog: IngredientInputDialog by lazy { runBlocking { deferredIngredientDialog.await() } }
    private lateinit var deferredInstructionDialog: Deferred<UpdateInstructionDialog>
    private val instructionDialog: UpdateInstructionDialog by lazy { runBlocking { deferredInstructionDialog.await() } }
    override lateinit var ingredientAdapter: IngredientAdapter
    override lateinit var instructionAdapter: InstructionAdapter
    private var crossToCheckAnimation: AnimatedVectorDrawableCompat? = null
    private var checkToCrossAnimation: AnimatedVectorDrawableCompat? = null
    private val editButtonAnimatedVector: AnimatedVectorDrawableCompat? by lazy {
        AnimatedVectorDrawableCompat.create(this, R.drawable.edit_to_save)
    }
    private val saveButtonAnimatedVector: AnimatedVectorDrawableCompat? by lazy {
        AnimatedVectorDrawableCompat.create(this, R.drawable.save_to_edit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)
        setMode(intent, savedInstanceState)
        setupButtonListeners()
        crossToCheckAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.cross_to_check)
        checkToCrossAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.check_to_cross)
    }

    private fun setMode(intent: Intent?, savedInstanceState: Bundle?) {
        intent?.run {
            mode = getIntExtra(getString(R.string.view_mode_extra), mode)
        }
        savedInstanceState?.run {
            mode = getInt(getString(R.string.view_mode_extra), mode)
        }
    }

    override fun launchDeferredTasks() {
        deferredIngredientDialog = async { IngredientInputDialog(this@ViewRecipeActivity) }
        deferredInstructionDialog = async { UpdateInstructionDialog(this@ViewRecipeActivity) }
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { ingredientDialog.addIngredientListener(ingredientAdapter) }
        add_instruction_button.setOnClickListener { addInstructionClick() }
        if(mode.isEditing())
            action_button.animateWithCallback(editButtonAnimatedVector) { saveActionListener() }
        else action_button.setOnClickListener { editActionListener() }

        registerForContextMenu(ingredients_list_view)
        registerForContextMenu(instructions_list_view)
    }

    private fun addInstructionClick() {
        action_button.visibility = View.GONE
        instruction_input_layout.visibility = View.VISIBLE
        add_instruction_button.animateWithCallback(crossToCheckAnimation!!) { saveInstructionClick() }
    }

    private fun editActionListener() {
        mode = EDIT_TAGS
        showCorrectViews()
        action_button.animateWithCallback(editButtonAnimatedVector) { saveActionListener() }
    }

    private fun saveActionListener() {
        mode = VIEW
        showCorrectViews()
        saveRecipeState()
        Toast.makeText(this, getString(R.string.toast_save), Toast.LENGTH_SHORT).show()
        action_button.animateWithCallback(saveButtonAnimatedVector) { editActionListener() }
    }

    private fun saveInstructionClick() {
        if(!instruction_input.text.isNullOrEmpty()) {
            instructionAdapter.add(Instruction(instruction_input.text.toString()))
            instruction_input.text!!.clear()
        }
        action_button.visibility = View.VISIBLE
        instruction_input_layout.visibility = View.GONE
        hideKeyboard()
        add_instruction_button.animateWithCallback(checkToCrossAnimation) { addInstructionClick() }
    }

    private fun FloatingActionButton.animateWithCallback(animatedDrawable: AnimatedVectorDrawableCompat?,
                                                         callback: () -> Unit) {
        setImageDrawable(animatedDrawable)
        (drawable as Animatable).start()
        setOnClickListener { callback() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(getString(R.string.view_mode_extra), mode)
        super.onSaveInstanceState(outState)
        if(mode.isEditing())
            saveRecipeState()
    }

    /**
     * Called from [onResume]
     */
    override fun populateViews(intent: Intent?) {
        if(intent == null) {
            Timber.w("No recipe ID passed through intent: not sure how to populate views")
            Timber.i("exiting")
            finish()
            return // Ensures kotlin knows intent is not null
        }
        intent.run {
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
        setCommonViewVisibility(mode.isEditing())
        when(mode) {
            VIEW -> {
                ingredients_list_view.visibility = View.VISIBLE
                instructions_list_view.visibility = View.VISIBLE
                add_ingredient_button.visibility = View.GONE
                add_instruction_button.visibility = View.GONE
            }
            EDIT_TAGS -> {
                setTagViews(currentRecipe.tags)
                setTagsVisibility(View.VISIBLE)
                setIngredientsVisibility(View.GONE)
                setInstructionsVisibility(View.GONE)
            }
            EDIT_INGREDIENTS -> {
                setTagsVisibility(View.GONE)
                setIngredientsVisibility(View.VISIBLE)
                setInstructionsVisibility(View.GONE)
            }
            EDIT_INSTRUCTIONS -> {
                setTagsVisibility(View.GONE)
                setIngredientsVisibility(View.GONE)
                setInstructionsVisibility(View.VISIBLE)
            }
        }
    }

    private fun setTagsVisibility(visible: Int) {
        tags_edit_chip_layout.visibility = visible
    }

    private fun setIngredientsVisibility(visible: Int) {
        ingredients_list_view.visibility = visible
        add_ingredient_button.visibility = visible
    }

    private fun setInstructionsVisibility(visible: Int) {
        instructions_list_view.visibility = visible
        add_instruction_button.visibility = visible
    }

    private fun setCommonViewVisibility(editing: Boolean) {
        title_text_view.text = if(editing) getString(R.string.recipe_editor) else getString(R.string.recipe_details)
        recipe_name_view_layout.visibility = if(editing) View.INVISIBLE else View.VISIBLE
        recipe_name_edit_layout.visibility = if(editing) View.VISIBLE else View.INVISIBLE
        tags_view_chip_group.visibility = if(editing) View.GONE else View.VISIBLE
        tags_edit_chip_layout.visibility = if(editing) View.VISIBLE else View.GONE
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
            R.id.option_update -> {
                updateItem(info)
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

    private fun updateItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientDialog.updateIngredientListener(ingredientAdapter, menuInfo.position)
            R.id.instruction_view -> instructionDialog.updateInstruction(instructionAdapter, menuInfo.position)
        }
    }

    private fun deleteItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when(menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.delete(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.delete(menuInfo.position)
        }
    }

    private fun saveRecipeState() {
        launch(Dispatchers.IO) {
            val name = recipe_name.text.toString()
            val time = cook_time.text.run { if(!isNullOrEmpty()) toString().toInt() else 0 }
            val tags = buildTagsList()
            val ingredients = ingredientAdapter.getData()
            val instructions = instructionAdapter.getData()
            val dbInput = RecipeData(id,
                                     name,
                                     time,
                                     tags,
                                     ingredients,
                                     instructions)
            if(dbInput.id != BAD_ID) {
                recipePersistentData.updateRecipe(dbInput)
                currentRecipe = dbInput
            } else Timber.w("Tried to update recipe with uninitialized id")
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
        when(dish_type_chips.checkedChipId) {
            R.id.chip_entree -> res.add(RecipeTag.ENTREE)
            R.id.chip_side -> res.add(RecipeTag.SIDE)
            R.id.chip_soup -> res.add(RecipeTag.SOUP)
            R.id.chip_dessert -> res.add(RecipeTag.DESSERT)
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

    private fun setTagViews(tags: List<RecipeTag>) {
        tags_view_chip_group.removeAllViews()
        for(tag in tags)
            tags_view_chip_group.addView(tag.toChipView(this))
        (cook_type_chips.children + dish_type_chips.children + tags_group.children).forEach {
            (it as Chip).apply { isChecked = getTagForChip(it) in tags }
        }
    }

    private fun getTagForChip(chip: Chip): RecipeTag {
        return when(chip.id) {
            chip_instant_pot.id -> RecipeTag.INSTANT_POT
            chip_stove.id -> RecipeTag.STOVE
            chip_oven.id -> RecipeTag.OVEN
            chip_sous_vide.id -> RecipeTag.SOUS_VIDE
            chip_fast.id -> RecipeTag.FAST
            chip_easy.id -> RecipeTag.EASY
            chip_healthy.id -> RecipeTag.HEALTHY
            chip_vegetarian.id -> RecipeTag.VEGETARIAN
            chip_vegan.id -> RecipeTag.VEGAN
            chip_entree.id -> RecipeTag.ENTREE
            chip_side.id -> RecipeTag.SIDE
            chip_soup.id -> RecipeTag.SOUP
            chip_dessert.id -> RecipeTag.DESSERT
            else -> throw IllegalArgumentException("Unknown chip argument")
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun tagsClick(v: View) {
        switchMode(EDIT_TAGS)
    }

    @Suppress("UNUSED_PARAMETER")
    fun ingredientsClick(v: View) {
        switchMode(EDIT_INGREDIENTS)
    }

    @Suppress("UNUSED_PARAMETER")
    fun instructionsClick(v: View) {
        switchMode(EDIT_INSTRUCTIONS)
    }

    private fun switchMode(newMode: Int) {
        if(mode.isEditing() && mode != newMode) {
            mode = newMode
            showCorrectViews()
        }
    }

    companion object {
        const val VIEW = 0
        const val EDIT_TAGS = 1
        const val EDIT_INGREDIENTS = 2
        const val EDIT_INSTRUCTIONS = 3
        private fun Int.isEditing(): Boolean {
            return when(this) {
                EDIT_TAGS,
                EDIT_INGREDIENTS,
                EDIT_INSTRUCTIONS -> true
                else -> false
            }
        }
    }
}
