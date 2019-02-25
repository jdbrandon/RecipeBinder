package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.os.Bundle
import com.jeffbrandon.recipebinder.R

class EditRecipeActivity : RecipeAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action!!

        if(action == Intent.ACTION_EDIT)
            setContentView(R.layout.activity_edit_recipe)
        else
            setContentView(/*TODO*/0) //View action

        val id = intent!!.extras!!.getInt(getString(R.string.database_recipe_id))
        val recipe = recipePersistantData.fetchRecipe(id)
        //populate view fields from recipe
    }
}