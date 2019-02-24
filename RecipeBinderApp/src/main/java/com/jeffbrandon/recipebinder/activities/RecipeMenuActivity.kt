package com.jeffbrandon.recipebinder.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import kotlinx.android.synthetic.main.activity_recipe_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RecipeMenuActivity : RecipeAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_menu)
        setSupportActionBar(toolbar)

        setupNewRecipeButton()
        recipeListView.setOnItemClickListener { parent, view, pos, id ->
            val item = recipeListView[pos] as AppCompatTextView
            Timber.i("id: $id ${item.text} clicked.")
        }
        updateRecipeAdapter()
    }

    private fun updateRecipeAdapter() {
        launch(Dispatchers.IO) {
            val nameArr = recipePersistantData.fetchRecipeNames()
            launch(Dispatchers.Main) {
                recipeListView.adapter =
                    ArrayAdapter<String>(this@RecipeMenuActivity, android.R.layout.simple_list_item_1, nameArr)
            }
        }
    }

    private fun setupNewRecipeButton() {
        fab.setOnClickListener { view ->
            //Open a Dialog to create a recipe
            val newRecipeDialogContent = View.inflate(this, R.layout.dialog_create_recipe, null)
            val input = newRecipeDialogContent.findViewById<EditText>(R.id.input_new_recipe_name)
            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent)
                .setPositiveButton(R.string.create) { dialog, which ->
                    dialog.cancel()
                    val name = input!!.text.toString()
                    Timber.i("Creating a new recipe: $name")
                    if(name.isEmpty()) {
                        Timber.d("Recipe needs a name")
                        //make a toast
                        Toast.makeText(
                            this,
                            R.string.toast_recipe_name, Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        launch(Dispatchers.IO) {
                            //add basic recipe to db
                            val r = RecipeData()
                            r.name = name
                            r.id = recipePersistantData.insertRecipe(r)
                            launch(Dispatchers.Main) {
                                updateRecipeAdapter()
                                navigateToEditRecipeActivity(r.id!!)
                            }
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, which ->
                    Timber.i("canceling recipe creation")
                    dialog.cancel()
                }
                .create()
            dialog.show()
        }
    }

    private fun navigateToEditRecipeActivity(id: Long) {
        val i = Intent("android.intent.action.ACTION_EDIT")
        i.putExtra(getString(R.string.database_recipe_id), id)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
