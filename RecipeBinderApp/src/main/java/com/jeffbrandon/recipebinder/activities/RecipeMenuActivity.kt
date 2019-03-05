package com.jeffbrandon.recipebinder.activities

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.size
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
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

    private lateinit var deferredMenuAdapter: Deferred<RecipeAdapter>

    private val recipeMenuAdapter: RecipeAdapter by lazy { runBlocking { deferredMenuAdapter.await() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        deferredMenuAdapter = async(Dispatchers.IO) {
            RecipeAdapter(this@RecipeMenuActivity,
                          recipePersistantData.fetchAllRecipes().toMutableList()
            )
        }
        setContentView(R.layout.activity_recipe_menu)
        setSupportActionBar(toolbar)
        setupNewRecipeButton()
        recipe_list_view.setOnItemClickListener { _, view, pos, id ->
            val nameView = view.findViewById<TextView>(R.id.recipe_name)
            if(pos < recipe_list_view.size) {
                val item = recipe_list_view[pos]
                val recipeId = item.findViewById<TextView>(R.id.id_view)
                val name = item.findViewById<TextView>(R.id.recipe_name)
                Timber.i("id: $id. dbID: ${recipeId.text}, ${name.text} clicked.")
                launch(Dispatchers.Default) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        nameView.transitionName = getString(R.string.name_transition)
                        navigateToViewRecipeActivity(recipeId.text.toString().toLong(),
                                                     nameView,
                                                     getString(R.string.name_transition))
                    } else
                        navigateToViewRecipeActivity(recipeId.text.toString().toLong())
                }
            } else Timber.d("$pos was out of bounds")
        }
        recipe_list_view.adapter = recipeMenuAdapter
        registerForContextMenu(recipe_list_view)
    }

    private fun setupNewRecipeButton() {
        fab.setOnClickListener {
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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.recipe_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when(item.itemId) {
            R.id.recipe_menu_delete -> {
                launch(Dispatchers.IO) {
                    recipePersistantData.deleteRecipe(recipeMenuAdapter.getItem(info.position))
                    launch(Dispatchers.Main) { recipeMenuAdapter.delete(info.position) }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
