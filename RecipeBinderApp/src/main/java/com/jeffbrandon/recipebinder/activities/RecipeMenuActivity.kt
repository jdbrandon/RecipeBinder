package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.get
import androidx.core.view.size
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.room.RecipeData
import kotlinx.android.synthetic.main.activity_recipe_menu.*
import kotlinx.android.synthetic.main.content_recipe_menu.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class RecipeMenuActivity : RecipeAppActivity() {

    private lateinit var deferredMenuAdapter: Deferred<ArrayAdapter<String>>

    private val recipeMenuAdapter: ArrayAdapter<String> by lazy { runBlocking { deferredMenuAdapter.await() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        deferredMenuAdapter = async(Dispatchers.IO) {
            ArrayAdapter(this@RecipeMenuActivity,
                         android.R.layout.simple_list_item_1,
                         recipePersistantData.fetchRecipeNames()
            )
        }
        setContentView(R.layout.activity_recipe_menu)
        setSupportActionBar(toolbar)

        setupNewRecipeButton()
        recipe_list_view.setOnItemClickListener { _, _, pos, id ->
            if(pos < recipe_list_view.size) {
                val item = recipe_list_view[pos] as AppCompatTextView
                Timber.i("id: $id ${item.text} clicked.")
                //TODO Open recipe view activity instead of edit
                launch(Dispatchers.Default) {
                    navigateToViewRecipeActivity(id + 1) //TODO: hackish need to fix
                }
            } else Timber.d("$pos was out of bounds")
        }
    }

    override fun onResume() {
        super.onResume()
        recipe_list_view.adapter = recipeMenuAdapter
    }

    private fun setupNewRecipeButton() {
        fab.setOnClickListener { _ ->
            //Open a Dialog to create a recipe
            val newRecipeDialogContent = View.inflate(this, R.layout.dialog_create_recipe, null)
            val input = newRecipeDialogContent.findViewById<EditText>(R.id.input_new_recipe_name)
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent)
                .setPositiveButton(R.string.create) { dialog, _ ->
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
                        recipeMenuAdapter.add(name)
                        launch(Dispatchers.IO) {
                            //add basic recipe to db
                            val r = RecipeData()
                            r.name = name
                            r.id = recipePersistantData.insertRecipe(r)
                            launch(Dispatchers.Default) {
                                navigateToEditRecipeActivity(r.id!!)
                            }
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    Timber.i("canceling recipe creation")
                    dialog.cancel()
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
