package com.jeffbrandon.recipebinder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_create_recipe.*
import timber.log.Timber

class RecipeMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //Open a Dialog to create a recipe
            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_new_recipe_title)
                .setView(R.layout.dialog_create_recipe)
                .setPositiveButton(R.string.create) { dialog, which ->
                    val text = input_new_recipe_name.text.toString()
                    Timber.i("Creating a new recipe $text")
                    if(text.isNullOrEmpty()) {
                        Timber.d("Recipe needs a name")
                        //make a toast
                        Toast.makeText(this, R.string.toast_recipe_name, Toast.LENGTH_SHORT)
                        dialog.cancel()
                    } else {
                        val r = Recipe(text)
                        //add basic recipe to db and navigate to recipe editing activity
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
