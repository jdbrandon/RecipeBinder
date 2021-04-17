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
import com.jeffbrandon.recipebinder.databinding.ActivityViewRecipeBinding
import com.jeffbrandon.recipebinder.databinding.EditTagsBinding
import com.jeffbrandon.recipebinder.enums.RecipeTag
import com.jeffbrandon.recipebinder.room.RecipeData
import com.jeffbrandon.recipebinder.widgets.IngredientInputDialog
import com.jeffbrandon.recipebinder.widgets.UpdateInstructionDialog
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ViewRecipeActivity : RecipeActivity() {
    private var mode: Int = VIEW
    @Inject lateinit var deferredIngredientDialog: Lazy<IngredientInputDialog>
    private val ingredientDialog: IngredientInputDialog
        get() = deferredIngredientDialog.get()
    @Inject lateinit var deferredInstructionDialog: Lazy<UpdateInstructionDialog>
    private val instructionDialog: UpdateInstructionDialog
        get() = deferredInstructionDialog.get()
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
    private lateinit var binding: ActivityViewRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMode(intent, savedInstanceState)
        binding.setupButtonListeners()
        crossToCheckAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.cross_to_check)
        checkToCrossAnimation = AnimatedVectorDrawableCompat.create(this, R.drawable.check_to_cross)
    }

    private fun setMode(intent: Intent?, savedInstanceState: Bundle?) {
        intent?.run {
            mode = getIntExtra(getString(R.string.extra_view_mode), mode)
        }
        savedInstanceState?.run {
            mode = getInt(getString(R.string.extra_view_mode), mode)
        }
    }

    private fun ActivityViewRecipeBinding.setupButtonListeners() {
        addIngredientButton.setOnClickListener {
            ingredientDialog.add(ingredientAdapter)
        }
        addInstructionButton.setOnClickListener { addInstructionClick() }
        if (mode.isEditing()) actionButton.animateWithCallback(editButtonAnimatedVector) { saveActionListener() }
        else actionButton.setOnClickListener { editActionListener() }

        registerForContextMenu(ingredientsListView)
        registerForContextMenu(instructionsListView)
        registerForContextMenu(ingredientsListViewLarge)
        registerForContextMenu(instructionsListViewLarge)
    }

    private fun ActivityViewRecipeBinding.addInstructionClick() {
        actionButton.visibility = View.GONE
        instructionInputLayout.visibility = View.VISIBLE
        addInstructionButton.animateWithCallback(crossToCheckAnimation!!) { saveInstructionClick() }
    }

    private fun ActivityViewRecipeBinding.editActionListener() {
        mode = EDIT_TAGS
        showCorrectViews()
        actionButton.animateWithCallback(editButtonAnimatedVector) { saveActionListener() }
    }

    private fun ActivityViewRecipeBinding.saveActionListener() {
        mode = VIEW
        showCorrectViews()
        saveRecipeState()
        Toast.makeText(this@ViewRecipeActivity, getString(R.string.toast_save), Toast.LENGTH_SHORT)
            .show()
        actionButton.animateWithCallback(saveButtonAnimatedVector) { editActionListener() }
    }

    private fun ActivityViewRecipeBinding.saveInstructionClick() {
        if (!instructionInput.text.isNullOrEmpty()) {
            instructionAdapter.add(Instruction(instructionInput.text.toString()
                                                   .capitalize(Locale.getDefault())))
            instructionInput.text!!.clear()
        }
        actionButton.visibility = View.VISIBLE
        instructionInputLayout.visibility = View.GONE
        hideKeyboard()
        addInstructionButton.animateWithCallback(checkToCrossAnimation) { addInstructionClick() }
    }

    private fun FloatingActionButton.animateWithCallback(
        animatedDrawable: AnimatedVectorDrawableCompat?,
        callback: () -> Unit,
    ) {
        setImageDrawable(animatedDrawable)
        (drawable as Animatable).start()
        setOnClickListener { callback() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(getString(R.string.extra_view_mode), mode)
        super.onSaveInstanceState(outState)
        if (mode.isEditing()) binding.saveRecipeState()
    }

    /**
     * Called from [onResume]
     */
    override fun populateViews(intent: Intent?) {
        if (intent == null) {
            Timber.w("No recipe ID passed through intent: not sure how to populate views")
            Timber.i("exiting")
            finish()
            return // Ensures kotlin knows intent is not null
        }
        intent.run {
            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.extra_recipe_id))
                mode = extras!!.getInt(getString(R.string.extra_view_mode), mode)
                // currentRecipe = recipePersistentData.fetchRecipe(id)
                val ingredients = currentRecipe.ingredients
                val instructions = currentRecipe.instructions
                val time = if (currentRecipe.cookTime == 0) ""
                else currentRecipe.cookTime.toString()
                launch(Dispatchers.Main) {
                    with(binding) {
                        ingredientAdapter = populateIngredients(ingredients)
                        instructionAdapter = populateInstructions(instructions)
                        tagsLayout.setTagViews(currentRecipe.tags)
                        titleTextView.text = currentRecipe.name
                        recipeName.setText(currentRecipe.name)
                        cookTimeView.text = time
                        cookTime.setText(time)
                        ingredientsListView.adapter = ingredientAdapter
                        instructionsListView.adapter = instructionAdapter
                        ingredientsListViewLarge.adapter = ingredientAdapter
                        instructionsListViewLarge.adapter = instructionAdapter
                        loadingPanel.visibility = View.GONE
                        viewActivityContent.visibility = View.VISIBLE
                        showCorrectViews()
                    }
                }
            }
        }
    }

    private fun ActivityViewRecipeBinding.showCorrectViews() {
        setCommonViewVisibility(mode.isEditing())
        when (mode) {
            VIEW -> {
                addIngredientButton.visibility = View.GONE
                addInstructionButton.visibility = View.GONE
            }
            EDIT_TAGS -> {
                tagsLayout.setTagViews(currentRecipe.tags)
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

    private fun ActivityViewRecipeBinding.setTagsVisibility(visible: Int) {
        tagsLayout.tagsLayout.visibility = visible
    }

    private fun ActivityViewRecipeBinding.setIngredientsVisibility(visible: Int) {
        ingredientsListViewLarge.visibility = visible
        addIngredientButton.visibility = visible
    }

    private fun ActivityViewRecipeBinding.setInstructionsVisibility(visible: Int) {
        instructionsListViewLarge.visibility = visible
        addInstructionButton.visibility = visible
    }

    private fun ActivityViewRecipeBinding.setCommonViewVisibility(editing: Boolean) {
        titleTextView.text = if (editing) "Edit ${currentRecipe.name}" else currentRecipe.name
        recipeNameViewLayout.visibility = if (!editing) View.VISIBLE else View.INVISIBLE
        recipeNameEditLayout.visibility = if (editing) View.VISIBLE else View.INVISIBLE
        tagsSectionTitle.visibility = if (editing) View.VISIBLE else View.GONE
        tagsLayout.tagsLayout.visibility = if (editing) View.VISIBLE else View.GONE
        ingredientsListView.visibility = if (!editing) View.VISIBLE else View.GONE
        instructionsListView.visibility = if (!editing) View.VISIBLE else View.GONE
        ingredientsListViewLarge.visibility = if (editing) View.VISIBLE else View.GONE
        instructionsListViewLarge.visibility = if (editing) View.VISIBLE else View.GONE
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
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
        when (menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.moveUp(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.moveUp(menuInfo.position)
        }
    }

    private fun moveItemDown(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when (menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.moveDown(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.moveDown(menuInfo.position)
        }
    }

    private fun updateItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when (menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientDialog.update(ingredientAdapter, menuInfo.position)
            R.id.instruction_view -> instructionDialog.show(this,
                                                            instructionAdapter,
                                                            menuInfo.position)
        }
    }

    private fun deleteItem(menuInfo: AdapterView.AdapterContextMenuInfo) {
        when (menuInfo.targetView.id) {
            R.id.ingredient_view -> ingredientAdapter.delete(menuInfo.position)
            R.id.instruction_view -> instructionAdapter.delete(menuInfo.position)
        }
    }

    private fun ActivityViewRecipeBinding.saveRecipeState() {
        launch(Dispatchers.IO) {
            val name = recipeName.text.toString()
            val time = cookTime.text.run { if (!isNullOrEmpty()) toString().toInt() else 0 }
            val tags = tagsLayout.buildTagsList()
            val ingredients = ingredientAdapter.getData()
            val instructions = instructionAdapter.getData()
            val dbInput = RecipeData(id,
                                     name.capitalize(Locale.getDefault()),
                                     time,
                                     tags,
                                     ingredients,
                                     instructions)
            if (dbInput.id != BAD_ID) {
                recipePersistentData.updateRecipe(dbInput)
                currentRecipe = dbInput
            } else Timber.w("Tried to update recipe with uninitialized id")
            launch(Dispatchers.Main) {
                titleTextView.text = name
                cookTimeView.text = if (time == 0) "" else time.toString()
                tagsLayout.setTagViews(tags)
            }
        }
    }

    private fun EditTagsBinding.buildTagsList(): MutableList<RecipeTag> {
        val res = when (cookTypeChips.checkedChipId) {
            R.id.chip_instant_pot -> mutableListOf(RecipeTag.INSTANT_POT)
            R.id.chip_stove -> mutableListOf(RecipeTag.STOVE)
            R.id.chip_oven -> mutableListOf(RecipeTag.OVEN)
            R.id.chip_sous_vide -> mutableListOf(RecipeTag.SOUS_VIDE)
            else -> mutableListOf()
        }
        when (dishTypeChips.checkedChipId) {
            R.id.chip_entree -> res.add(RecipeTag.ENTREE)
            R.id.chip_side -> res.add(RecipeTag.SIDE)
            R.id.chip_soup -> res.add(RecipeTag.SOUP)
            R.id.chip_dessert -> res.add(RecipeTag.DESSERT)
        }
        res.apply {
            if (chipFast.isChecked) add(RecipeTag.FAST)
            if (chipEasy.isChecked) add(RecipeTag.EASY)
            if (chipHealthy.isChecked) add(RecipeTag.HEALTHY)
            if (chipVegetarian.isChecked) add(RecipeTag.VEGETARIAN)
            if (chipVegan.isChecked) add(RecipeTag.VEGAN)
        }
        return res
    }

    private fun EditTagsBinding.setTagViews(tags: List<RecipeTag>) {
        (cookTypeChips.children + dishTypeChips.children + tagsGroup.children).forEach {
            (it as Chip).apply { isChecked = getTagForChip(it) in tags }
        }
    }

    private fun EditTagsBinding.getTagForChip(chip: Chip): RecipeTag {
        return when (chip.id) {
            chipInstantPot.id -> RecipeTag.INSTANT_POT
            chipStove.id -> RecipeTag.STOVE
            chipOven.id -> RecipeTag.OVEN
            chipSousVide.id -> RecipeTag.SOUS_VIDE
            chipFast.id -> RecipeTag.FAST
            chipEasy.id -> RecipeTag.EASY
            chipHealthy.id -> RecipeTag.HEALTHY
            chipVegetarian.id -> RecipeTag.VEGETARIAN
            chipVegan.id -> RecipeTag.VEGAN
            chipEntree.id -> RecipeTag.ENTREE
            chipSide.id -> RecipeTag.SIDE
            chipSoup.id -> RecipeTag.SOUP
            chipDessert.id -> RecipeTag.DESSERT
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
        if (mode.isEditing() && mode != newMode) {
            mode = newMode
            binding.showCorrectViews()
        }
    }

    companion object {
        const val VIEW = 0
        const val EDIT_TAGS = 1
        const val EDIT_INGREDIENTS = 2
        const val EDIT_INSTRUCTIONS = 3
        private fun Int.isEditing(): Boolean {
            return when (this) {
                EDIT_TAGS,
                EDIT_INGREDIENTS,
                EDIT_INSTRUCTIONS,
                -> true
                else -> false
            }
        }
    }
}
