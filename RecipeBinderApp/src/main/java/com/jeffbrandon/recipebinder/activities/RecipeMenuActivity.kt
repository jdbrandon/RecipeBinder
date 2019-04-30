package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.data.RecipeAdapter
import com.jeffbrandon.recipebinder.room.RecipeData
import kotlinx.android.synthetic.main.content_recipe_menu.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class RecipeMenuActivity : RecipeAppActivity(), Observer<List<RecipeData>> {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var deferredMenuAdapter: Deferred<RecipeAdapter>

    private val recipeMenuAdapter: RecipeAdapter by lazy { runBlocking { deferredMenuAdapter.await() } }

    private lateinit var recipeLiveData: LiveData<List<RecipeData>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeLiveData = recipePersistentData.fetchAllRecipes()
        recipeLiveData.observe(this, this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        deferredMenuAdapter = async(Dispatchers.IO) {
            RecipeAdapter(this@RecipeMenuActivity,
                          recipeLiveData.value?.toMutableList() ?: mutableListOf()
            )
        }
        setContentView(R.layout.activity_recipe_menu)
        setupNewRecipeButton()
        viewManager = LinearLayoutManager(this)
        recyclerView = recipe_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = recipeMenuAdapter
        }
        registerForContextMenu(recipe_recycler_view)
    }

    private fun setupNewRecipeButton() {
        add_recipe_button.setOnClickListener {
            //Open a Dialog to create a recipe
            val newRecipeDialogContent = View.inflate(this, R.layout.dialog_create_recipe, null)
            val input = newRecipeDialogContent.findViewById<EditText>(R.id.input_new_recipe_name)
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_new_recipe_title)
                .setView(newRecipeDialogContent)
                .setPositiveButton(R.string.create) { _, _ ->
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
                            r.id = recipePersistentData.insertRecipe(r)
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
                    recipePersistentData.deleteRecipe(recipeMenuAdapter.getRecipe(info.position))
                    launch(Dispatchers.Main) { recipeMenuAdapter.deleteRecipe(info.position) }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onChanged(data: List<RecipeData>) {
        recipeMenuAdapter.setDataSource(data)
    }
}
