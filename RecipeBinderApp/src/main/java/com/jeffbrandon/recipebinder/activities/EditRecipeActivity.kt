package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.IngredientAdapter
import com.jeffbrandon.recipebinder.data.Instruction
import com.jeffbrandon.recipebinder.data.InstructionAdapter
import com.jeffbrandon.recipebinder.enums.UnitType
import com.jeffbrandon.recipebinder.room.RecipeData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.content_edit_recipe.*
import kotlinx.android.synthetic.main.dialog_add_ingredient.*
import kotlinx.android.synthetic.main.ingredient_list_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalArgumentException

class EditRecipeActivity : RecipeAppActivity() {

    private lateinit var currentRecipe: RecipeData
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val ingredientJsonAdapter =
        moshi.adapter<List<Ingredient>>(Types.newParameterizedType(List::class.java, Ingredient::class.java))
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var instructionAdapter: InstructionAdapter
    private val instructionJsonAdapter =
        moshi.adapter<List<Instruction>>(Types.newParameterizedType(List::class.java, Instruction::class.java))

    private val unitMap: HashMap<String, String> by lazy {
        hashMapOf(
            Pair(getString(R.string.cup), "c"),
            Pair(getString(R.string.ounce), "oz"),
            Pair(getString(R.string.table_spoon), "Tbsp"),
            Pair(getString(R.string.tea_spoon), "tsp"),
            Pair(getString(R.string.pint), "pt"),
            Pair(getString(R.string.quart), "qt"),
            Pair(getString(R.string.gallon), "gal"),
            Pair(getString(R.string.liter), "l"),
            Pair(getString(R.string.milliliter), "ml"),
            Pair(getString(R.string.pound), "lb"),
            Pair(getString(R.string.gram), "g")
        )
    }

    private val keyListener = { v: View -> quantity_input!!.text!!.append((v as MaterialButton).text) }
    private val unitListener = { v: View -> units_text_view.text = unitMap[(v as Chip).text] }
    private val fractionListener = { v: View ->
        val newText = (v as Chip).text
        if(fraction_input!!.text != newText)
            fraction_input.text = newText
        else fraction_input.text = ""
    }
    private val addIngredientListener = {
        val view = View.inflate(this, R.layout.dialog_add_ingredient, null)
        setupAddIngredientButtons()
        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.cancel()
                val amount = computeAmount(quantity_input.text.toString(), fraction_input.text)
                try {
                    val type = UnitType.fromString(units_text_view.text)
                    val newIngredient = Ingredient(ingredient_name.text.toString(), amount, type)
                    ingredientAdapter.add(newIngredient)
                } catch(e: IllegalArgumentException) {
                    Timber.e(e)
                }
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        setupButtonListeners()
    }

    override fun onNewIntent(i: Intent?) {
        super.onNewIntent(i)
        i?.apply {
            val action = extras!!.getString(getString(R.string.activity_edit_recipe_intent_mode),
                                            getString(R.string.view_mode_view))

            if(action == getString(R.string.view_mode_edit))
                setContentView(R.layout.activity_edit_recipe)
            else
                setContentView(/*TODO*/0) //View action

            val id = extras!!.getLong(getString(R.string.database_recipe_id))
            launch(Dispatchers.IO) {
                currentRecipe = recipePersistantData.fetchRecipe(id)
                recipe_name.setText(currentRecipe.name)
                cook_time.setText(if(currentRecipe.cookTime == 0) ""
                                  else currentRecipe.cookTime.toString())
                ingredientAdapter = populateIngredients(ingredientJsonAdapter.fromJson(currentRecipe.ingredientsJson))
                instructionAdapter =
                    populateInstructions(instructionJsonAdapter.fromJson(currentRecipe.instructionsJson))
                launch(Dispatchers.Main) {
                    ingredients_list_view.adapter = ingredientAdapter
                    instructions_list_view.adapter = instructionAdapter
                }
            }
        }
    }

    private fun setupButtonListeners() {
        add_ingredient_button.setOnClickListener { addIngredientListener }
        add_instruction_button.setOnClickListener { v ->
            layoutInflater.inflate(R.layout.dialog_create_recipe, findViewById(R.id.instructions_list_view), true)
        }
    }

    private fun computeAmount(whole: String, fraction: CharSequence?): Float {
        val n = whole.toInt()
        return n + when(fraction) {
            "1/4" -> 0.25f
            "1/3" -> 0.333f
            "1/2" -> 0.5f
            "2/3" -> 0.667f
            "3/4" -> 0.75f
            else -> 0.0f
        }
    }

    private fun setupAddIngredientButtons() {
        button_input_0.setOnClickListener { keyListener }
        button_input_1.setOnClickListener { keyListener }
        button_input_2.setOnClickListener { keyListener }
        button_input_3.setOnClickListener { keyListener }
        button_input_4.setOnClickListener { keyListener }
        button_input_5.setOnClickListener { keyListener }
        button_input_6.setOnClickListener { keyListener }
        button_input_7.setOnClickListener { keyListener }
        button_input_8.setOnClickListener { keyListener }
        button_input_9.setOnClickListener { keyListener }
        button_input_dot.setOnClickListener {
            if(quantity_input!!.text!!.contains('.'))
                return@setOnClickListener
            keyListener
        }
        button_input_delete.setOnClickListener {
            quantity_input!!.text!!.apply {
                if(length > 0) delete(lastIndex - 1, lastIndex)
            }
        }
        chip_input_quarter.setOnClickListener { fractionListener }
        chip_input_third.setOnClickListener { fractionListener }
        chip_input_half.setOnClickListener { fractionListener }
        chip_input_2_thirds.setOnClickListener { fractionListener }
        chip_input_3_quarter.setOnClickListener { fractionListener }

        cup_chip.setOnClickListener { unitListener }
        ounce_chip.setOnClickListener { unitListener }
        tbsp_chip.setOnClickListener { unitListener }
        tsp_chip.setOnClickListener { unitListener }
        pint_chip.setOnClickListener { unitListener }
        quart_chip.setOnClickListener { unitListener }
        gallon_chip.setOnClickListener { unitListener }
        liter_chip.setOnClickListener { unitListener }
        milliliter_chip.setOnClickListener { unitListener }
        pound_chip.setOnClickListener { unitListener }
        gram_chip.setOnClickListener { unitListener }
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