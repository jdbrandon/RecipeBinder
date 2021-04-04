package com.jeffbrandon.recipebinder.activities

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.viewbinding.RecipeMenuViewBinder
import com.jeffbrandon.recipebinder.viewmodel.RecipeMenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RecipeMenuActivity : NewRecipeAppActivity(), RecipeMenuViewBinder.ViewContract {
    private lateinit var viewBinder: RecipeMenuViewBinder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: RecipeMenuViewModel by viewModels()
        viewBinder = RecipeMenuViewBinder(vm, this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.recipe_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = viewBinder.selectedPosition()
        return when (item.itemId) {
            R.id.recipe_menu_delete -> position?.let { viewBinder.delete(it) } ?: true
            else -> super.onContextItemSelected(item)
        }
    }

    override fun registerContextMenu(v: View) {
        Timber.i("registering for context menu ${v.contentDescription}")
        registerForContextMenu(v)
    }

    override fun unregisterContextMenu(v: View) =
        unregisterForContextMenu(v)
}
