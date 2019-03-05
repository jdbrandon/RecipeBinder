package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.View
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
        edit_recipe_button.setOnClickListener {
            launch(Dispatchers.Default) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    navigateToEditRecipeActivity(id, recipe_name_view, getString(R.string.name_transition))
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
            setContentView(R.layout.activity_view_recipe)
            launch(Dispatchers.IO) {
                id = extras!!.getLong(getString(R.string.database_recipe_id))
                currentRecipe = recipePersistantData.fetchRecipe(id)
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
        val chipGroup = tags_group
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
