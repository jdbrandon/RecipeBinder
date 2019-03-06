package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.AppendableAdapter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.RecipeTag
import kotlinx.android.synthetic.main.activity_view_recipe.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewRecipeActivity : RecipeActivity() {
    override lateinit var ingredientAdapter: AppendableAdapter<Ingredient>
    override lateinit var instructionAdapter: AppendableAdapter<Instruction>
    private var editButtonAnimatedVector: AnimatedVectorDrawableCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)
        edit_recipe_button.setOnClickListener {
            launch(Dispatchers.Default) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val namePair = Pair(recipe_name_view as View, getString(R.string.name_transition))
                    val timePair = Pair(cook_time_view as View, getString(R.string.time_transition))
                    val tagsPair = Pair(tags_group as View, getString(R.string.tags_transition))
                    val ingredientsPair =
                        Pair(ingredients_list_layout as View, getString(R.string.ingredient_layout_transition))
                    val instructionsPair =
                        Pair(instructions_list_layout as View, getString(R.string.instruction_layout_transition))
                    val intent = getEditActivityIntent(id)
                    launch(Dispatchers.Main) {
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@ViewRecipeActivity,
                                                                                         namePair,
                                                                                         timePair,
                                                                                         tagsPair,
                                                                                         ingredientsPair,
                                                                                         instructionsPair)
                        startActivity(intent, options.toBundle())
                    }
                } else navigateToEditRecipeActivity(id)
            }
            (edit_recipe_button.drawable as Animatable).start()
        }
    }

    override fun onResume() {
        super.onResume()
        editButtonAnimatedVector = AnimatedVectorDrawableCompat.create(this, R.drawable.edit_blast_off)
        edit_recipe_button.setImageDrawable(editButtonAnimatedVector)
    }

    override fun populateViews(intent: Intent?) {
        intent?.apply {
            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.database_recipe_id))
                currentRecipe = recipePersistentData.fetchRecipe(id)
                val ingredients = currentRecipe.ingredientsJson
                val instructions = currentRecipe.instructionsJson
                val cookTime = if(currentRecipe.cookTime == 0) ""
                else getString(R.string.cook_time_format).format(currentRecipe.cookTime)
                launch(Dispatchers.Main) {
                    ingredientAdapter = populateIngredients(ingredients)
                    instructionAdapter = populateInstructions(instructions)
                    setTagViews(currentRecipe.tags)
                    recipe_name_view.text = currentRecipe.name
                    cook_time_view.text = cookTime
                    ingredients_list_view.adapter = ingredientAdapter
                    instructions_list_view.adapter = instructionAdapter
                    loading_panel.visibility = View.GONE
                    view_activity_content.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun setTagViews(tags: List<RecipeTag>) {
        val chipGroup = tags_group.apply { removeAllViews() }
        for(tag in tags) {
            val tagChip = tag.toChipView(this)
            tagChip.isCheckable = false
            chipGroup.addView(tagChip)
        }
    }

    override fun populateIngredients(ingredients: List<Ingredient>?): IngredientAdapter {
        if(ingredients.isNullOrEmpty())
            return IngredientAdapter(this, mutableListOf())
        return IngredientAdapter(this, ingredients.toMutableList())
    }

    override fun populateInstructions(instructions: List<Instruction>?): InstructionAdapter {
        if(instructions.isNullOrEmpty())
            return InstructionAdapter(this, mutableListOf())
        return InstructionAdapter(this, instructions.toMutableList())
    }
}
