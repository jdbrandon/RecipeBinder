package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuActivityViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecipeMenuActivity : RecipeAppActivity() {
    @Inject lateinit var binder: RecipeMenuActivityViewBinder
    private val viewModel: RecipeMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_menu)
        findViewById<FragmentContainerView>(R.id.fragment_container)?.let { view ->
            binder.bind(view, viewModel, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.recipe_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_import -> binder.import()
            R.id.action_settings -> binder.settings()
            else -> super.onOptionsItemSelected(item)
        }
    }
}
